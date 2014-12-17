package com.szzjcs.commons.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.szzjcs.commons.conf.FileUpdate;
import com.szzjcs.commons.conf.NotifyFileUpdate;
import com.szzjcs.commons.conf.ProfileManager;
import com.szzjcs.commons.string.StringUtil;

/**
 * 灰度发布的一个小工具 例如： 如果ProfileManager.getByKey( "gray_rel.qqsms" )=="deny;%100;468639448;3752767"； 表示缺省是拒绝，但是qq % 100 ==0
 * 或者qq号等于468639448,3752767例外 结果，isAllowedUser("qqsms",468639448)==true;
 * 
 * @author smartlv
 */
public class GrayReleaseUtil
{
    static class RuleChain
    {
        boolean defaultDeny = true;

        Map<RuleType, List<Object>> ruleLists = new HashMap<RuleType, List<Object>>();

        public boolean isAllowedUser(long refId)
        {
            if (defaultDeny)
            {
                for (Entry<RuleType, List<Object>> e : ruleLists.entrySet())
                {
                    if (e.getKey().isMatch(refId, e.getValue()))
                    {
                        return true;
                    }
                }
                return false;
            }
            for (Entry<RuleType, List<Object>> e : ruleLists.entrySet())
            {
                if (e.getKey().isMatch(refId, e.getValue()))
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString()
        {
            return "RuleChain [defaultDeny=" + defaultDeny + ", ruleLists=" + ruleLists + "]";
        }
    }

    enum RuleType
    {
        /**
         * 具体的一个号码类型，例如12345
         * 
         * @author nikodu
         */
        No
        {
            @Override
            boolean isMatch(long refId, List<Object> r)
            {
                for (Object object : r)
                {
                    if (refId == ((Long) object).longValue())
                    {
                        return true;
                    }
                }
                return false;
            }

            @Override
            Object paseValue(String v)
            {
                return Long.parseLong(v);
            }
        },
        /**
         * 取模规则类型，例如%5
         * 
         * @author nikodu
         */
        Mod
        {
            @Override
            boolean isMatch(long refId, List<Object> r)
            {
                for (Object object : r)
                {
                    if (refId % ((Long) object).longValue() == 0)
                    {
                        return true;
                    }
                }
                return false;

            }

            @Override
            Object paseValue(String v)
            {
                return Long.parseLong(v.substring(1));
            }
        },
        /**
         * 号码段类型，例如 12345-8546987
         * 
         * @author nikodu
         */
        NoSegment
        {
            @Override
            @SuppressWarnings("unchecked")
            boolean isMatch(long refId, List<Object> r)
            {

                for (Object object : r)
                {
                    Pair<Long, Long> p = (Pair<Long, Long>) object;
                    if (refId >= p.first && refId <= p.second)
                    {
                        return true;
                    }
                }
                return false;
            }

            @Override
            Object paseValue(String v)
            {
                String[] split = StringUtil.split(v, "-");
                if (split.length != 2)
                {
                    throw new RuntimeException("NoSegment Wrong format : " + v);
                }
                Pair<Long, Long> p = Pair.makePair(StringUtil.convertLong(split[0], Long.MIN_VALUE),
                        StringUtil.convertLong(split[1], Long.MIN_VALUE));

                if (p.first == Long.MIN_VALUE || p.second == Long.MIN_VALUE)
                {
                    throw new RuntimeException("NoSegment Wrong value : " + v);
                }
                return p;
            }
        };
        abstract boolean isMatch(long refId, List<Object> r);

        abstract Object paseValue(String v);
    }

    final static String CONFIG_FILE = "gray_rel";

    private static Map<String, String> bidMap = new ConcurrentHashMap<String, String>();

    /**
     * key是文件名，value： key是配置项的名称，value是规则
     */
    static Map<String, Map<String, RuleChain>> map = new HashMap<String, Map<String, RuleChain>>();

    static
    {
        bidMap.put("", CONFIG_FILE);
        NotifyFileUpdate.registInterface(new FileUpdate()
        {
            @Override
            public void updateFile(String name)
            {
                if (name.startsWith(CONFIG_FILE))
                {
                    // 哪儿个配置文件更新就清理掉哪儿个配置项
                    map.remove(name);
                }
            }
        });
    }

    /**
     * @param fileName
     *        格式是“gray_rel_bid”
     * @param keyName
     */
    static synchronized void initOneChain(String fileName, String keyName)
    {

        Map<String, RuleChain> bidMap = map.get(fileName);
        if (null != bidMap && bidMap.containsKey(keyName))
        {
            return;
        }
        if (null == bidMap)
        {
            bidMap = new HashMap<String, RuleChain>();
            map.put(fileName, bidMap);
        }
        String str = ProfileManager.getByKey(fileName + "." + keyName);
        // String str = "deny;%100;4686394482;88884168";
        if (str == null)
        {
            throw new RuntimeException("config file " + fileName + "." + keyName + " is not exist");
        }
        String[] arr = StringUtil.split(str, ";");
        if (arr == null || arr.length == 0)
        {
            throw new RuntimeException("config item " + keyName + " format error");
        }

        RuleChain chain = new RuleChain();

        String defaultAction = arr[0];
        if ("deny".equals(defaultAction))
        {
            chain.defaultDeny = true;
        }
        else if ("allow".equals(defaultAction))
        {
            chain.defaultDeny = false;
        }
        else
        {
            throw new RuntimeException("config item " + keyName + " format error," + defaultAction + " is unknown");
        }

        for (int i = 1; i < arr.length; i++)
        {
            try
            {
                String ruleStr = arr[i].trim();
                if (ruleStr.startsWith("%"))
                {
                    List<Object> list = chain.ruleLists.get(RuleType.Mod);
                    if (null == list)
                    {
                        list = new ArrayList<Object>();
                        chain.ruleLists.put(RuleType.Mod, list);
                    }

                    list.add(RuleType.Mod.paseValue(ruleStr));
                }
                else if (ruleStr.indexOf('-') != -1)
                {
                    List<Object> list = chain.ruleLists.get(RuleType.NoSegment);
                    if (null == list)
                    {
                        list = new ArrayList<Object>();
                        chain.ruleLists.put(RuleType.NoSegment, list);
                    }

                    list.add(RuleType.NoSegment.paseValue(ruleStr));
                }
                else
                {
                    List<Object> list = chain.ruleLists.get(RuleType.No);
                    if (null == list)
                    {
                        list = new ArrayList<Object>();
                        chain.ruleLists.put(RuleType.No, list);
                    }

                    list.add(RuleType.No.paseValue(ruleStr));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        bidMap.put(keyName, chain);
    }

    /**
     * 论坛、家族、聊天室等的灰度发布
     * 
     * @param name
     * @param refId
     * @return
     */
    public static boolean isAllowed(String name, long refId)
    {
        return isAllowed("", name, refId);
    }

    /**
     * 按照业务的bid到对应的灰度文件中查找灰度策略
     * 
     * @param bid
     *        灰度策略对应的业务的bid
     * @param name
     *        灰度策略的名称
     * @param refId
     * @return
     */
    public static boolean isAllowed(String bid, String name, long refId)
    {
        try
        {
            String fileName = bidMap.get(bid);
            if (null == fileName)
            {
                fileName = CONFIG_FILE + "_" + bid;
                bidMap.put(bid, fileName);

            }
            Map<String, RuleChain> bidMap = map.get(fileName);
            if (null == bidMap)
            {
                initOneChain(fileName, name);
                bidMap = map.get(fileName);
            }
            RuleChain chain = bidMap.get(name);
            if (null == chain)
            {
                initOneChain(fileName, name);
                bidMap = map.get(fileName);
            }
            chain = bidMap.get(name);
            return chain.isAllowedUser(refId);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            return false;
        }
    }

}
