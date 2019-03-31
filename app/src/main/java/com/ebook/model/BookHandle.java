package com.ebook.model;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import static org.litepal.LitePalBase.TAG;

/**
 * book工具类，区别于bookLab
 */
public class BookHandle {

    private static String encoding = "GBK";
    private Context mContext;

    public BookHandle(Context context) {
        mContext = context;
    }

    /**
     * 把本地TXT文件转换成Book
     *
     * @param path（文件路径）
     * @return
     */
    public Book localTxtToBook(String path) {
        if (path != null && !path.equals("")) {
            Book book = null;
            File file = new File(path);
            InputStreamReader reader = null;
            StringBuilder stringBuilder = new StringBuilder();
            if (file.isFile() && file.exists()) { //判断文件是否存在
                try {
                    reader = new InputStreamReader(new FileInputStream(file), encoding);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String lineTxt = null;
                    while ((lineTxt = bufferedReader.readLine()) != null) {
                        stringBuilder.append(lineTxt);
                    }
                    reader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                book = new Book(file.getName(), new BookLab(mContext).loadImage("image/book_image.jpg"), stringBuilder.toString());
                Log.d(TAG, "localTxtToBook: " + stringBuilder.toString());
                return book;
            }
        }
        return null;
    }

    public String localTxtToText(String path) throws Exception {
        if (path != null && !path.equals("")) {
            String code = resolveCode(path);
            Book book = null;
            File file = new File(path);
            InputStreamReader reader = null;
            StringBuilder stringBuilder = new StringBuilder();
            if (file.isFile() && file.exists()) { //判断文件是否存在
                try {
                    reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String lineTxt = null;
                    while ((lineTxt = bufferedReader.readLine()) != null) {
                        stringBuilder.append(lineTxt);
                    }
                    reader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.d(TAG, "localTxtToBook: " + stringBuilder.toString());
                return stringBuilder.toString();
            }
        }
        return null;
    }

    public static String resolveCode(String path) throws Exception {
        InputStream inputStream = new FileInputStream(path);
        byte[] b = new byte[3];
        inputStream.read(b);
        inputStream.close();
        String code = "GBK";
        if (b[0] == -17 && b[1] == -69 && b[2] == -65)
            code = "UTF-8";
        Log.d(TAG, "编码格式是：" + code);
        return code;
    }
}
