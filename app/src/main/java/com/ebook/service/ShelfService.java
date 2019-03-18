package com.ebook.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ebook.db.SQLiteDbHelper;
import com.ebook.model.Shelf;

import java.util.ArrayList;
import java.util.List;

public class ShelfService {
    private SQLiteDbHelper helper;
    private SQLiteDatabase database;

    public ShelfService(Context context) {
        super();
        helper = new SQLiteDbHelper(context);
        database = helper.getWritableDatabase();
    }

    //添加书架
    public void addSelf(Shelf shelf) {
        database.insert(SQLiteDbHelper.TABLE_SHELF, null, shelfToContentValues(shelf));
    }

    //查询书架
    public List<Shelf> listShelf() {
        List<Shelf> shelves = new ArrayList<>();
        Cursor cursor = database.query(SQLiteDbHelper.TABLE_SHELF, null, null, null, null, null, null);
        // 不断移动光标获取值
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String localPath = cursor.getString(cursor.getColumnIndex("localPath"));
            String image = cursor.getString(cursor.getColumnIndex("image"));
            String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
            Shelf shelf = new Shelf();
            shelf.setId(id);
            shelf.setName(name);
            shelf.setLocalPath(localPath);
            shelf.setImage(image);
            shelf.setCreateTime(createTime);
            shelves.add(shelf);
        }
        return shelves;
    }

    private ContentValues shelfToContentValues(Shelf shelf) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", shelf.getId());
        contentValues.put("name", shelf.getName());
        contentValues.put("localPath", shelf.getLocalPath());
        contentValues.put("image", shelf.getImage());
        contentValues.put("createTime", shelf.getCreateTime());
        return contentValues;
    }
}
