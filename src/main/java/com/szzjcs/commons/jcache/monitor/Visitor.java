package com.szzjcs.commons.jcache.monitor;

import com.szzjcs.commons.jcache.AutoRefreshCacheBase;
import com.szzjcs.commons.jcache.Cache;
import com.szzjcs.commons.jcache.adv.AdvAutoSaveCache;
import com.szzjcs.commons.jcache.adv.AdvCache;

public interface Visitor
{

    void visitAutoRefreshCache(AutoRefreshCacheBase<?, ?> cache, String name);

    void visitAdvAutoSaveCache(AdvAutoSaveCache<?, ?> cache, String name);

    void visitAdvCache(AdvCache<?, ?> cache, String name);

    void visitCustomCache(Cache<?, ?> cache, String name);
}
