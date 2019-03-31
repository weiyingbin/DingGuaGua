package com.ebook.api;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static android.support.constraint.Constraints.TAG;

public class Api {
    private OkHttpClientFactory okHttpClientFactory = new OkHttpClientFactory();
    private String mUrl;
    private Context mContext;

    public Api(Context context, String url) {
        mContext = context;
        mUrl = url;
    }

    public boolean openOfficeApi(String filePath, Callback callback) throws UnsupportedEncodingException {
        if (filePath != null) {
            File file = new File(filePath);
            if (file != null && file.isFile()) {
                String url = mUrl + "/upload/";
                OkHttpClient okHttpClient = okHttpClientFactory.getOkHttpClient();
                //RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", Base64.encodeToString(file.getName().getBytes(), Base64.NO_WRAP),
                                RequestBody.create(MediaType.parse("multipart/form-data"), file))
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(callback);
                Log.d(TAG, "openOfficeApi: 请求路径：" + url);
                return true;
            }
        }
        return false;
    }

    public boolean searchApi(String name, Callback callback) {
        if (name != null && !name.equals("")) {
            String url = mUrl + "/search/?name=" + name;
            OkHttpClient okHttpClient = okHttpClientFactory.getOkHttpClient();
            Request request = new Request.Builder().get().url(url).build();
            okHttpClient.newCall(request).enqueue(callback);
            Log.d(TAG, "searchApi: 请求路径：" + url);
            return true;
        }
        return false;
    }

    public void getBookInfo(String code, Callback callback) {
        if (code != null && !code.equals("")) {
            String url = mUrl + "/getBookInfo/?code=" + code;
            //OkHttpClient okHttpClient = okHttpClientFactory.getOkHttpClient();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时时间
                    .readTimeout(20, TimeUnit.SECONDS)//设置读取超时时间
                    .build();
            Request request = new Request.Builder().get().url(url).build();
            client.newCall(request).enqueue(callback);
            Log.d(TAG, "searchApi: 请求路径：" + url);
        }
    }

    /**
     * ResponseBody转html，失败则返回null
     *
     * @param rb
     * @param shelfId
     * @return
     * @throws IOException
     */
    public String responseBodyToHtml(ResponseBody rb, Integer shelfId) throws IOException {
        String fileName = shelfId + ".html";
        String htmlPath = mContext.getFilesDir() + "/html/";
        File htmlFile = new File(htmlPath);
        if (!htmlFile.exists()) {
            htmlFile.mkdirs();
        }
        File file = new File(htmlFile.getPath(), fileName);
        Log.d(TAG, "filePath:" + file.getPath());
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(rb.bytes());
        outputStream.flush();
        outputStream.close();
        return file.getPath();
    }
}

