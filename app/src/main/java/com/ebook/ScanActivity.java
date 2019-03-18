package com.ebook;

import android.app.ProgressDialog;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ebook.util.FileManageUtil;

import java.io.File;
import java.util.List;

public class ScanActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private List<File> files;
    private FileManageUtil fileManageUtil;
    private ListView mListView;
    private ScanFileAdapter adapter;
    private ProgressDialog mDefaultDialog;
    private String path;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter = new ScanFileAdapter();
            mListView.setAdapter(adapter);
            mDefaultDialog.cancel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Bundle bundle = getIntent().getExtras();
        String path = (String) bundle.get("path");
        if (path != null && !path.equals("")) {
            this.path = path;
        }
        initToolbar();
        initView();
        start();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.activity_scan_toolbar);
        mToolbar.setTitle("扫描结果");
        this.setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.activity_scan_list);
        mDefaultDialog = new ProgressDialog(this);
        mDefaultDialog.setMessage("正在扫描...");
        mDefaultDialog.setCanceledOnTouchOutside(false);
    }

    private void start() {
        mDefaultDialog.show();
        fileManageUtil = new FileManageUtil();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (path == null) {
                    path = Environment.getExternalStorageDirectory() + "/";
                }
                files = fileManageUtil.scanOfficeFile(path);
                Message message = new Message();
                handler.sendMessage(message);
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class ScanFileAdapter extends BaseAdapter {

        private ImageView mImageView;
        private TextView mTextView;

        @Override
        public int getCount() {
            return files.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView == null) {//若沒有缓存
                view = View.inflate(ScanActivity.this, R.layout.file_list_item, null);
                mImageView = (ImageView) view.findViewById(R.id.file_image);
                mImageView.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
                mTextView = (TextView) view.findViewById(R.id.file_text);
                mTextView.setText(files.get(position).getName());
            } else {
                view = convertView;
            }
            return view;
        }
    }

}
