package me.huaisu.common.android.utils;

import android.database.Cursor;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtils {

    private static final String TAG = "IoUtils";

    private static final int BUFFER_LENGTH = 4096;

    public static void closeQuietly(Closeable fd) {
        if (fd != null) {
            try {
                fd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean copyFile(String sourceFile, String targetFile) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = new FileInputStream(sourceFile);
            fos = new FileOutputStream(targetFile);
            int len;
            byte[] buff = new byte[BUFFER_LENGTH];
            while ((len = is.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
            return true;
        } catch (Exception e) {
            Logger.w(TAG, e);
        } finally {
            closeQuietly(is);
            closeQuietly(fos);
        }

        return false;
    }

    public static void closeQuietly(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveToFile(String filePath, byte[] data) {
        FileOutputStream fos = null;
        try {
            if (!TextUtils.isEmpty(filePath)) {
                fos = new FileOutputStream(filePath);
                fos.write(data);
                fos.flush();
            }
        } catch (IOException e) {
            Logger.w(TAG, e);
        } finally {
            closeQuietly(fos);
        }
    }

    public static String readToString(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        StringBuilder sb = new StringBuilder(BUFFER_LENGTH);
        InputStream in = null;
        BufferedReader br = null;
        try {
            in = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(in));
            String read;
            while ((read = br.readLine()) != null) {
                sb.append(read);
            }
        } catch (Exception e) {
            Logger.w(TAG, e);
        } finally {
            closeQuietly(in);
            closeQuietly(br);
        }
        return sb.toString();
    }

    public static String readToString(InputStream in) {
        StringBuilder sb = new StringBuilder(BUFFER_LENGTH);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in));
            String read;
            while ((read = br.readLine()) != null) {
                sb.append(read);
            }
        } catch (Exception e) {
            Logger.w(TAG, e);
        } finally {
            closeQuietly(in);
            closeQuietly(br);
        }
        return sb.toString();
    }

    public static byte[] fileToBytes(String path) {
        if (TextUtils.isEmpty(path)) {
            return new byte[0];
        }
        return fileToBytes(new File(path));
    }

    public static byte[] fileToBytes(File file) {
        byte[] result = new byte[0];
        if (file == null || !file.exists()) {
            return result;
        }
        InputStream in = null;
        ByteArrayOutputStream bos = null;
        byte[] buffer = new byte[BUFFER_LENGTH];
        try {
            in = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            int length = in.read(buffer, 0, BUFFER_LENGTH);
            while (length != -1) {
                bos.write(buffer, 0, length);
                length = in.read(buffer, 0, BUFFER_LENGTH);
            }
            result = bos.toByteArray();
        } catch (Exception e) {
            Logger.w(TAG, e);
        } finally {
            closeQuietly(in);
            closeQuietly(bos);
        }
        return result;
    }
}
