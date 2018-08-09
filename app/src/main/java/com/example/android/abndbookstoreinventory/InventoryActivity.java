package com.example.android.abndbookstoreinventory;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.math.BigDecimal;

import com.example.android.abndbookstoreinventory.data.ProductDbHelper;
import com.example.android.abndbookstoreinventory.data.ProductContract.ProductEntry;

public class InventoryActivity extends AppCompatActivity {

    ProductDbHelper prodDbHelper;

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

        switch(id){
            case R.id.action_insert_dummy_data:
                insertProduct();

                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to insert hardcoded values into db
     */
    private void insertProduct(){
        // Get database in write mode
        SQLiteDatabase db = prodDbHelper.getWritableDatabase();

        // Create a map of values with column names as keys
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, getString(R.string.dummy_name));

        int price = Integer.parseInt(getString(R.string.dummy_price));
        values.put(ProductEntry.COLUMN_PRICE, price);

        int quantity = Integer.parseInt(getString(R.string.dummy_quantity));
        values.put(ProductEntry.COLUMN_QUANTITY, quantity);

        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, getString(R.string.dummy_supplier_name));
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, getString(R.string.dummy_supplier_phone));

        Log.i("InventoryActivity","Content values: " + values);
        // Insert the new row, returning the primary key value of the new row i.e. _id
        // Returns -1 if error occurs inserting data
        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);


    }
}
