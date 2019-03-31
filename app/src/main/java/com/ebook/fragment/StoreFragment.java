package com.ebook.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebook.EBookApp;
import com.ebook.R;
import com.ebook.activity.BookInfoActivity;
import com.ebook.activity.FileActivity;
import com.ebook.activity.ScanActivity;
import com.ebook.api.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.support.constraint.Constraints.TAG;

public class StoreFragment extends Fragment {

    private Toolbar mToolbar;
    private Context mContext;
    private SearchView mSearchView;
    private ListView mListView;
    private Api mApi;
    private List<SearchBook> mSearchBooks;
    private SearchBookAdapter mSearchBookAdapter;
    private ProgressDialog mDefaultDialog;

    private Handler searchHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mDefaultDialog.cancel();
            if (msg.arg1 == 0) {
                Toast.makeText(mContext, "请求服务器失败，请检查网络设置", Toast.LENGTH_LONG).show();
            }
            if (msg.arg1 == 1) {
                mSearchBookAdapter = new SearchBookAdapter();
                mListView.setAdapter(mSearchBookAdapter);
            }
        }
    };

    public StoreFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_store, container, false);
        mContext = getActivity();
        initView(v);
        initToolbar(v);
        return v;
    }

    private void initToolbar(View v) {
        mToolbar = (Toolbar) v.findViewById(R.id.store_fragment_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        //mToolbar.setTitle(R.string.book_store);
        setHasOptionsMenu(true);
    }

    public void initView(View v) {
        mListView = (ListView) v.findViewById(R.id.store_list_view);
        mDefaultDialog = new ProgressDialog(mContext);
        mDefaultDialog.setMessage("正在搜索...");
        mDefaultDialog.setCanceledOnTouchOutside(false);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String code = mSearchBooks.get(position).getCode();
                Toast.makeText(mContext, code, Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("code", code);
                intent.setClass(mContext, BookInfoActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_store_toolbar, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setQueryHint("搜索网络小说");
        mSearchView.onActionViewExpanded();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
                if (mApi == null) {
                    mApi = new Api(mContext, EBookApp.urlApi);
                }
                if (s != null && !s.equals("")) {
                    mDefaultDialog.show();
                    mApi.searchApi(s, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Message message = new Message();
                            message.arg1 = 0;
                            searchHandler.sendMessage(message);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            ResponseBody body = response.body();
                            String string = body.string();
                            Log.d(TAG, "onResponse: " + string);
                            try {
                                responseBodyToList(string);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void responseBodyToList(String body) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject(body);
        String code = jsonObject.getString("code");
        if (code.equals("200")) {
            mSearchBooks = new ArrayList<>();
            JSONArray data = jsonObject.getJSONArray("data");
            if (data != null && data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject dataJSONObject = data.getJSONObject(i);
                    SearchBook searchBook = new SearchBook();
                    searchBook.setType(dataJSONObject.getString("type"));
                    searchBook.setName(dataJSONObject.getString("name"));
                    searchBook.setCode(dataJSONObject.getString("code"));
                    searchBook.setChapter(dataJSONObject.getString("chapter"));
                    searchBook.setChapterCode(dataJSONObject.getString("chapterCode"));
                    searchBook.setAuthor(dataJSONObject.getString("author"));
                    searchBook.setClick(dataJSONObject.getString("click"));
                    searchBook.setTime(dataJSONObject.getString("time"));
                    mSearchBooks.add(searchBook);
                }
            }
            Message message = new Message();
            message.arg1 = 1;
            searchHandler.sendMessage(message);
        }
    }

    private class SearchBookAdapter extends BaseAdapter {
        private TextView typeTextView;
        private TextView nameTextView;
        private TextView chapterTextView;
        private TextView authorTextView;
        private TextView timeTextView;

        @Override
        public int getCount() {
            return mSearchBooks.size();
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
                SearchBook searchBook = mSearchBooks.get(position);
                view = View.inflate(mContext, R.layout.search_book_list_item, null);
                typeTextView = (TextView) view.findViewById(R.id.search_book_type);
                typeTextView.setText("[" + searchBook.getType() + "]");
                nameTextView = (TextView) view.findViewById(R.id.search_book_name);
                nameTextView.setText(searchBook.getName());
                chapterTextView = (TextView) view.findViewById(R.id.search_book_chapter);
                chapterTextView.setText("最新章节：" + searchBook.getChapter());
                authorTextView = (TextView) view.findViewById(R.id.search_book_author);
                authorTextView.setText(searchBook.getAuthor());
                timeTextView = (TextView) view.findViewById(R.id.search_book_time);
                timeTextView.setText("最后更新时间：" + searchBook.getTime());
            } else {
                view = convertView;
            }
            return view;
        }
    }
}

class SearchBook implements Serializable {
    private String type;//类型
    private String name;//名称
    private String code;//编号
    private String chapter;//最新章节
    private String chapterCode;//最新章节编号
    private String author;//作者
    private String click;//点击
    private String time;//最后更新时间

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getChapterCode() {
        return chapterCode;
    }

    public void setChapterCode(String chapterCode) {
        this.chapterCode = chapterCode;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}