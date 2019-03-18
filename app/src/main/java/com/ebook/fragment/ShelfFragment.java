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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebook.FileActivity;
import com.ebook.R;
import com.ebook.ReadingActivity;
import com.ebook.model.Book;
import com.ebook.model.BookLab;
import com.ebook.model.Shelf;
import com.ebook.service.ShelfService;

import java.util.ArrayList;
import java.util.List;

import static org.litepal.LitePalBase.TAG;


public class ShelfFragment extends Fragment {
    private Context mContext;
    private List<Book> mBookList;
    private Toolbar mToolbar;
    private ShelfService shelfService;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shelf_layout, container, false);
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
        mToolbar.setTitle(R.string.title_activity_main);
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
        mContext = getActivity();
        mBookList = new ArrayList<>();
        mBookList = BookLab.newInstance(mContext).getBookList();
        if (shelfService == null) {
            shelfService = new ShelfService(mContext);
        }
        List<Book> bookList = BookLab.newInstance(mContext).getBookList();
        List<Shelf> shelves = shelfService.listShelf();
        for (Shelf shelf : shelves) {
            Log.d(TAG, "shelf:" + shelf.toString());
            Book book = new Book(shelf.getName(), bookList.get(0).getBookCover());
            mBookList.add(book);
        }
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.fragment_book_shelf_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        recyclerView.setAdapter(new BookAdapter(mBookList));
    }


    private class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mBookCover;
        private TextView mBookTitle;
        private Book mBook;

        public BookHolder(View itemView) {
            super(itemView);
            mBookCover = (ImageView) itemView.findViewById(R.id.item_recycler_view_image_view);
            mBookTitle = (TextView) itemView.findViewById(R.id.item_recycler_view_text_view);
            itemView.setOnClickListener(this);
        }

        public void bind(Book book) {
            mBook = book;
            mBookCover.setImageBitmap(mBook.getBookCover());
            mBookTitle.setText(mBook.getBookTitle());
        }

        @Override
        public void onClick(View v) {
            Intent intent = ReadingActivity.newIntent(mContext, mBookList.indexOf(mBook));
            startActivity(intent);
        }


    }

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private List<Book> bookList = new ArrayList<>();

        public BookAdapter(List<Book> bookList) {
            this.bookList = bookList;
        }

        @Override
        public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_recycler_view_shelf, parent, false);
            return new BookHolder(view);
        }

        @Override
        public void onBindViewHolder(BookHolder holder, int position) {
            holder.bind(bookList.get(position));
        }

        @Override
        public int getItemCount() {
            return bookList.size();
        }
    }

}
