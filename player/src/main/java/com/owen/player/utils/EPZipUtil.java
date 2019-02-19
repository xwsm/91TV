package com.owen.player.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by owen on 15/2/11.
 */
public class EPZipUtil {
    private static final String TAG = EPZipUtil.class.getSimpleName();

    /**
     * 取得压缩包中的 文件列表(文件夹,文件自选)
     *
     * @param zipFileString  压缩包名字
     * @param bContainFolder 是否包括 文件夹
     * @param bContainFile   是否包括 文件
     * @return
     * @throws Exception
     */
    public static List<File> getFileList(String zipFileString, boolean bContainFolder, boolean bContainFile) throws Exception {

        Log.v(TAG, "getFileList...");

        List<File> fileList = new ArrayList<File>();
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";

        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();

            if (zipEntry.isDirectory()) {

                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(szName);
                if (bContainFolder) {
                    fileList.add(folder);
                }

            } else {
                File file = new File(szName);
                if (bContainFile) {
                    fileList.add(file);
                }
            }
        }

        inZip.close();

        return fileList;
    }

    /**
     * 返回压缩包中的文件InputStream
     *
     * @param zipFileString 压缩文件的名字
     * @param fileString    解压文件的名字
     * @return InputStream
     * @throws Exception
     */
    public static java.io.InputStream upZip(String zipFileString, String fileString) throws Exception {
        Log.v(TAG, "upZip...");
        java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(zipFileString);
        ZipEntry zipEntry = zipFile.getEntry(fileString);

        return zipFile.getInputStream(zipEntry);

    }


    /**
     * 解压一个压缩文档 到指定位置
     *
     * @param zipFileString 压缩包的名字
     * @param outPathString 指定的路径
     * @return List 返回解压后的文件目录
     */
    public static List<String> unZipFolder(String zipFileString, String outPathString) {
        Log.d(TAG, "unZipFolder start");
        ZipInputStream zip = null;
        List<String> filePaths = new ArrayList<>();
        try {
            zip = new ZipInputStream(new FileInputStream(zipFileString));

            File outFolder = new File(outPathString);

            if (!outFolder.exists() || !outFolder.isDirectory()) {
                outFolder.mkdirs();
            }

            ZipEntry zipEntry;
            String name = "";
            while ((zipEntry = zip.getNextEntry()) != null) {
                name = zipEntry.getName();

                if (zipEntry.isDirectory()) {
                    name = name.substring(0, name.length() - 1);
                    File folder = new File(outPathString + File.separator + name);
                    folder.mkdirs();
                } else {
                    File file = new File(outPathString + File.separator + name);
                    file.createNewFile();
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(file);
                        int len;
                        byte[] buffer = new byte[4096];
                        // read (len) bytes into buffer
                        while ((len = zip.read(buffer)) != -1) {
                            // write (len) byte from buffer at the position 0
                            out.write(buffer, 0, len);
                            out.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            }catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    filePaths.add(file.getAbsolutePath());
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "unZipFolder end");
        }
        return filePaths;
    }


    /**
     * 压缩文件,文件夹
     *
     * @param srcFileString 要压缩的文件/文件夹名字
     * @param zipFileString 指定压缩的目的和名字
     * @throws Exception
     */
    public static void zipFolder(String srcFileString, String zipFileString) throws Exception {
        Log.v(TAG, "zipFolder...");

        //创建Zip包
        java.util.zip.ZipOutputStream outZip = new java.util.zip.ZipOutputStream(new FileOutputStream(zipFileString));

        //打开要输出的文件
        File file = new File(srcFileString);

        //压缩
        zipFiles(file.getParent() + File.separator, file.getName(), outZip);

        //完成,关闭
        outZip.finish();
        outZip.close();

    }

    /**
     * 压缩文件
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void zipFiles(String folderString, String fileString, java.util.zip.ZipOutputStream zipOutputSteam) throws Exception {
        Log.v(TAG, "zipFiles...");

        if (zipOutputSteam == null) {
            return;
        }

        File file = new File(folderString + fileString);

        //判断是不是文件
        if (file.isFile()) {

            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);

            int len;
            byte[] buffer = new byte[4096];

            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }

            zipOutputSteam.closeEntry();
        } else {

            //文件夹的方式,获取文件夹下的子文件
            String fileList[] = file.list();

            //如果没有子文件, 则添加进去即可
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }

            //如果有子文件, 遍历子文件
            for (int i = 0; i < fileList.length; i++) {
                zipFiles(folderString, fileString + File.separator + fileList[i], zipOutputSteam);
            }

        }

    }
}
