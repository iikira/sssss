package com.szzjcs.commons.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 经纬度和距离换算
 * 
 * @author: hualong
 * @date 2014年2月12日 下午2:00:12
 */
public class LonLatUtil
{
    private static final double PI = 3.14159265;
    private static final double RAD = Math.PI / 180.0;
    private static final double EARTH_RADIUS = 6378137;

    /**
     * 经纬度度分秒转换为小数
     * 
     * @param du
     *        度
     * @param fen
     *        分
     * @param miao
     *        秒
     * @return
     * @date: 2014年3月3日上午10:53:37
     */
    public static double convertToDecimal(double du, double fen, double miao)
    {
        if (du < 0)
        {
            return -(Math.abs(du) + (Math.abs(fen) + (Math.abs(miao) / 60)) / 60);
        }

        return Math.abs(du) + (Math.abs(fen) + (Math.abs(miao) / 60)) / 60;
    }

    /**
     * 将千米转换成米
     * 
     * @param distance
     * @return
     * @date: 2014年2月24日下午3:19:51
     */
    public static String kmToM(double distance)
    {
        if (distance <= 0)
        {
            throw new RuntimeException("距离不合法！");
        }
        return String.format("%.2f", distance / 1000);
    }

    /**
     * 计算指定圆心经纬度和半径后经纬度范围，该范围值正好为圆的圆外切正方形，正方形各顶点位置与中心位置距离超过半径
     * 
     * @param lat
     *        纬度
     * @param lon
     *        经度
     * @param radius
     *        半径，单位是米
     * @return 经纬度范围，数组类型长度为4：最小经度，最小纬度，最大经度，最大纬度
     * @author: hualong
     * @date: 2014年2月12日下午5:17:19
     */
    public static double[] getAround(double lon, double lat, double raidus)
    {
        double degree = (24901 * 1609) / 360.0;
        double radiusLat = (1 / degree) * raidus;
        double minLat = lat - radiusLat;
        double maxLat = lat + radiusLat;

        double mpdLon = degree * Math.cos(lat * (PI / 180));
        double radiusLon = (1 / mpdLon) * raidus;
        double minLon = lon - radiusLon;
        double maxLon = lon + radiusLon;

        return new double[] { minLon, minLat, maxLon, maxLat };
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米。
     * 
     * @param lon1
     *        经度
     * @param lat1
     *        纬度
     * @param lon2
     *        经度
     * @param lat2
     *        纬度
     * @return 两点间的距离
     */
    public static double getDistance(double lon1, double lat1, double lon2, double lat2)
    {
        double radLat1 = lat1 * RAD;
        double radLat2 = lat2 * RAD;

        double a = radLat1 - radLat2;
        double b = lon1 * RAD - lon2 * RAD;

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;

        return s;
    }

    public static void a()
    {
        List<String> l = new ArrayList<String>();
        l.add("111");
        l.add("222");

        // for (Iterator i = l.iterator(); i.hasNext();)
        // {
        // if (i.next().equals("222"))
        // {
        // i.remove();
        // }
        //
        // }
        for (int i = 0; i < l.size(); i++)
        {
            String s = l.get(i);
            if (s.equals("111"))
            {
                l.remove(s);
            }
        }
        System.out.println(l);
    }

    public static void main(String[] args)
    {
        // double lon1 = 113.989563;
        // double lat1 = 22.594357;
        //
        // int radius = 5000;
        // double arr[] = getAround(lon1, lat1, radius);
        // System.out.println(arr[0] + "," + arr[1] + "," + arr[2] + "," + arr[3]);

        // arr = getAround(lat1, lon1, radius);
        // [34.25566276027792,108.94186385411045,34.27363323972208,108.96360814588955]
        // System.out.println(arr[0] + "," + arr[1] + "," + arr[2] + "," + arr[3]);

        // 911717.0 34.264648,108.952736,39.904549,116.407288
        // double dis = getDistance(lon1, lat1, arr[2], arr[3]);
        // System.out.println(dis);

        // System.out.println(convertToDecimal(22, 35, 41));
        // System.out.println(convertToDecimal(113, 59, 23));

        a();
    }

}
