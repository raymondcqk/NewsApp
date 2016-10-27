package com.boolan.news.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by SpaceRover on 2016/9/29.
 */

public class ResourceStorage {

    private Context context;

    /**
     * 构造函数取得context引用
     * @param context
     */
    public ResourceStorage(Context context) {
        this.context = context;
    }

    /**
     * 解析URL中的文件名
     *
     * 取得最后一个斜杠后面的子字符串 --- 文件名
     *
     * @param url
     * @return
     */
    public String getFileName(String url) {
        //取得最后一个斜杠后面的子字符串 --- 文件名
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 获得图片目录
     *
     * @return 返回目录路径字符串
     */
    public String getNewsImgDir() {
        /**
         * File.separator 分隔符 /
         */

        // 目录文件（路径)
        File dir = new File(context.getExternalFilesDir(null), File.separator + "img" + File
                .separator);
        if (!dir.exists()) {
            dir.mkdir();
        }
        // 返回目录路径字符串
        return dir.toString();
    }

    /**
     * 获取文件后缀名
     *
     * @param fileName
     * @return
     */
    public String getFileFormat(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 获得图片的压缩格式
     *
     * 根据文件名的后缀名，判断图片压缩格式
     *
     * @param fileName
     * @return Bitmap.CompressFormat.PNG or Bitmap.CompressFormat.JPEG
     */
    public Bitmap.CompressFormat getCompressFormat(String fileName) {
        String format = getFileFormat(fileName);
        if (format.equals("png")) {
            return Bitmap.CompressFormat.PNG;
        } else {
            return Bitmap.CompressFormat.JPEG;
        }
    }

    /**
     * 保存图片到文件
     *
     *
     * @param fileName
     * @param bitmap
     */
    public void saveImg(String fileName, Bitmap bitmap) {
        File file = new File(getNewsImgDir(), fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(getCompressFormat(fileName), 100, fos);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
