package com.szzjcs.commons.fileupload;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.szzjcs.commons.fileupload.sftp.SftpFileTransfer;
import com.szzjcs.commons.fileupload.util.FileSavingUtils;
import com.szzjcs.commons.util.LogicUtil;

/**
 * 文件重发器：对未上传成功的图片重新上传到图片服务器
 * 
 * @author: Hualong
 * @date 2014年1月3日 上午9:45:10
 */
public class FileResender
{
    private final Logger log = LoggerFactory.getLogger(FileResender.class);

    private SftpFileTransfer sftpFileTransfer;

    /**
     * @param sftpFileTransfer
     *        sftp文件传输器
     */
    public void setSftpFileTransfer(SftpFileTransfer sftpFileTransfer)
    {
        this.sftpFileTransfer = sftpFileTransfer;
    }

    /**
     * @return sftp文件传输器
     * @date: 2014年1月13日上午9:57:27
     */
    public SftpFileTransfer getSftpFileTransfer()
    {
        return sftpFileTransfer;
    }

    /**
     * 获取连接
     * 
     * @date: 2014年1月6日下午3:01:41
     */
    public void getConnection()
    {
        if (LogicUtil.isNotNull(sftpFileTransfer))
        {
            sftpFileTransfer.getConnection();
        }
    }

    /**
     * 关闭连接
     * 
     * @date: 2014年1月6日下午3:02:35
     */
    public void closeConnection()
    {
        if (LogicUtil.isNotNull(sftpFileTransfer))
        {
            sftpFileTransfer.closeConnection();
        }
    }

    /**
     * 是否需要删除指定文件（不含目录）
     * 
     * @param localBaseDirPath
     *        本地文件所在基本目录路径
     * @param localFileRelativePath
     *        本地文件相对路径
     * @param remoteBaseDirPath
     *        远程存储文件所在基本目录径路
     * @return 需要删除为true，无需为false
     * @date: 2014年1月3日上午11:21:03
     */
    private boolean isNeedResend(String localBaseDirPath, String localFileRelativePath, String remoteBaseDirPath)
    {
        File localFile = new File(localBaseDirPath + localFileRelativePath);
        if (localFile.exists())
        {
            long localFileSize = localFile.length();
            long remoteFileSize = sftpFileTransfer.getFileSize(remoteBaseDirPath, localFileRelativePath);
            if (localFileSize == remoteFileSize)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 重发目录
     * 
     * @param localDirPath
     *        本地目录路径
     * @param remoteDirPath
     *        远程目录路径
     * @date: 2014年1月3日上午10:35:26
     */
    public void resendDir(File localDir, String remoteDirPath)
    {
        try
        {
            if (localDir.isDirectory())
            {
                File[] fs = localDir.listFiles();
                if (LogicUtil.isNotNullAndEmpty(fs))
                {
                    for (File f : fs)
                    {

                        if (f.isFile())
                        {// 如果是文件
                            resendFile(localDir.getCanonicalPath(), f.getName(), remoteDirPath);

                        }
                        else
                        {// 如果是目录,远程目录路径需新建以免递归父目录时使用子目录的路径
                            String newRemoteDirPath = FileSavingUtils.formatDirPath(remoteDirPath) + f.getName();
                            resendDir(f, newRemoteDirPath);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("resend dir is error", e);
        }
    }

    /**
     * 重发一个文件
     * 
     * @param localBaseDirPath
     *        本地文件所在基本目录路径
     * @param localFileRelativePath
     *        本地文件相对路径
     * @param remoteBaseDirPath
     *        远程存储文件所在基本目录径路
     * @date: 2014年1月3日上午10:06:41
     */
    public void resendFile(String localBaseDirPath, String localFileRelativePath, String remoteBaseDirPath)
    {
        localBaseDirPath = FileSavingUtils.formatDirPath(localBaseDirPath);
        localFileRelativePath = FileSavingUtils.formatFilePath(localFileRelativePath);

        remoteBaseDirPath = FileSavingUtils.formatDirPath(remoteBaseDirPath);
        String remoteDirPath = FileSavingUtils.getDirPath(remoteBaseDirPath + localFileRelativePath);

        if (isNeedResend(localBaseDirPath, localFileRelativePath, remoteBaseDirPath))
        {
            sftpFileTransfer.uploadFile(localBaseDirPath + localFileRelativePath, remoteDirPath);
        }
    }

    public static void main(String[] args)
    {
        FileResender fd = new FileResender();
        fd.setSftpFileTransfer(new SftpFileTransfer("113.106.90.188", 22, "root", "ddpanico.,ltd@~!@#2505c", 5000));

        fd.getConnection();
        fd.resendDir(new File("D:/test1/f1"), "/usr/local/src/test/f1");
        fd.closeConnection();

        System.out.println("end");
    }
}
