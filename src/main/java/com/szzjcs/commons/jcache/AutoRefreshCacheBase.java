/**
 * 
 */
package com.szzjcs.commons.jcache;

import com.szzjcs.commons.jcache.monitor.Visitable;

/**
 * @author bingyi
 */
public abstract class AutoRefreshCacheBase<K, V> implements Cache<K, V>, Visitable
{

    abstract public long getReflushTime();

    public abstract String getReflushingStatus();

}
