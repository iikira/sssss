package com.szzjcs.commons.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.szzjcs.commons.logutil.LogProxy;
import com.szzjcs.commons.util.FileUtil;
import com.szzjcs.commons.util.LogicUtil;

/**
 * key值要小心，只能是字母数字下划线,不可以是别的如圆点字符
 * 
 * @author smart
 */
public final class ProfileManager
{
    // 搞2P
    private static final class Pair<X, Y> implements Serializable
    {
        private static final long serialVersionUID = 4170711041659909233L;
        X x;
        Y y;

        Pair(X x, Y y)
        {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString()
        {
            return "Pair [x=" + x + ", y=" + y + "]";
        }
    }

    /**
     * 已加载的配置内容 当name=#..时，返回整个文件的内容 当tag==1时说明本配置项被使用过
     */
    private static Map<String/* file */, Map<String/* name */, Pair<String/* value */, Integer/* tag */>>> config = new ConcurrentHashMap<String, Map<String, Pair<String, Integer>>>();

    private static final Logger logger = LogProxy.getLogger(ProfileManager.class);

    public static final int MAX_FILE_SIZE = 10485760;

    private static Pattern pattern = Pattern.compile("^\\w+$");

    static
    {
        // 1. init profile root
        try
        {
            ProfileRoot.reInit();
        }
        catch (Throwable t)
        {
            System.err.println("init configcenter autoconf failed.");
            t.printStackTrace();
        }

        // 2. 猫屋暂时没配置中心,删掉此步骤了。connect to config center push service

        // 3. 全局性配置,猫屋用不到init global config

    }

    // 用法ProfileManager.getBoolByKey(maowu_app.isStaff,false)
    public static boolean getBoolByKey(String key, boolean defaultBool)
    {
        String resultValue = ProfileManager.getByKey(key);
        try
        {
            if ("true".equals(resultValue) || "false".equals(resultValue))
            {
                return Boolean.parseBoolean(resultValue);
            }
            logger.error("ProfileManager.getBoolByKey(" + key + "," + defaultBool + ")=" + resultValue
                    + ", return default " + defaultBool);
            return defaultBool;
        }
        catch (Throwable e)
        {
            logger.error("ProfileManager.getBoolByKey(" + key + "," + defaultBool + ")=" + resultValue, e);
            return defaultBool;
        }
    }

