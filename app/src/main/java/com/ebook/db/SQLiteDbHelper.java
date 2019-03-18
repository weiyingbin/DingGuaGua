package com.ebook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "ebook.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_SHELF = "shelf";

    private static final String CREATE_SHELF_TABLE_SQL = "create table " + TABLE_SHELF + "("
            + "id integer(11) primary key autoincrement,"
            + "name varchar(255),localPath varchar(255),"
            + "image varchar(255),createTime varchar(255))";

    public SQLiteDbHelper(Context context) {
        // 传递数据库名与版本号给父类
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 在这里通过 db.execSQL 函数执行 SQL 语句创建所需要的表
        // 创建 shelf 表
        db.execSQL(CREATE_SHELF_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 数据库版本号变更会调用 onUpgrade 函数，在这根据版本号进行升级数据库
        switch (oldVersion) {
            case 1:
                // do something
                break;
            default:
                break;
        }
    }
}
