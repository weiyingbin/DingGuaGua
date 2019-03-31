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
    private Context mContext;

    public ShelfService(Context context) {
        super();
        mContext = context;
        helper = new SQLiteDbHelper(mContext);
        database = helper.getWritableDatabase();
    }

    //添加书架
    public long addSelf(Shelf shelf) {
        return database.insert(SQLiteDbHelper.TABLE_SHELF, null, shelfToContentValues(shelf));
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

    /**
     * 根据id查询书架中的书
     *
     * @param id
     * @return
     */
    public Shelf getShelf(Integer id) {
        Shelf shelf = new Shelf();
        Cursor cursor = database.query(SQLiteDbHelper.TABLE_SHELF, null, "id=?", new String[]{id.toString()}, null, null, null);
        while (cursor.moveToNext()) {
            int shelfId = cursor.getInt(0);
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String localPath = cursor.getString(cursor.getColumnIndex("localPath"));
            String image = cursor.getString(cursor.getColumnIndex("image"));
            String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
            shelf.setId(shelfId);
            shelf.setName(name);
            shelf.setLocalPath(localPath);
            shelf.setImage(image);
            shelf.setCreateTime(createTime);
        }
        return shelf;
    }

    /**
     * 根据本地路径查询书架中的书
     *
     * @param path
     * @return
     */
    public Shelf getShelfByPath(String path) {
        Shelf shelf = null;
        Cursor cursor = database.query(SQLiteDbHelper.TABLE_SHELF, null, "localPath=?", new String[]{path}, null, null, null);
        while (cursor.moveToNext()) {
            shelf = new Shelf();
            int shelfId = cursor.getInt(0);
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String localPath = cursor.getString(cursor.getColumnIndex("localPath"));
            String image = cursor.getString(cursor.getColumnIndex("image"));
            String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
            shelf.setId(shelfId);
            shelf.setName(name);
            shelf.setLocalPath(localPath);
            shelf.setImage(image);
            shelf.setCreateTime(createTime);
        }
        return shelf;
    }

    /**
     * 根据path判断书架中是否处存在这本书
     *
     * @param path
     * @return
     */
    public boolean isExist(String path) {
        Cursor cursor = database.query(SQLiteDbHelper.TABLE_SHELF, new String[]{"id"}, "localPath=?", new String[]{path}, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            return true;//存在
        } else {
            return false;//不存在
        }
    }

    /**
     * 修改htmlPath值
     *
     * @param shelfId
     * @param htmlPath
     * @return
     */
    public boolean updateHtmlPath(Integer shelfId, String htmlPath) {
//        ContentValues values = new ContentValues();
//        values.put("htmlPath", htmlPath);
//        int update = database.update(SQLiteDbHelper.TABLE_SHELF, values, "id=?", new String[]{String.valueOf(shelfId)});
//        if (update > 0) {
//            return true;
//        }
        SQLiteDbHelper.update(database, "update shelf set htmlPath='" + htmlPath + "' where id=" + shelfId);
        return false;
    }


    /**
     * 把shelf转换成ContentValues对象
     *
     * @param shelf
     * @return
     */
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
