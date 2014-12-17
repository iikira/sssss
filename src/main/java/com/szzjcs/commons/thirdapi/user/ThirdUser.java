package com.szzjcs.commons.thirdapi.user;

import java.io.Serializable;

import com.google.common.base.MoreObjects;

/**
 * 第三方用户信息
 */
public class ThirdUser implements Serializable
{

    private static final long serialVersionUID = 9114425617587340852L;
    // 用户id
    private String uid;
    // 用户昵称
    private String nickname;
    // 用户图像
    private String profileImg;

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getProfileImg()
    {
        return profileImg;
    }

    public void setProfileImg(String profileImg)
    {
        this.profileImg = profileImg;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this).add("uid", uid).add("nickname", nickname).add("img", profileImg)
                .toString();
    }
}
