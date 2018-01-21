package com.woodsho.absoluteplan.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by hewuzhao on 18/1/20.
 */

public class FileUtil {
    private static final String TAG = "FileUtil";

    // 判断文件夹是否存在
    public static void judeDirExists(File file) {
        if (file.exists()) {
            if (!file.isDirectory()) {
                file.delete();
                file.mkdir();
            }
        } else {
            file.mkdir();
        }
    }

    public static String getSkinDirPath(Context context) {
        File cacheDir = context.getCacheDir();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = context.getExternalCacheDir();
            if (dir != null && (dir.exists() || dir.mkdirs())) {
                cacheDir = dir;
            }
        }

        File skinDir = new File(cacheDir, "skin");
        if (skinDir.exists()) {
            skinDir.mkdirs();
        }
        return skinDir.getAbsolutePath();
    }

    public static void moveRawToDir(Context context, String rawName, String dir) {
        try {
            writeFile(context.getAssets().open(rawName), dir, true);
        } catch (Exception ex) {
            Log.e(TAG, "" + ex);
        }
    }

    public static File writeFile(InputStream is, String path, boolean isOverride) throws Exception {
        String sPath = extractFilePath(path);
        if (!pathExists(sPath)) {
            makeDir(sPath);
        }

        if (!isOverride && fileExists(path)) {
            if (path.contains(".")) {
                String suffix = path.substring(path.lastIndexOf("."));
                String pre = path.substring(0, path.lastIndexOf("."));
                path = pre + "_" + System.currentTimeMillis() + suffix;
            } else {
                path = path + "_" + System.currentTimeMillis();
            }
        }

        FileOutputStream os = null;
        File file = null;

        try {
            file = new File(path);
            os = new FileOutputStream(file);
            int byteCount = 0;
            byte[] bytes = new byte[1024];

            while ((byteCount = is.read(bytes)) != -1) {
                os.write(bytes, 0, byteCount);
            }
            os.flush();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("写文件错误", e);
        } finally {
            try {
                if (os != null)
                    os.close();
                if (is != null)
                    is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从文件的完整路径名（路径+文件名）中提取 路径（包括：Drive+Directroy )
     *
     * @param _sFilePathName
     * @return
     */
    public static String extractFilePath(String _sFilePathName) {
        int nPos = _sFilePathName.lastIndexOf('/');
        if (nPos < 0) {
            nPos = _sFilePathName.lastIndexOf('\\');
        }

        return (nPos >= 0 ? _sFilePathName.substring(0, nPos + 1) : "");
    }

    /**
     * 检查指定文件的路径是否存在
     *
     * @param _sPathFileName 文件名称(含路径）
     * @return 若存在，则返回true；否则，返回false
     */
    public static boolean pathExists(String _sPathFileName) {
        String sPath = extractFilePath(_sPathFileName);
        return fileExists(sPath);
    }

    public static boolean fileExists(String _sPathFileName) {
        File file = new File(_sPathFileName);
        return file.exists();
    }

    /**
     * 创建目录
     *
     * @param _sDir             目录名称
     * @return
     */
    public static boolean makeDir(String _sDir) {
        boolean zResult = false;
        try {
            File file = new File(_sDir);
            zResult = file.mkdir();

            if (!zResult)
                zResult = file.exists();

        } catch (Exception ex) {
            Log.e(TAG, "ex: " + ex);
        }

        return zResult;
    }
}
