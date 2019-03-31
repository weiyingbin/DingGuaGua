package com.ebook.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebook.R;
import com.ebook.api.Api;
import com.ebook.exception.EBookServiceRuntimeException;
import com.ebook.model.BookHandle;
import com.ebook.model.Directory;
import com.ebook.model.Shelf;
import com.ebook.service.ShelfService;
import com.ebook.util.FileManageUtil;
import com.ebook.util.ShelfHandle;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FileActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    private ListView mListView;
    private List<Directory> list;
    private FilesAdapter adapter;
    private TextView currentFolderTextView;
    private FileManageUtil fileManageUtil;
    private Toolbar mToolbar;
    private Button scanButton;
    private ShelfService shelfService;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        initToolbar();
        verifyStoragePermissions(FileActivity.this);
        initView();
        listFiles();
        mListViewItemClick();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.file_listView);
        View view = View.inflate(FileActivity.this, R.layout.files_manage_back, null);
        mListView.addHeaderView(view);
        currentFolderTextView = (TextView) findViewById(R.id.current_folder);
        scanButton = (Button) findViewById(R.id.btn_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("path", currentFolderTextView.getText().toString());
                intent.putExtras(bundle);
                intent.setClass(FileActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.activity_file_toolbar);
        mToolbar.setTitle("本地书导入");
        this.setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void listFiles() {
        fileManageUtil = new FileManageUtil();
        String path = Environment.getExternalStorageDirectory() + "/";
        currentFolderTextView.setText(path);
        Log.d("path", "path: " + path);
        list = fileManageUtil.list(path);
        adapter = new FilesAdapter();
        mListView.setAdapter(adapter);
    }

    private void mListViewItemClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    position = position - 1;
                    int type = list.get(position).getType();
                    String path = list.get(position).getPath();
                    //Toast.makeText(FileActivity.this, path, Toast.LENGTH_LONG).show();
                    if (type == 2) {
                        currentFolderTextView.setText(path);
                        list = fileManageUtil.list(path);
                        Log.d("list", "onCreate: list是：" + list.toString());
                        adapter = new FilesAdapter();
                        mListView.setAdapter(adapter);
                    } else {
                        if (shelfService == null) {
                            shelfService = new ShelfService(FileActivity.this);
                        }
                        //点击文件，是txt或者office
                        boolean isTxt = fileManageUtil.isTxt(new File(path));
                        boolean isOffice = fileManageUtil.isOfficeFile(new File(path));
                        //step1.判断书架是否存在此书，根据书的本地路径判断
                        Shelf shelf = shelfService.getShelfByPath(path);
                        if (shelf == null) {
                            //不存在，则添加书架
                            shelf = new Shelf();
                            shelf.setName(new File(path).getName());
                            shelf.setLocalPath(path);
                            shelf.setCreateTime(sdf.format(new Date()));
                            long shelfId = shelfService.addSelf(shelf);
                            Log.d("123", "onItemClick: id" + shelfId);
                            shelf.setId((int) shelfId);
                        }
                        try {
                            ShelfHandle.openShelf(FileActivity.this, shelf.getId());
                        } catch (EBookServiceRuntimeException e) {
                            Toast.makeText(FileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    String text = currentFolderTextView.getText().toString();
                    String parentDirectory = fileManageUtil.getParentDirectory(text);
                    if (parentDirectory != null && !parentDirectory.equals("")) {
                        parentDirectory += "/";
                        currentFolderTextView.setText(parentDirectory);
                        list = fileManageUtil.list(parentDirectory);
                        Log.d("list", "onCreate: list是：" + list.toString());
                        adapter = new FilesAdapter();
                        mListView.setAdapter(adapter);
                    }
                    //Toast.makeText(FileActivity.this, parentDirectory, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private class FilesAdapter extends BaseAdapter {

        class ViewHolder {
            private ImageView mImageView;
            private TextView mTextView;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            if (list.get(position).getType() != null && list.get(position).getType() == 1) {//文件类型
                return 1;
            } else {//文件夹类型
                return 0;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int currentType = getItemViewType(position);//当前类型
            ViewHolder viewHolder;
            if (currentType == 1) {//文件类型
                if (convertView == null) {//若沒有缓存
                    viewHolder = new ViewHolder();
                    convertView = View.inflate(FileActivity.this, R.layout.file_list_item, null);
                    viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.file_image);
                    viewHolder.mImageView.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
                    viewHolder.mTextView = (TextView) convertView.findViewById(R.id.file_text);
                    viewHolder.mTextView.setText(list.get(position).getName());
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                    viewHolder.mImageView.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
                    viewHolder.mTextView.setText(list.get(position).getName());
                }
            } else {//文件夹类型
                if (convertView == null) {//若沒有缓存
                    viewHolder = new ViewHolder();
                    convertView = View.inflate(FileActivity.this, R.layout.folder_list_item, null);
                    viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.directory_img);
                    viewHolder.mTextView = (TextView) convertView.findViewById(R.id.directory_text);
                    viewHolder.mTextView.setText(list.get(position).getName());
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                    viewHolder.mTextView.setText(list.get(position).getName());
                }
            }
            return convertView;
        }
    }

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i : grantResults) {
            if (i == -1) {
                Intent intent = new Intent(FileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
