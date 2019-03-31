package com.ebook.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ebook.EBookApp;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.support.constraint.Constraints.TAG;

public class ImageUtil {
    public static Bitmap urlToImage(String url) {
        Bitmap bmp = null;
        try {
            URL myUrl = new URL(EBookApp.urlBook + url);
            // 获得连接
            Log.d(TAG, "图片路径：" + myUrl);
            HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }
}
