package com.szzjcs.commons.net.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.szzjcs.commons.string.StringUtil;
import com.szzjcs.commons.util.LogicUtil;

/**
 * URL中查询字符串转map的神器,支持urldecode和编码 此类线程可不安全,不要把本类当类或对象成员变量使用,当局部变量使用没问题
 * 
 * @author: smartlv
 * @date 2014年3月5日 上午10:24:23
 */
public class QryStrUtil
{
    private final Map<String, List<String>> paramValues = new HashMap<>();

    /**
     * 解析查询字符串，默认UrlEncode的utf-8编码格式
     * 
     * @param qryString
     *        查询字符串
     * @author: hualong
     */
    public QryStrUtil(String qryString)
    {
        this(qryString, "utf-8");
    }

    /**
     * 解析查询字符串，使用UrlEncode的指定编码格式
     * 
     * @param qryString
     *        查询字符串
     * @param charset
     *        字符编码
     * @author: hualong
     */
    public QryStrUtil(String qryString, String charset)
    {
        parseParameters(qryString, charset);
    }

    private void parseParameters(String qryString, String charset)
    {
        if (LogicUtil.isNullOrEmpty(qryString))
        {
            return;
        }

        String[] split = qryString.split("&");
        if (LogicUtil.isNullOrEmpty(split))
        {
            return;
        }

        for (String sp : split)
        {
            String kv[] = sp.split("=");
            if (LogicUtil.isNullOrEmpty(kv))
            {
                continue;
            }
            if (kv.length == 1)
            {
                addParameter(kv[0], "");
            }
            else
            {
                addParameter(kv[0], StringUtil.urlDecodeUTF8(kv[1]));
            }
        }
    }

    private void addParameter(String key, String value)
    {
        if (key == null)
        {
            return;
        }

        List<String> values = paramValues.get(key);
        if (values == null)
        {
            values = new ArrayList<>(1);
            paramValues.put(key, values);
        }

        values.add(value);
    }

    public String getParameter(String name)
    {
        List<String> values = paramValues.get(name);
        if (LogicUtil.isNullOrEmpty(values))
        {
            return null;
        }

        return values.get(0).trim();
    }

    public Map<String, List<String>> getParameterMap()
    {
        return paramValues;
    }

    public Set<String> getParameterNames()
    {
        return paramValues.keySet();
    }

    public String[] getParameterValues(String name)
    {
        List<String> values = paramValues.get(name);
        if (values == null)
        {
            return null;
        }

        return values.toArray(new String[values.size()]);
    }

    /**
     * 构建查询字符串，字符串中的值未经过url encode编码
     * 
     * @param map
     *        查询字符串中的参数与值
     * @return 查询字符串
     * @author: hualong
     * @date: 2014年5月14日下午12:02:19
     */
    public static String buildQryString(Map<String, String> map)
    {
        return buildQryString(map, null);
    }

    /**
     * 构建查询字符串，字符串中的值经过url encode编码
     * 
     * @param map
     *        查询字符串中的参数与值
     * @param charset
     * @return 查询字符串
     * @author: hualong
     * @date: 2014年5月14日下午12:18:37
     */
    public static String buildQryString(Map<String, String> map, String charset)
    {
        if (LogicUtil.isNullOrEmpty(map))
        {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        for (Entry<String, String> en : map.entrySet())
        {
            sb.append(en.getKey());
            sb.append("=");
            if (LogicUtil.isNullOrEmpty(charset))
            {
                sb.append(en.getValue());
            }
            else
            {
                sb.append(StringUtil.urlDecodeUTF8(en.getValue()));
            }
            sb.append("&");
        }

        return (sb.length() > 0) ? sb.substring(0, sb.length() - 1) : null;
    }

    public static void main(String[] args)
    {
        String url = "name=%E5%95%86%E5%93%81%E5%90%8D%E7%A7%B0&title=%E5%95%86%E5%93%81%E5%90%8D%E7%A7%B0&goodsCategoryId=1&defaultPic=   goodspic%2F10000%2F2014%2F03%2F05%2F10000%2F4BBDE34B62A2C435A4A9B2CB0126C734.jpg&defaultPic=goodspic%2F10000%2F2014%2F03%2F05%2F10000%2FB2C25D643145D8BF4D502055408B3CEE.jpg&goodsDesc=%E5%95%86%E5%93%81%E5%90%8D%E7%A7%B0&skuname1=%E5%95%86%E5%93%81%E5%90%8D%E7%A7%B0&skutitle1=%E5%95%86%E5%93%81%E5%90%8D%E7%A7%B0&marketPrice1=100&salesPrice1=100&inventory1=100&propName11=%E5%95%86%E5%93%81%E5%90%8D%E7%A7%B0&propValue11=%E5%95%86%E5%93%81%E5%90%8D%E7%A7%B0&propName21=&propValue21=&skudefaultPic1=goodspic%2F10000%2F2014%2F03%2F05%2F10000%2F5C7FCAA29D192EE2D1C1B41E55F2E98B.jpg&skudefaultPic1=goodspic%2F10000%2F2014%2F03%2F05%2F10000%2F638CB976D8A43F12210BEC8C7D20FEDD.jpg";
        QryStrUtil p = new QryStrUtil(url);
        String v = p.getParameter("defaultPic");
        System.out.println(v);

        // Map<String, String> map = new HashMap<String, String>();
        // map.put("121", "cccc");
        // map.put("122", "cccc");
        // map.put("123", "cccc");
        // map.put("124", "cccc");
        //
        // System.out.println(buildQryString(map));
    }
}
