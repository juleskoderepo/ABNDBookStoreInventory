package com.example.android.abndbookstoreinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.abndbookstoreinventory.data.ProductContract.ProductEntry;

public class InventoryActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = InventoryActivity.class.getSimpleName();
    private static final int PROD_LOADER_ID = 900;

    ProductCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openDetail = new Intent(InventoryActivity.this, DetailActivity.class);
                startActivity(openDetail);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        // Find ListView and populate
        ListView productListView = findViewById(R.id.list_product);

        // Find and set the empty view on the list view when there are no items in the list
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        // Set up cursor adapter using cursor
        cursorAdapter = new ProductCursorAdapter(this, null, 0);
        // Attach cursor adapter to ListView
        productListView.setAdapter(cursorAdapter);

        // Open detail screen of item selected/clicked
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                    long id) {

                Log.i(LOG_TAG, "List item clicked");
                // Create a new intent to launch the DetailActivity
                Intent openDetail = new Intent(InventoryActivity.this,
                        DetailActivity.class);
                // Form the content URI to the specific product
                // Append the "id" onto the {@link ProductEntry#CONTENT_URI}
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                Log.i(LOG_TAG, "Uri passed in the intent is: " + currentProductUri);
                // Set the URI on the data field of the intent
                openDetail.setData(currentProductUri);
                // Launch the {@link DetailActivity} to display the detail for the current product
                startActivity(openDetail);
            }
        });

        // Initialize the CursorLoader
        getLoaderManager().initLoader(PROD_LOADER_ID, null, this);

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
            // Respond to click on 'Insert Dummy Data' menu option
            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;
            // Respond to click on 'Delete All Inventory' menu option
            case R.id.action_delete_all:
                showDeleteConfirmationDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to insert hardcoded values into db. For debugging purposes.
     */
    private void insertProduct() {
        // Create a map of values with column names as keys
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, getString(R.string.dummy_name));
        values.put(ProductEntry.COLUMN_PRODUCT_DESCRIPTION, "Guide book for visitors");
        values.put(ProductEntry.COLUMN_PRODUCT_CATEGORY, ProductEntry.CATEGORY_BOOK);
        values.put(ProductEntry.COLUMN_PRICE, getResources().getInteger(R.integer.dummy_price));
        values.put(ProductEntry.COLUMN_QUANTITY_IN_STOCK, getResources().getInteger(R.integer.dummy_quantity_in_stock));
        values.put(ProductEntry.COLUMN_QUANTITY_ON_ORDER, getResources().getInteger(R.integer.dummy_quantity_on_order));
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, getString(R.string.dummy_supplier_name));
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, getString(R.string.dummy_supplier_phone));

        Log.i(LOG_TAG, "Content values: " + values);

        // Call the ContentResolver to insert values into the database table
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        long rowID = ContentUris.parseId(newUri);

        if (newUri == null || rowID == -1) {
            Toast.makeText(this, "Error inserting row", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "New row inserted", Toast.LENGTH_LONG).show();
        }

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loader_id, @Nullable Bundle bundle) {
        // Declare and assign values for parameters needed for CursorLoader
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY_IN_STOCK
        };

        // Return CursorLoader that executes the content provider's query method on a
        // background thread
        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Moves the query results into the adapter, causing ListView fronting the adapter
        // to re-display
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Clear out the adapter's reference to the cursor to prevent memory leaks
        cursorAdapter.swapCursor(null);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and set click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_all_dialog_msg));
        builder.setPositiveButton(getString(R.string.delete_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // delete inventory
                deleteInventory();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked 'Cancel' so go back to activity
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    /**
     * Perform delete of all records from db table
     */
    private void deleteInventory() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI,
                String.valueOf(1), null);
        Toast.makeText(this, getString(R.string.rows_deleted) + rowsDeleted,
                Toast.LENGTH_LONG).show();

    }
}
