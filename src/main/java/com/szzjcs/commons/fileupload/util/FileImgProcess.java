package com.szzjcs.commons.fileupload.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.slf4j.Logger;

import com.szzjcs.commons.common.Constants;
import com.szzjcs.commons.logutil.LogProxy;
import com.szzjcs.commons.util.LogicUtil;

/**
 * @author: smartlv
 * @date 2014年3月4日 上午10:28:13
 */
public class FileImgProcess
{
    private static final Logger log = LogProxy.getLogger(FileImgProcess.class);

    /**
     * 图片类型
     */
    public static final String IMAGE_TYPE = "jpg, png, gif, bmp, jpeg";

    /**
     * flash类型
     */
    public static final String FLASH_TYPE = "swf,flv";

    /**
     * 视频类型
     */
    public static final String VEDIO_TYPE = "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb";

    /**
     * 根据文件名判断是否为图片
     * 
     * @param fileName
     *        文件名
     * @return 是图片为true，不是为false
     * @date: 2014年1月6日上午10:11:46
     */
    public static boolean isImg(String fileName)
    {
        if (LogicUtil.isNullOrEmpty(fileName))
        {
            return false;
        }

        String extName = FileSavingUtils.getExtName(fileName);
        if (LogicUtil.isNullOrEmpty(extName))
        {
            return false;
        }

        for (String type : IMAGE_TYPE.split(Constants.COMMA))
        {
            if (extName.equalsIgnoreCase(type))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否flash
     * 
     * @param fileName
     * @return
     * @date: 2014年1月7日上午10:31:34
     */
    public static boolean isFlash(String fileName)
    {
        if (LogicUtil.isNullOrEmpty(fileName))
        {
            return false;
        }

        String extName = FileSavingUtils.getExtName(fileName);
        if (LogicUtil.isNullOrEmpty(extName))
        {
            return false;
        }

        for (String type : FLASH_TYPE.split(Constants.COMMA))
        {
            if (extName.equalsIgnoreCase(type))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否为视频
     * 
     * @param fileName
     * @return
     * @date: 2014年1月7日上午10:31:42
     */
    public static boolean isVedio(String fileName)
    {
        if (LogicUtil.isNullOrEmpty(fileName))
        {
            return false;
        }

        String extName = FileSavingUtils.getExtName(fileName);
        if (LogicUtil.isNullOrEmpty(extName))
        {
            return false;
        }

        for (String type : VEDIO_TYPE.split(Constants.COMMA))
        {
            if (extName.equalsIgnoreCase(type))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * 根据给定文件路径（含文件新名称）保存源文件
     * 
     * @param newFilePath
     *        文件的新保存路径(含文件名)
     * @param inputStream
     *        源文件的输入流
     * @return 是否保存成功：成功为true，失败为false
     * @date: 2014年1月6日上午10:26:51
     */
    public static boolean saveFile(String newFilePath, InputStream inputStream)
    {
        return FileSavingUtils.writeFileToDisc(new File(newFilePath), inputStream);
    }

    /**
     * 图片裁剪
     * 
     * @param originalImgPath
     *        源图片路径
     * @param targetImgPath
     *        新图片路径
     * @param position
     *        位置 0正中间，1中间左边，2中间右边，3底部中间，4底部左边，5底部右边，6顶部中间，7顶部左边，8顶部右边， 其他为默认正中间
     * @param width
     *        裁剪宽度
     * @param height
     *        裁剪高度
     * @throws Exception
     * @date: 2013-8-29下午07:04:38
     */
    public static void cropImg(String originalImgPath, String targetImgPath, int position, int width, int height)
    {
        try
        {
            ImageThumbUtils.crop(originalImgPath, position, width, height, targetImgPath);
        }
        catch (Exception e)
        {
            log.error("crop image error", e);
        }
    }

    /**
     * 给图片添加水印
     * 
     * @param originalImgPath
     *        将被添加水印图片 路径
     * @param watermarkImgPath
     *        水印图片路径
     * @param position
     *        位置 0正中间，1中间左边，2中间右边，3底部中间，4底部左边，5底部右边，6顶部中间，7顶部左边，8顶部右边， 其他为默认正中间
     * @param opacity
     *        不透明度,0完全透明，1完全不透明
     * @param targetImgPath
     *        含有水印的新图片路径
     * @throws Exception
     * @author: Hualong
     * @date: 2013-8-29下午07:00:00
     */
    public static void watermarkImg(String originalImgPath, String watermarkImgPath, int position, float opacity,
            String targetImgPath)
    {
        try
        {
            ImageThumbUtils.watermark(originalImgPath, watermarkImgPath, position, opacity, targetImgPath);
        }
        catch (Exception e)
        {
            log.error("crop image error", e);
        }
    }

    /**
     * 根据给定的图片路径（含图片新名称）保存源图及缩略图
     * 
     * @param originalImgNewSavingPath
     *        图片的新存储路径（含图片新名称）
     * @param thumbSizes
     *        缩略尺寸
     * @param originalImgInputStream
     *        源图片的输入流，此参数放在函数声明的最后，因为hessian的原因
     * @return 是否保存成功：成功为true，失败为false
     * @date: 2014年1月6日上午10:12:16
     */
    public static boolean thumbingAndSavingAllImgs(String originalImgSavingPath, int[][] thumbSizes,
            InputStream originalImgInputStream)
    {
        try
        {
            // 保存源图
            FileSavingUtils.writeFileToDisc(new File(originalImgSavingPath), originalImgInputStream);

            // 保存缩略图
            String targetDir = FileSavingUtils.getDirPath(originalImgSavingPath);
            for (int i = 0; i < thumbSizes.length; i++)
            {
                int[] wh = thumbSizes[i];

                String targetImgPath = targetDir
                        + FileSavingUtils.getThumbImgName(originalImgSavingPath, wh[0] + Constants.X + wh[1]);
                ImageThumbUtils.thumbImage(originalImgSavingPath, wh[0], wh[1], targetImgPath);
            }
            return true;

        }
        catch (Exception e)
        {
            log.error("Image thumbing failed.", e);
        }

        return false;
    }

    public static void main(String[] args) throws Exception
    {
        int thumbsize[][] = { { 10, 10 }, { 20, 20 } };
        new FileImgProcess();

        InputStream is = new FileInputStream("C:/Users/Public/Pictures/Sample Pictures/Desert.jpg");
        FileSavingUtils.getNewFileName("C:/Users/Public/Pictures/Sample Pictures/Desert.jpg");
        System.out.println(thumbsize);
        FileImgProcess
                .thumbingAndSavingAllImgs(
                        "D:/Eclipse/workspace/ddpasystem/ddpa-view/target/ddpa-view/userfile/0/0/0/0/24/B937AFB491AE4ECD13D6BA301910CB17.jpg",
                        thumbsize, is);
    }
}
