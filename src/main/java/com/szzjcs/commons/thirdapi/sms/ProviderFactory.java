package com.szzjcs.commons.thirdapi.sms;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.szzjcs.commons.thirdapi.sms.config.SmsConfig;

public class ProviderFactory
{
    private static final Map<String, Provider> provider_cache = new HashMap<>();
    private static final Lock lock = new ReentrantLock();

    public static Provider getProvider(String name)
    {
        Provider provider = provider_cache.get(name);
        if (provider == null)
        {
            try
            {
                lock.lock();
                if (provider == null)
                {
                    provider = (Provider) Class.forName(name).newInstance();
                    provider_cache.put(name, provider);
                }
            }
            catch (Exception e)
            {
                throw new SmsException("provider name is invalid:" + name, e);
            }
            finally
            {
                lock.unlock();
            }
        }
        return provider;
    }

    public static Provider getDefaultProvider()
    {
        String defaultProvider = SmsConfig.getProvider();
        return getProvider(defaultProvider);
    }
}