    /**
     * @param key
     * @return
     * @throws IndexOutOfBoundsException
     *         when key does not contains char '.'
     */
    public static String getByKey(String key) throws IndexOutOfBoundsException
    {
        try
        {
            if (key == null || key.isEmpty())
            {
                return null;
            }

            int index = key.indexOf('.');
            String file, name;
            if (index != -1)
            {
                file = key.substring(0, index);
                name = key.substring(index + 1);
            }
            else
            {
                return null;
            }

            Map<String, Pair<String, Integer>> map = config.get(file);
            if (map == null || map.isEmpty())
            {
                synchronized (config)
                {
                    map = config.get(file);
                    if (map == null || map.isEmpty())
                    {
                        map = loadFile(file);
                    }
                }
            }

            Pair<String, Integer> pair = map.get(name);
            if (pair == null)
            {
                return null;
            }

            pair.y = Integer.valueOf(1);
            return pair.x;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String getConfigsPath()
    {
        String path = ProfileRoot.AUTOCONF_PATH + "/configs";
        try
        {
            File file = new File(path);
            if (!file.exists())
            {
                if (!file.mkdirs())
                {
                    logger.error("ProfileManager: create " + path + " failed.");
                }
            }
        }
        catch (Throwable e)
        {
            logger.error("ProfileManager: create " + path + " failed.", e);
        }
        return path;
    }

    public static String getFile(String fileName) throws IOException
    {
        String raw = FileUtil.read(fileName);
        if (raw == null)
        {
            throw new IOException("File Not Found or IOException");
        }
        return Convertor.convertUnicodeToString(raw);
    }

    public static int getIntByKey(String key, int defaultInt)
    {
        String resultValue = ProfileManager.getByKey(key);
        try
        {
            return Integer.parseInt(resultValue);
        }
        catch (Throwable e)
        {
            logger.error("ProfileManager.getIntByKey(" + key + "," + defaultInt + ")=" + resultValue
                    + ", return default " + defaultInt, e);
            return defaultInt;
        }
    }

    public static long getLongByKey(String key, long defaultLong)
    {
        String resultValue = ProfileManager.getByKey(key);
        try
        {
            return Long.parseLong(resultValue);
        }
        catch (Throwable e)
        {
            logger.error("ProfileManager.getLongByKey(" + key + "," + defaultLong + ")=" + resultValue
                    + ", return default " + defaultLong, e);
            return defaultLong;
        }
    }

    public static String getpath()
    {
        return ProfileRoot.AUTOCONF_PATH;
    }

    public static String getStringByKey(String key, String defaultStr)
    {
        String resultValue = ProfileManager.getByKey(key);
        try
        {
            if (resultValue != null)
            {
                return resultValue;
            }
            logger.error("ProfileManager.getStringByKey(" + key + "," + defaultStr + ")=null, return default "
                    + defaultStr);
            return defaultStr;
        }
        catch (Throwable e)
        {
            logger.error("ProfileManager.getStringByKey(" + key + "," + defaultStr + ")=" + resultValue, e);
            return defaultStr;
        }
    }

    private static Map<String, Pair<String, Integer>> loadFile(String fileName)
    {
        Properties p = ProfileManager.parseFile(new File(ProfileRoot.AUTOCONF_PATH + '/' + fileName));
        Map<String, Pair<String, Integer>> map = new HashMap<String, Pair<String, Integer>>();
        if (p != null && !p.isEmpty())
        {
            for (@SuppressWarnings("rawtypes")
            Entry entry : p.entrySet())
            {
                String name = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (name.startsWith("#"))
                {
                    continue;
                }
                map.put(name, new Pair<String, Integer>(value, Integer.valueOf(0)));
            }

        }
        config.put(fileName, map);
        return map;
    }

    private static Properties parseFile(File f)
    {
        String content = FileUtil.read(f.getName());
        if (content == null)
        {
            return null;
        }
        // 检查配置文件的内容是否是key value方式
        BufferedReader reader = new BufferedReader(new StringReader(content));
        try
        {

            Map<String, String> validateMap = new HashMap<String, String>();
            String lineContent = null;
            while ((lineContent = reader.readLine()) != null)
            {

                // 忽略空行和注释行
                if (lineContent.equals("") || lineContent.startsWith("#") || lineContent.startsWith("\\#"))
                {
                    continue;
                }
                // 行不为空检查 key value
                if (lineContent.indexOf("=") <= 0)
                {
                    // 非key value格式的文件
                    System.out.println("ProfileManager info " + f.getName() + "\t the line is not key value format :["
                            + lineContent + "]");
                    return null;
                }
                int index = lineContent.indexOf("=");
                String key = Convertor.convertUnicodeToString(lineContent.substring(0, index));
                // 校验key
                Matcher matcher = pattern.matcher(key);
                if (!matcher.matches())
                {
                    System.out.println("ProfileManager info " + f.getName() + "\t the  key's format error :[" + key
                            + "]");
                    return null;
                }
                if (validateMap.containsKey(key))
                {
                    // 有相同的 key
                    System.out
                            .println("ProfileManager info " + f.getName() + "\t the  key is duplicate :[" + key + "]");
                    return null;
                }
                String value = Convertor.convertUnicodeToString(lineContent.substring(index + 1));
                // 没有相同的key
                validateMap.put(key, value);
            }

        }
        catch (Throwable e)
        {
            System.out.println("ProfileManager error " + f.getName() + "\t some error throw :[" + e.getMessage() + "]");
            e.printStackTrace(System.out);
            return null;
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        // 加载key value内容到内存
        try
        {
            reader = new BufferedReader(new StringReader(content));
            Properties p = new Properties();
            p.load(reader);
            return p;
        }
        catch (Exception e)
        {
            System.out.println("ProfileManager error " + f.getName() + "\t some error throw :[" + e.getMessage() + "]");
            e.printStackTrace(System.out);
            return null;
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通知本地文件有变更
     * 
     * @param configFile
     */
    public static void reInit(String configFile)
    {
        // 只重新加载已经加载过的文件。
        if (LogicUtil.isNotNullAndEmpty(config.get(configFile)))
        {
            loadFile(configFile);
        }
    }

}
