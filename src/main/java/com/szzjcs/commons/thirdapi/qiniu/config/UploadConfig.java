package com.szzjcs.commons.thirdapi.qiniu.config;

import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

/**
 * 七牛云存储
 **/
@Service
@DisconfFile(filename = "upload.properties")
public class UploadConfig
{
    private static String accessKey = "wCqJ6WvbyNo0NlF4geFRGrUxjZFQeQtiSqlfF73r";
    private static String secretKey = "RPPZjNPmv2NFaERK2pbzCLCWA3xcMcRCMFXIVD8d";
    // 统一上传地址
    private static String uploadURL = "http://upload.qiniu.com/";


    // 用户中心资源
    private static String user_directory = "userprofile";
    private static String user_domain = "http://userprofile.qiniudn.com";

    // 路况资源
    private static String traffic_directory = "traffic-test";
    private static String traffic_domain = "http://traffic-test.qiniudn.com";

    // 星期八资源
    private static String weekend_directory = "ticket-file";
    private static String weekend_domain = "http://ticket-file.qiniudn.com";

    // 抢票资源
    private static String ticket_directory = "ticket-file";
    private static String ticket_domain = "http://ticket-file.qiniudn.com";

    // 圈子资源
    private static String circle_directory = "ticket-file";
    private static String circle_domain = "http://ticket-file.qiniudn.com";

    // 广告资源
    private static String adv_directory = "ticket-file";
    private static String adv_domain = "http://ticket-file.qiniudn.com";

    private static long expire_time = 60 * 60; // 单位为秒, 默认设置1小时吧

    @DisconfFileItem(name = "accessKey")
    public static String getAccessKey() {
        return accessKey;
    }

    @DisconfFileItem(name = "secretKey")
    public static String getSecretKey() {
        return secretKey;
    }

    @DisconfFileItem(name = "user_directory")
    public static String getUser_directory() {
        return user_directory;
    }

    @DisconfFileItem(name = "traffic_directory")
    public static String getTraffic_directory() {
        return traffic_directory;
    }

    @DisconfFileItem(name = "weekend_directory")
    public static String getWeekend_directory() {
        return weekend_directory;
    }

    @DisconfFileItem(name = "ticket_directory")
    public static String getTicket_directory() {
        return ticket_directory;
    }

    @DisconfFileItem(name = "circle_directory")
    public static String getCircle_directory() {
        return circle_directory;
    }

    @DisconfFileItem(name = "adv_directory")
    public static String getAdv_directory() {
        return adv_directory;
    }

    @DisconfFileItem(name = "upload_url")
    public static String getUploadURL() {
        return uploadURL;
    }

    @DisconfFileItem(name = "expire_time")
    public static long getExpire_time() {
        return expire_time;
    }

    @DisconfFileItem(name = "user_domain")
    public static String getUser_domain() {
        return user_domain;
    }

    @DisconfFileItem(name = "traffic_domain")
    public static String getTraffic_domain() {
        return traffic_domain;
    }

    @DisconfFileItem(name = "weekend_domain")
    public static String getWeekend_domain() {
        return weekend_domain;
    }

    @DisconfFileItem(name = "ticket_domain")
    public static String getTicket_domain() {
        return ticket_domain;
    }

    @DisconfFileItem(name = "circle_domain")
    public static String getCircle_domain() {
        return circle_domain;
    }

    @DisconfFileItem(name = "adv_domain")
    public static String getAdv_domain() {
        return adv_domain;
    }
}
