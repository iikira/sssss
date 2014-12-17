package com.szzjcs.commons.thirdapi.sms;

import com.szzjcs.commons.thirdapi.sms.provider.iems.IemsProvider;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SmsUtilTest
{
    @Test
    public void testSendSms(){
        try {
//            ApplicationContext context = new ClassPathXmlApplicationContext("spring-main.xml");
            IemsProvider provider = new IemsProvider();
            provider.send("13714896419", "您的验证码为7899");
//            SmsUtil.sendSMS("13114896419", "您的验证码为7899");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
