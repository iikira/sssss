package com.szzjcs.commons.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import com.szzjcs.commons.json.JsonConverter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * XStream把xml和bean相互转换
 * 
 * @author: smartlv
 * @date 2014年2月12日 上午11:04:01
 */
public class XStreamUtil
{
    private static final Logger logger = LoggerFactory.getLogger(JsonConverter.class);

    public static XStream createXstream(boolean isAddCDAT)
    {
        if (isAddCDAT)
        {
            return new XStream(new XppDriver()
            {
                @Override
                public HierarchicalStreamWriter createWriter(Writer out)
                {
                    return new PrettyPrintWriter(out)
                    {
                        boolean cdata = false;
                        Class<?> targetClass = null;

                        @Override
                        @SuppressWarnings("rawtypes")
                        public void startNode(String name, Class clazz)
                        {
                            super.startNode(name, clazz);
                            // 业务处理，对于用XStreamCDATA标记的Field，需要加上CDATA标签
                            if (!name.equals("xml"))
                            {// 代表当前处理节点是class，用XstreamAlias把class的别名改成xml，下面的代码片段有提到
                                if (!ClassUtils.isPrimitiveOrWrapper(clazz))
                                {
                                    targetClass = clazz;
                                }
                                cdata = needCDATA(targetClass, name);
                            }
                            else
                            {
                                targetClass = clazz;
                            }
                        }

                        @Override
                        protected void writeText(QuickWriter writer, String text)
                        {
                            if (cdata)
                            {
                                writer.write("<![CDATA[");
                                writer.write(text);
                                writer.write("]]>");
                            }
                            else
                            {
                                writer.write(text);
                            }
                        }
                    };
                }
            });
        }
        else
        {
            return new XStream();
        }
    }

    // bean 转换成xml,支持自动检测注解
    public static String toXML(Object result, boolean isAddCDAT)
    {
        if (result == null)
        {
            return null;
        }

        XStream xstream = createXstream(isAddCDAT);

        xstream.setMode(XStream.NO_REFERENCES);
        xstream.autodetectAnnotations(true);
        xstream.alias("root", result.getClass());

        return xstream.toXML(result);
    }

    /**
     * 将传入xml文本转换成Java对象
     * 
     * @param xmlStr
     * @param cls
     *        xml对应的class类
     * @return T xml对应的class类的实例对象
     */
    public static <T> T readXml2Bean(String xmlStr, Class<T> cls)
    {
        XStream xstream = new XStream(new DomDriver());
        xstream.processAnnotations(cls);
        @SuppressWarnings("unchecked")
        T obj = (T) xstream.fromXML(xmlStr);

        return obj;
    }

    public static Object readXml2Object(String xml)
    {
        XStream xstream = new XStream();
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.autodetectAnnotations(true);

        return xstream.fromXML(xml);
    }

    @SuppressWarnings("unchecked")
    public static <T> T readXml2Bean(InputStream xmlInput, Class<T> cls)
    {
        XStream xstream = new XStream(new DomDriver());
        xstream.processAnnotations(cls);
        T obj = (T) xstream.fromXML(xmlInput);

        return obj;
    }

    @SuppressWarnings("unchecked")
    public static <T> T toBeanFromFile(String absPath, String fileName, String encode, Class<T> cls)
    {
        String filePath = absPath + fileName;
        T obj = null;
        try (InputStream ins = new FileInputStream(new File(filePath)))
        {
            XStream xstream = new XStream(new DomDriver(encode));
            xstream.processAnnotations(cls);
            try
            {
                obj = (T) xstream.fromXML(ins);
            }
            catch (Exception e)
            {
                logger.error("解析{" + filePath + "}文件失败！", e);
            }
        }
        catch (Exception e)
        {
            logger.error("读{" + filePath + "}文件失败！", e);
        }

        return obj;
    }

    // map转XML根据val是否数字加CDATA
    public static String mapToXml(HashMap<String, String> map)
    {
        String xml = "<xml>";

        Iterator<Entry<String, String>> iter = map.entrySet().iterator();
        while (iter.hasNext())
        {
            Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String val = entry.getValue();
            if (StringUtils.isNumeric(val))
            {
                xml += "<" + key + ">" + val + "</" + key + ">";
            }
            else
            {
                xml += "<" + key + "><![CDATA[" + val + "]]></" + key + ">";
            }
        }

        xml += "</xml>";
        return xml;
    }

    private static boolean existsCDATA(Class<?> clazz, String fieldAlias)
    {
        // scan fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields)
        {
            // 1. exists XStreamCDATA
            if (field.getAnnotation(XStreamCDATA.class) != null)
            {
                XStreamAlias xStreamAlias = field.getAnnotation(XStreamAlias.class);
                // 2. exists XStreamAlias
                if (null != xStreamAlias)
                {
                    if (fieldAlias.equals(xStreamAlias.value()))
                    {
                        return true;
                    }
                }
                else
                {// not exists XStreamAlias
                    if (fieldAlias.equals(field.getName()))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean needCDATA(Class<?> targetClass, String fieldAlias)
    {
        boolean cdata = false;
        // first, scan self
        cdata = existsCDATA(targetClass, fieldAlias);
        if (cdata)
        {
            return cdata;
        }
        // if cdata is false, scan supperClass until java.lang.Object
        Class<?> superClass = targetClass.getSuperclass();
        while (!superClass.equals(Object.class))
        {
            cdata = existsCDATA(superClass, fieldAlias);
            if (cdata)
            {
                return cdata;
            }
            superClass = superClass.getClass().getSuperclass();
        }
        return false;
    }
}
