package com.ebook;

import android.support.v4.app.Fragment;

import com.ebook.fragment.ShelfFragment;

public class ShelfActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ShelfFragment();
    }

}
