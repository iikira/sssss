package com.szzjcs.commons.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标志某个属性值是否要加入到CDATA标签中
 * 
 * @author: smartlv
 * @date 2014年2月12日 下午12:17:23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface XStreamCDATA
{

}
