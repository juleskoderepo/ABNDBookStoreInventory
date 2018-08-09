package com.example.android.abndbookstoreinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.android.abndbookstoreinventory.data.ProductContract.ProductEntry.*;

public class ProductDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
            COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0, " +
            COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
            COLUMN_SUPPLIER_NAME + " TEXT, " +
            COLUMN_SUPPLIER_PHONE + " TEXT " +
            ");";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // Default constructor
    public ProductDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.i("ProductDbHelper"," CREATE TABLE SQL statement: " + SQL_CREATE_TABLE);
        // Create db table
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // If database is upgraded, discard the data and start over
        sqLiteDatabase.execSQL(SQL_DROP_TABLE);
        onCreate(sqLiteDatabase);
    }
}
