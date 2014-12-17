package com.szzjcs.commons.thirdapi.user;

import junit.framework.Assert;

import org.junit.Test;

import com.szzjcs.commons.thirdapi.user.impl.QQUserService;

public class QQUserServiceTest
{
    private QQUserService userService = new QQUserService();

    @Test
    public void testGetUid()
    {
        String uid = userService.getUid("30D6F8DB3452321E14D7FB749F6F8F01");
        Assert.assertNotNull(uid);
    }

    @Test
    public void testGetUser()
    {
        ThirdUser user = userService.getUser( "30D6F8DB3452321E14D7FB749F6F8F01");
        System.out.println(user);
        Assert.assertNotNull(user);
    }
}
