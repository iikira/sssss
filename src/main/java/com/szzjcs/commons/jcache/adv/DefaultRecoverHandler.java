package com.szzjcs.commons.jcache.adv;

public final class DefaultRecoverHandler<K, V> implements RecoverHandler<K, V>
{
    public boolean handle(Node<K, V> n)
    {
        return true;
    }
}
