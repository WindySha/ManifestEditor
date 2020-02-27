package com.wind.meditor.utils;

import java.io.FileInputStream;
import java.util.HashMap;

/**
 * @author Windysha
 */
public class FileTypeUtils {

    private static final HashMap<String, String> fileHeaderCache = new HashMap<>();

    public static boolean isAndroidManifestFile(String filePath) {
        String sufix = filePath.substring(filePath.lastIndexOf(".") + 1);
        if ("xml".equalsIgnoreCase(sufix)) {
            return true;
        }
        String fileHeader = getFileHeader(filePath);
        if ("03000800".equalsIgnoreCase(fileHeader)) {
            return true;
        }
        return false;
    }

    public static boolean isApkFile(String filePath) {
        String sufix = filePath.substring(filePath.lastIndexOf(".") + 1);
        if ("apk".equalsIgnoreCase(sufix)) {
            return true;
        }
        String fileHeader = getFileHeader(filePath);
        if ("504B0304".equalsIgnoreCase(fileHeader)) {
            return true;
        }
        return false;
    }

    public static String getFileHeader(String filePath) {
        String cachedHeader = fileHeaderCache.get(filePath);
        if (cachedHeader != null && !cachedHeader.isEmpty()) {
            return cachedHeader;
        }
        String header = getFileHeaderInternal(filePath);
        fileHeaderCache.put(filePath, header);
        return header;
    }


    /**
     * 方法描述：根据文件路径获取文件头信息
     *
     * @param filePath 文件路径
     * @return 文件头信息
     */
    private static String getFileHeaderInternal(String filePath) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[4];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            Utils.close(is);
        }
        return value;
    }

    /**
     * 方法描述：将要读取文件头信息的文件的byte数组转换成string类型表示
     *
     * @param src 要读取文件头信息的文件的byte数组
     * @return 文件头信息
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }
}
