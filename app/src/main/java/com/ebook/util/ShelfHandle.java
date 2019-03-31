package com.ebook.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.ebook.EBookApp;
import com.ebook.activity.ReadingActivity;
import com.ebook.activity.ReadingOfficeActivity;
import com.ebook.api.Api;
import com.ebook.exception.EBookServiceRuntimeException;
import com.ebook.model.Shelf;
import com.ebook.service.ShelfService;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.support.constraint.Constraints.TAG;

public class ShelfHandle {

    public static void openShelf(Context context, Integer shelfId) throws EBookServiceRuntimeException {
        if (shelfId == null) {
            throw new EBookServiceRuntimeException("书架id为空");
        }
        //step1.根据书架id查询书架信息
        ShelfService shelfService = new ShelfService(context);
        Shelf shelf = shelfService.getShelf(shelfId);
        Log.d(TAG, "openShelf: shelf" + shelf.toString());
        if (shelf != null) {
            //step2.获取到书架中的文件路径并判断其是TXT还是office
            String localPath = shelf.getLocalPath();
            if (localPath != null) {
                FileManageUtil fileManageUtil = new FileManageUtil();
                boolean isTxt = fileManageUtil.isTxt(new File(localPath));
                boolean isOffice = fileManageUtil.isOfficeFile(new File(localPath));
                if (isTxt) {//如果是TXT文件
                    Intent intent = ReadingActivity.newIntent(context, shelf.getId());
                    context.startActivity(intent);
                } else if (isOffice) {//如果是office文件
                    //step3.获取shelf中的htmlPath
                    String htmlPath = shelf.getHtmlPath();
                    if (htmlPath != null) {
                        Intent intent = ReadingOfficeActivity.newIntent(context, "file://" + shelf.getHtmlPath());
                        context.startActivity(intent);
                    } else {//若没有htmlPath，则请求服务器生成html
                        officeToHtml(context, shelf);
                    }
                }
            } else {
                throw new EBookServiceRuntimeException("书架中没有本地路径，请删除后从新添加");
            }
        } else {
            throw new EBookServiceRuntimeException("找不到书架信息");
        }
    }

    /**
     * office文件转html
     *
     * @param context
     * @param shelf
     */
    public static void officeToHtml(final Context context, final Shelf shelf) {
        final Api api = new Api(context, EBookApp.urlApi);
        final ShelfService shelfService = new ShelfService(context);
        try {
            api.openOfficeApi(shelf.getLocalPath(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(context, e + "请求服务器失败，请检查网络设置", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("123", "onFailure: 请求成功");
                    ResponseBody body = response.body();
                    if (body != null) {
                        String path = api.responseBodyToHtml(body, shelf.getId());
                        Log.d("123", "onResponse: s" + path);
                        shelfService.updateHtmlPath(shelf.getId(), path);
                        Intent intent = ReadingOfficeActivity.newIntent(context, "file://" + path);
                        context.startActivity(intent);
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
