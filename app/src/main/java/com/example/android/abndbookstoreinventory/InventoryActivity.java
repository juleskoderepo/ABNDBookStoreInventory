package com.example.android.abndbookstoreinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.abndbookstoreinventory.data.ProductDbHelper;
import com.example.android.abndbookstoreinventory.data.ProductContract.ProductEntry;

import java.math.BigDecimal;

public class InventoryActivity extends AppCompatActivity {

    private static final String LOG_TAG = InventoryActivity.class.getSimpleName();

    ProductDbHelper prodDbHelper;
    ProductCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Database helper to get access to the database
        prodDbHelper = new ProductDbHelper(this);

        // Display current db info
        displayProductInfo();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_insert_dummy_data:
                insertProduct();
                displayProductInfo();

                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Helper method to read data from the database and display it on screen
    private void displayProductInfo() {
        // Define a projection as it is inefficient to return all columns by passing in null
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY_IN_STOCK
        };

        Cursor cursor = getContentResolver().query(ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

        // Find ListView and populate
        ListView productListView = findViewById(R.id.list_product);

        // Find and set the empty view on the list view when there are no items in the list
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        // Set up cursor adapter using cursor
        cursorAdapter = new ProductCursorAdapter(this, cursor, 0);
        // Attach cursor adapter to ListView
        productListView.setAdapter(cursorAdapter);
    }

    /**
     * Helper method to insert hardcoded values into db. For debugging purposes.
     */
    private void insertProduct() {
        // Create a map of values with column names as keys
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, getString(R.string.dummy_name));
        values.put(ProductEntry.COLUMN_PRODUCT_DESCRIPTION,"Guide book for visitors");
        values.put(ProductEntry.COLUMN_PRODUCT_CATEGORY,ProductEntry.CATEGORY_BOOK);
        values.put(ProductEntry.COLUMN_PRICE, getResources().getInteger(R.integer.dummy_price));
        values.put(ProductEntry.COLUMN_QUANTITY_IN_STOCK, getResources().getInteger(R.integer.dummy_quantity_in_stock));
        values.put(ProductEntry.COLUMN_QUANTITY_ON_ORDER, getResources().getInteger(R.integer.dummy_quantity_on_order));
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, getString(R.string.dummy_supplier_name));
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, getString(R.string.dummy_supplier_phone));

        Log.i(LOG_TAG,"Content values: " + values);

        // Call the ContentResolver to insert values into the database table
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI,values);
        long rowID = ContentUris.parseId(newUri);

        if (newUri == null || rowID == -1){
            Toast.makeText(this,"Error inserting row",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"New row inserted",Toast.LENGTH_LONG).show();
        }

    }
}
