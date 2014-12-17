package com.szzjcs.commons.jcache.adv;

/**
 * 默认的ItemHandler 直接把超时、淘汰的元素从cache中删除
 * 
 * @author smartlvchen
 * @param <K>
 * @param <V>
 */
public final class NullHandler<K, V> implements ItemHandler<K, V>
{
    public boolean handle(Node<K, V> n, boolean isTimeout)
    {
        return true;
    }
}
