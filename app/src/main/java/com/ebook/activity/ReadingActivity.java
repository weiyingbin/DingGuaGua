package com.ebook.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.WindowManager;


public class ReadingActivity extends SingleFragmentActivity {
    public static final String EXTRA_BOOK_ID = "EXTRA_BOOK_ID";
    public static final String SHELF_ID = "SHELF_ID";//书架id

    /**
     * 创建intent
     *
     * @param context（应用上下文）
     * @param shelfId（书架id）
     * @return
     */
    public static Intent newIntent(Context context, Integer shelfId) {
        Intent intent = new Intent(context, ReadingActivity.class);
        intent.putExtra(SHELF_ID, shelfId);
        return intent;
    }

    @Override
    protected void setScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    //全屏
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected Fragment createFragment() {
        int shelfId = getIntent().getIntExtra(SHELF_ID, 0);
        return ReadingFragment.newInstance(shelfId);
    }

}

