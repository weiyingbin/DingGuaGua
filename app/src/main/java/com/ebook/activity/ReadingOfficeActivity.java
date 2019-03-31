package com.ebook.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.ebook.R;

public class ReadingOfficeActivity extends AppCompatActivity {

    private WebView mWebView;
    private String mPath;

    public static Intent newIntent(Context context, String path) {
        Bundle args = new Bundle();
        Intent intent = new Intent(context, ReadingOfficeActivity.class);
        args.putString("path", path);
        intent.putExtras(args);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_office);
        initView();
        initData();
    }

    public void initView() {
        mWebView = (WebView) findViewById(R.id.reading_office_webview);
    }

    public void initData() {
        Bundle bundle = getIntent().getExtras();
        String path = (String) bundle.get("path");
        mPath = path;
        Log.d("initDate", "打开路径：" + mPath);
        mWebView.loadUrl(mPath);
    }
}
