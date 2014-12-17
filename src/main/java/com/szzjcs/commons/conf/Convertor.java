package com.szzjcs.commons.conf;

import com.szzjcs.commons.string.MultiStringReplacer;
import com.szzjcs.commons.string.StringUtil;

/**
 * 该类为配置文件刷新内部使用，其他人员禁止调用，否则后果自负
 * 
 * @author smartlv
 */
public class Convertor extends BaseConvertor
{

    /**
     * 把 html 输入的文本转换成 unicode，并在存入 mysql 之前进行转义替换
     * 
     * @param content
     * @return
     */
    public static String convertToUnicode(String content)
    {
        // 进行这个替换的原因是需要存入mysql，mysql会对这个再进行转义
        MultiStringReplacer replacer = new MultiStringReplacer();
        replacer.add("\\", "\\\\");
        replacer.add("'", "\\'");
        StringBuilder builder = new StringBuilder();
        String temp = content.replaceAll("\r\n", "\n");
        String[] contentArray = StringUtil.split(temp, "\n");
        for (String str : contentArray)
        {
            if (str.startsWith("#"))
            {
                builder.append(BaseConvertor.saveConvert(str, BaseConvertor.ESCAPEEMPTY)).append("\n");
            }
            else if (str.indexOf("=") > 0)
            {
                int index = str.indexOf("=");
                String key = str.substring(0, index + 1);
                String value = str.substring(index + 1);
                String result = key + BaseConvertor.saveConvert(value, BaseConvertor.ESCAPEEMPTY);
                builder.append(result).append("\n");
            }
            else
            {
                builder.append(BaseConvertor.saveConvert(str, BaseConvertor.ESCAPEEMPTY)).append("\n");
            }
        }
        String unicodeContent = builder.toString();
        return replacer.replace(unicodeContent);
    }

    /**
     * 把从数据库 load 出来的 unicode 字符转换成文本
     * 
     * @param unicodeContent
     * @return
     */
    public static String convertUnicodeToString(String unicodeContent)
    {
        char[] contentArray = unicodeContent.toCharArray();
        int length = contentArray.length;
        char[] convertBuffer = new char[length];
        String content = BaseConvertor.loadConvert(contentArray, 0, length, convertBuffer);
        return content;
    }

    public static String toHTMLString(String str, boolean needConvertBR)
    {
        if (str != null)
        {
            StringBuffer result = new StringBuffer(str.length());
            for (int i = 0; i < str.length(); i++)
            {
                char c = str.charAt(i);
                if (c == '\"')
                {
                    result.append("&quot;");
                }
                else if (c == '<')
                {
                    result.append("&lt;");
                }
                else if (c == '>')
                {
                    result.append("&gt;");
                }
                else if (c == '&')
                {
                    result.append("&amp;");
                }
                else if (c == '\n')
                {
                    if (needConvertBR)
                    {
                        result.append("<br/>");
                    }
                    else
                    {
                        result.append(c);
                    }
                }
                else
                {
                    result.append(c);
                }
            }
            str = result.toString();
        }
        return str;
    }

}
