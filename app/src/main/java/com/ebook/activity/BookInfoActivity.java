package com.ebook.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebook.EBookApp;
import com.ebook.R;
import com.ebook.api.Api;
import com.ebook.model.BookLab;
import com.ebook.util.ImageUtil;
import com.ebook.util.ScreenUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BookInfoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Api mApi;
    private ProgressDialog mDefaultDialog;
    private BookInfo mBookInfo;
    private ImageView bookInfoImage;
    private TextView bookTitleTextView;
    private TextView bookAuthorTextView;
    private TextView bookLastTimeTextView;
    private TextView bookNewChapterTextView;
    private TextView bookIntroTextView;
    private ListView mListView;
    private ChapterAdapter mChapterAdapter;
    private ScrollView mScrollView;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mDefaultDialog.cancel();
            super.handleMessage(msg);
            if (msg.arg1 == 0) {
                Toast.makeText(BookInfoActivity.this, "请求服务器失败，请检查网络设置", Toast.LENGTH_LONG).show();
            }
            if (msg.arg1 == 1) {
                Toast.makeText(BookInfoActivity.this, "请求成功", Toast.LENGTH_LONG).show();
                renderBookInfo();
            }
        }
    };
    private Handler imageHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Bitmap bmp = (Bitmap) msg.obj;
                    bookInfoImage.setImageBitmap(bmp);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        initView();
        initData();
    }

    public void initView() {
        mToolbar = (Toolbar) findViewById(R.id.book_info_toolbar);
        this.setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDefaultDialog = new ProgressDialog(this);
        mDefaultDialog.setMessage("请稍后...");
        mDefaultDialog.setCanceledOnTouchOutside(false);
        bookInfoImage = (ImageView) findViewById(R.id.book_info_image);
        bookInfoImage.setImageBitmap(BookLab.newInstance(this).loadImage("image/book_image.jpg"));
        bookTitleTextView = (TextView) findViewById(R.id.book_info_title);
        bookAuthorTextView = (TextView) findViewById(R.id.book_info_author);
        bookLastTimeTextView = (TextView) findViewById(R.id.book_info_lastTime);
        bookNewChapterTextView = (TextView) findViewById(R.id.book_info_newChapter);
        bookIntroTextView = (TextView) findViewById(R.id.book_info_intro);
        mListView = (ListView) findViewById(R.id.chapter_list);
        mScrollView = (ScrollView) findViewById(R.id.book_info_scroll);
    }

    public void initData() {
        Intent intent = getIntent();
        String code = intent.getStringExtra("code");
        //Toast.makeText(this, "code是：" + code, Toast.LENGTH_LONG).show();
        getBookInfo(code);
    }

    public void getBookInfo(String code) {
        if (mApi == null) {
            mApi = new Api(this, EBookApp.urlApi);
        }
        mDefaultDialog.show();
        mApi.getBookInfo(code, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.arg1 = 0;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Log.d("", "onResponse: " + body);
                try {
                    responseBodyToBookInfo(body);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.arg1 = 1;
                mHandler.sendMessage(message);
            }
        });
    }

    public void responseBodyToBookInfo(String body) throws JSONException {
        JSONObject jsonObject = new JSONObject(body);
        if (jsonObject.getString("code").equals("200")) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                mBookInfo = new BookInfo();
                List<Chapter> chapters = new ArrayList<>();
                JSONObject info = data.getJSONObject("info");
                mBookInfo.setTitle(info.getString("title"));
                mBookInfo.setAuthor(info.getString("author"));
                mBookInfo.setLastTime(info.getString("lastTime"));
                mBookInfo.setNewChapter(info.getString("newChapter"));
                mBookInfo.setNewChapterUrl(info.getString("newChapterUrl"));
                mBookInfo.setImage(info.getString("image"));
                mBookInfo.setIntro(info.getString("intro"));
                JSONArray chapterList = data.getJSONArray("chapterList");
                for (int i = 0; i < chapterList.length(); i++) {
                    Chapter chapter = new Chapter();
                    chapter.setName(chapterList.getJSONObject(i).getString("name"));
                    chapter.setUrl(chapterList.getJSONObject(i).getString("url"));
                    chapters.add(chapter);
                }
                mBookInfo.setChapters(chapters);
            }
        }
    }

    public void renderBookInfo() {
        if (mBookInfo == null) {
            mBookInfo = new BookInfo();
        }
        mToolbar.setTitle(mBookInfo.getTitle());
        if (mBookInfo.getImage() != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = ImageUtil.urlToImage(mBookInfo.getImage());
                    Message message = new Message();
                    message.what = 0;
                    message.obj = bitmap;
                    imageHandle.sendMessage(message);
                }
            }).start();
        }
        bookTitleTextView.setText(mBookInfo.getTitle());
        bookAuthorTextView.setText(mBookInfo.getAuthor());
        bookLastTimeTextView.setText(mBookInfo.getLastTime());
        bookNewChapterTextView.setText(mBookInfo.getNewChapter());
        Log.d("", "哈哈哈: " + mBookInfo.getIntro());
        bookIntroTextView.setText(mBookInfo.getIntro());
        ChapterAdapter chapterAdapter = new ChapterAdapter();
        mListView.setAdapter(chapterAdapter);
        //setListViewHeightBasedOnChildren(mListView);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private class ChapterAdapter extends BaseAdapter {
        class ViewHolder {
            private TextView mTextView;
        }

        @Override
        public int getCount() {
            return mBookInfo.getChapters().size();
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
            ViewHolder viewHolder;
            if (convertView == null) {//若沒有缓存
                viewHolder = new ViewHolder();
                convertView = View.inflate(BookInfoActivity.this, R.layout.chapter_list_item, null);
                viewHolder.mTextView = (TextView) convertView.findViewById(R.id.chapter_list_item_text);
                viewHolder.mTextView.setText(mBookInfo.getChapters().get(position).getName());
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                viewHolder.mTextView.setText(mBookInfo.getChapters().get(position).getName());
            }
            return convertView;
        }
    }

    /**
     * 动态设置ListView的高度
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = ScreenUtil.getAndroidScreenHeight(BookInfoActivity.this) * 2;
        listView.setLayoutParams(params);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}


class BookInfo {
    private String title;//书名
    private String author;//作者
    private String lastTime;//最后更新时间
    private String newChapter;//最新更新章节
    private String newChapterUrl;//最新章节路径
    private String image;//图片
    private String intro;//介绍
    private List<Chapter> chapters;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getNewChapter() {
        return newChapter;
    }

    public void setNewChapter(String newChapter) {
        this.newChapter = newChapter;
    }

    public String getNewChapterUrl() {
        return newChapterUrl;
    }

    public void setNewChapterUrl(String newChapterUrl) {
        this.newChapterUrl = newChapterUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }
}

class Chapter {
    private String name;//章节名
    private String url;//地址

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
