package com.example.android.abndbookstoreinventory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.abndbookstoreinventory.data.ProductDbHelper;
import com.example.android.abndbookstoreinventory.data.ProductContract.ProductEntry;

import java.math.BigDecimal;

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
        // Create and/or open a database to read from
        SQLiteDatabase db = prodDbHelper.getReadableDatabase();

        // Define the db query to get a Cursor that contains all the rows from the products table
        Cursor productsCursor = db.query(ProductEntry.TABLE_NAME, null, null, null, null, null, null);

        try {
            int productsCount = productsCursor.getCount();
            TextView displayView = findViewById(R.id.text_view_inventory);
            String tableSummary;

            if (productsCount == 1) {
                tableSummary = String.format(getString(R.string.products_table_contains),
                        productsCursor.getCount(),
                        getString(R.string.product));
                displayView.setText(tableSummary);
            } else {
                tableSummary = String.format(getString(R.string.products_table_contains),
                        productsCursor.getCount(),
                        getString(R.string.products));
                displayView.setText(tableSummary);
            }

            displayView.append(ProductEntry.COLUMN_ID + " - " +
                    ProductEntry.COLUMN_PRODUCT_NAME + " - " +
                    ProductEntry.COLUMN_PRICE + " - " +
                    ProductEntry.COLUMN_QUANTITY + " - " +
                    ProductEntry.COLUMN_SUPPLIER_NAME + " - " +
                    ProductEntry.COLUMN_SUPPLIER_PHONE + "\n\n");

            // Get the index position for each column in the table
            int idColIndex = productsCursor.getColumnIndex(ProductEntry.COLUMN_ID);
            int prodNameColIndex = productsCursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColIndex = productsCursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int quantityColIndex = productsCursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            int supplierNameColIndex = productsCursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColIndex = productsCursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE);

            // Iterate through the rows in the cursor, return columns values, and display
            // values on screen

            while (productsCursor.moveToNext()) {
                int currentId = productsCursor.getInt(idColIndex);
                String currentProduct = productsCursor.getString(prodNameColIndex);
                int currentPrice = productsCursor.getInt(priceColIndex);
                int currentQuantity = productsCursor.getInt(quantityColIndex);
                String currentSupplier = productsCursor.getString(supplierNameColIndex);
                String currentSupplierPhone = productsCursor.getString(supplierPhoneColIndex);

                // Convert integer price value to currency value
                BigDecimal formattedPrice = BigDecimal.valueOf(currentPrice);
                formattedPrice = formattedPrice.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);

                displayView.append(currentId + " - " +
                        currentProduct + " - " +
                        formattedPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplier + " - " +
                        currentSupplierPhone + "\n");
            }

        } finally {
            // Release the cursor resources and invalidate it
            productsCursor.close();
        }

    }

    /**
     * Helper method to insert hardcoded values into db. For debugging purposes.
     */
    private void insertProduct() {
        // Get database in write mode
        SQLiteDatabase db = prodDbHelper.getWritableDatabase();

        // Create a map of values with column names as keys
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, getString(R.string.dummy_name));
        values.put(ProductEntry.COLUMN_PRICE, getResources().getInteger(R.integer.dummy_price));
        values.put(ProductEntry.COLUMN_QUANTITY, getResources().getInteger(R.integer.dummy_quantity));
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, getString(R.string.dummy_supplier_name));
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, getString(R.string.dummy_supplier_phone));

        // Insert the new row. Returns the primary key value of the new row i.e. _id
        // or -1 if error occurs inserting data
        db.insert(ProductEntry.TABLE_NAME, null, values);

    }
}
