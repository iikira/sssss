package com.szzjcs.commons.map;

import com.google.common.base.MoreObjects;

public class Point
{
    double longitude;
    double latitude;
    double x;
    double y;

    public Point(){

    }

    public Point(double longitude, double latitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getX()
    {
        return this.x;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getY()
    {
        return this.y;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this).addValue(latitude).addValue(longitude).toString();
    }
}
