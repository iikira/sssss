package com.szzjcs.commons.thirdapi.user.impl;

import weiboclient4j.WeiboClient;
import weiboclient4j.WeiboClientException;
import weiboclient4j.model.User;
import weiboclient4j.params.Uid;

import com.szzjcs.commons.thirdapi.user.ThirdUser;
import com.szzjcs.commons.thirdapi.user.ThirdUserService;

/**
 * Weibo
 */
public class WeiboUserService implements ThirdUserService
{
    @Override
    public String getUid(String token)
    {
        WeiboClient client = new WeiboClient(token);
        try
        {
            return client.getAccountService().getUid() + "";
        }
        catch (WeiboClientException e)
        {
            throw new IllegalArgumentException("weibo token is invalid:" + token, e);
        }
    }

    @Override
    public ThirdUser getUser(String token)
    {
        WeiboClient client = new WeiboClient(token);
        try
        {
            String uid = getUid(token);
            User user = client.getUserService().show(new Uid(uid));
            ThirdUser _u = new ThirdUser();
            _u.setUid(uid);
            _u.setNickname(user.getScreenName());
            _u.setProfileImg(user.getProfileImageUrl());
            return _u;
        }
        catch (WeiboClientException e)
        {
            throw new IllegalArgumentException("weibo token is invalid:" + token, e);
        }
    }
}
