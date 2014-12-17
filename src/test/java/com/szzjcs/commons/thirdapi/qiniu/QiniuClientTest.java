package com.szzjcs.commons.thirdapi.qiniu;

import org.junit.Test;

import static org.junit.Assert.*;

public class QiniuClientTest {

    @Test
    public void testGetUploadToken() throws Exception {

    }

    @Test
    public void testGetPublicResourceURL() throws Exception {

    }

    @Test
    public void testUploadLocalFile() throws Exception {
        String str = QiniuClient.uploadLocalFile(DirectoryEnum.USER_PROFILE, "F:\\tony\\SmsInterface.wsdl");
        System.out.println(str);
    }

    @Test
    public void testUploadRemoteFile() throws Exception {
        String str = QiniuClient.uploadRemoteFile(DirectoryEnum.USER_PROFILE, "http://tp1.sinaimg.cn/1868283432/180/5635127861/1", "haha.tony");
        System.out.println(str);
    }

    @Test
    public void testGetAviInfo(){
        System.out.println(QiniuClient.getAviInfo("http://traffic-test.qiniudn.com/5461222360918224896"));
    }
}