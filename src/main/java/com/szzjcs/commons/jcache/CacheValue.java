package com.szzjcs.commons.jcache;

import java.io.Serializable;

@SuppressWarnings("serial")
final class CacheValue<V> implements Serializable
{
    long time;
    V value;
    int memoryUsage;
}
