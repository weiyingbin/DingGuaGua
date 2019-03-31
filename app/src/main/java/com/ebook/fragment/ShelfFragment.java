package com.ebook.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebook.activity.FileActivity;
import com.ebook.R;
import com.ebook.activity.ReadingActivity;
import com.ebook.activity.ShelfActivity;
import com.ebook.exception.EBookServiceRuntimeException;
import com.ebook.model.Book;
import com.ebook.model.BookLab;
import com.ebook.model.Shelf;
import com.ebook.service.ShelfService;
import com.ebook.util.ShelfHandle;

import java.util.ArrayList;
import java.util.List;


public class ShelfFragment extends Fragment {
    private Context mContext;
    private List<Book> mBookList;
    private List<Shelf> mShelfList;
    private Toolbar mToolbar;
    private ShelfService shelfService;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shelf_layout, container, false);
        mContext = getActivity();
        initToolbar(v);
        initEvents(v);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.shelf_toolbar_menu, menu);
    }

    private void initToolbar(View v) {
        mToolbar = (Toolbar) v.findViewById(R.id.shelf_fragment_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        setHasOptionsMenu(true);
        mToolbar.setTitle(R.string.book_shelf);
        mToolbar.inflateMenu(R.menu.shelf_toolbar_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.toolbar_menu_import:
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), FileActivity.class);
                        startActivity(intent);
                }
                return true;
            }
        });
    }

    private void initEvents(View v) {

        mShelfList = new ArrayList<>();
        //mBookList = BookLab.newInstance(mContext).getBookList();
        if (shelfService == null) {
            shelfService = new ShelfService(mContext);
        }
        List<Book> bookList = BookLab.newInstance(mContext).getBookList();
        List<Shelf> shelves = shelfService.listShelf();
        for (Shelf shelf : shelves) {
            mShelfList.add(shelf);
        }
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.fragment_book_shelf_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        recyclerView.setAdapter(new ShelfAdapter(mShelfList));
    }


    private class ShelfHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mShelfImage;
        private TextView mShelfName;
        private Shelf mShelf;

        public ShelfHolder(View itemView) {
            super(itemView);
            mShelfImage = (ImageView) itemView.findViewById(R.id.item_recycler_view_image_view);
            mShelfName = (TextView) itemView.findViewById(R.id.item_recycler_view_text_view);
            itemView.setOnClickListener(this);
        }

        public void bind(Shelf shelf) {
            mShelf = shelf;
            if (mShelf.getImage() == null) {
                mShelfImage.setImageBitmap(BookLab.newInstance(mContext).loadImage("image/book_image.jpg"));
            }
            mShelfName.setText(mShelf.getName());
        }

        @Override
        public void onClick(View v) {
            try {
                ShelfHandle.openShelf(mContext, mShelf.getId());
            } catch (EBookServiceRuntimeException e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class ShelfAdapter extends RecyclerView.Adapter<ShelfHolder> {
        private List<Shelf> shelfList = new ArrayList<>();

        public ShelfAdapter(List<Shelf> shelfList) {
            this.shelfList = shelfList;
        }

        @Override
        public ShelfHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_recycler_view_shelf, parent, false);
            return new ShelfHolder(view);
        }

        @Override
        public void onBindViewHolder(ShelfHolder holder, int position) {
            holder.bind(shelfList.get(position));
        }

        @Override
        public int getItemCount() {
            return shelfList.size();
        }
    }

}
