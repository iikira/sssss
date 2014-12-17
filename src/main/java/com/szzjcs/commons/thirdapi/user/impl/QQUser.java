package com.szzjcs.commons.thirdapi.user.impl;

import java.io.Serializable;

/**
 * QQ用户对象
 */
public class QQUser implements Serializable
{

    private static final long serialVersionUID = 1;
    private String uid;
    private String nickname;
    private String figureurl_qq_1;
    private String figureurl_qq_2;

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

    public String getFigureurl_qq_1()
    {
        return figureurl_qq_1;
    }

    public void setFigureurl_qq_1(String figureurl_qq_1)
    {
        this.figureurl_qq_1 = figureurl_qq_1;
    }

    public String getFigureurl_qq_2()
    {
        return figureurl_qq_2;
    }

    public void setFigureurl_qq_2(String figureurl_qq_2)
    {
        this.figureurl_qq_2 = figureurl_qq_2;
    }
}
