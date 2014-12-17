package com.szzjcs.commons.thirdapi.opensearch;

import java.util.HashMap;
import java.util.Map;

import com.opensearch.javasdk.CloudsearchClient;
import com.opensearch.javasdk.object.KeyTypeEnum;

/**
 * 阿里云搜索客户端
 */
public class CloundSearchClient
{
    private static CloudsearchClient client;
    static
    {
        String accesskey = Config.getOpensearch_key();
        String secret = Config.getOpensearch_secret();

        Map<String, Object> opts = new HashMap<>();
        opts.put("host", Config.getOpensearch_host());
        client = new CloudsearchClient(accesskey, secret, opts, KeyTypeEnum.ALIYUN);
    }

    public static CloudsearchClient getClient()
    {
        return client;
    }

    private CloundSearchClient()
    {
    }
}
