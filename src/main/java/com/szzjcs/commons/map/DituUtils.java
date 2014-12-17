package com.szzjcs.commons.map;

import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.util.VincentyGeodesy;

/**
 * 地图相关工具方法
 */
public abstract class DituUtils
{
    private DituUtils()
    {
    }

    /**
     * WGS坐标转成GCJ中国火星坐标
     */
    public static Point wgs2gcj(Point point)
    {
        // 只有中国区域范围内的才做转换
        if (point.getLongitude() < 73.33 || point.getLongitude() > 135.05 || point.getLatitude() < 3.51
                || point.getLatitude() > 53.33)
        {
            return point;
        }
        WGS84ToGCJ02 convert = new WGS84ToGCJ02();
        return convert.getEncryPoint(point.getLongitude(), point.getLatitude());
    }

    /**
     * 计算两点之间的距离, 两个点使用的坐标系可以随便, 但是使用的是相同的坐标系.
     * 
     * @return 两点距离, 以m(米)为单位
     */
    public static double distanceInMeters(Point point1, Point point2)
    {
        WGS84Point _p1 = new WGS84Point(point1.getLatitude(), point1.getLongitude());
        WGS84Point _p2 = new WGS84Point(point2.getLatitude(), point2.getLongitude());
        return VincentyGeodesy.distanceInMeters(_p1, _p2);
    }

}
