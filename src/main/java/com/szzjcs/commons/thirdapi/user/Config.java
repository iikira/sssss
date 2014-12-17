package com.szzjcs.commons.thirdapi.user;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;
import org.springframework.stereotype.Service;

/**
 * 第三平台开发者配置项
 */
@Service("thirdUserConfig")
@DisconfFile(filename = "thirdpart_user.properties")
public class Config {
    private static String qq_openid = "1101765303";
    private static String qq_get_uid_url = "https://graph.qq.com/oauth2.0/me";
    private static String qq_get_user_url = "https://graph.qq.com/user/get_user_info";

    @DisconfFileItem(name = "qq_openid")
    public static String getQq_openid() {
        return qq_openid;
    }

    @DisconfFileItem(name = "qq_get_uid_url")
    public static String getQq_get_uid_url() {
        return qq_get_uid_url;
    }

    @DisconfFileItem(name = "qq_get_user_url")
    public static String getQq_get_user_url() {
        return qq_get_user_url;
    }
}
