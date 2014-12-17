package com.szzjcs.commons.thirdapi.opensearch;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;
import org.springframework.stereotype.Service;

@Service
@DisconfFile(filename = "opensearch.properties")
public class Config {
    private static String opensearch_key = "1i8hrmDVwCwjg8pT";
    private static String opensearch_secret = "lGkzO0TajGXD1pGXdim4re7wb5IkvW";
    private static String opensearch_host = "http://opensearch.aliyuncs.com";

    @DisconfFileItem(name = "opensearch_key")
    public static String getOpensearch_key() {
        return opensearch_key;
    }

    @DisconfFileItem(name = "opensearch_secret")
    public static String getOpensearch_secret() {
        return opensearch_secret;
    }

    @DisconfFileItem(name = "opensearch_host")
    public static String getOpensearch_host() {
        return opensearch_host;
    }
}
