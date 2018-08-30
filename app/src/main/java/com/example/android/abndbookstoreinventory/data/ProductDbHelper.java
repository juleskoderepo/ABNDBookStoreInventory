package com.example.android.abndbookstoreinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.android.abndbookstoreinventory.data.ProductContract.ProductEntry.*;

public class ProductDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "inventory.db";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
            COLUMN_PRODUCT_DESCRIPTION + " TEXT, " +
            COLUMN_PRODUCT_CATEGORY + " INTEGER NOT NULL DEFAULT 0, " +
            COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0, " +
            COLUMN_QUANTITY_IN_STOCK + " INTEGER NOT NULL DEFAULT 0, " +
            COLUMN_QUANTITY_ON_ORDER + " INTEGER NOT NULL DEFAULT 0, " +
            COLUMN_SUPPLIER_NAME + " TEXT, " +
            COLUMN_SUPPLIER_PHONE + " TEXT " +
            ");";

    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // Default constructor
    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.i("ProductDbHelper", " CREATE TABLE SQL statement: " + SQL_CREATE_TABLE);
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
