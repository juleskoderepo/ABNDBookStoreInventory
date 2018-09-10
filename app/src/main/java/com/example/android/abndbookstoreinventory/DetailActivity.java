package com.example.android.abndbookstoreinventory;

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
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.abndbookstoreinventory.data.ProductContract.ProductEntry;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri currentProductUri;

    private EditText nameET;
    private Spinner categorySpinner;
    private EditText productDescriptionET;
    private EditText priceET;
    private EditText quantityInStockET;
    private EditText quantityOnOrderET;
    private EditText supplierNameET;
    private EditText supplierPhoneET;

    private int category = ProductEntry.CATEGORY_UNKNOWN;

    private static final int DETAIL_LOADER_ID = 901;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Use getIntent and getData to get the associated URI
        Intent openDetail = getIntent();
        currentProductUri = openDetail.getData();

        categorySpinner = findViewById(R.id.detail_category_spinner);
        setUpSpinner();

        // Set title of DetailActivity based on if ListView item is selected
        // or a new product is being added
        if(currentProductUri == null){
            // Update title for new product
            setTitle(getString(R.string.detail_title_new_product));
        } else {
            // Update title for selected product
            setTitle(getString(R.string.title_activity_detail));
            // initialize loader
            getLoaderManager().initLoader(DETAIL_LOADER_ID,null,this);
        }

        nameET = findViewById(R.id.detail_name);
        productDescriptionET = findViewById(R.id.detail_prod_description);
        priceET = findViewById(R.id.detail_price);
        quantityInStockET = findViewById(R.id.detail_quant_in_stock);
        quantityOnOrderET = findViewById(R.id.detail_quant_on_order);
        supplierNameET = findViewById((R.id.detail_supplier_name));
        supplierPhoneET = findViewById(R.id.detail_supplier_phone);

    }

    /**
     * Set up the dropdown spinner that allows user to select the product category
     */
    private void setUpSpinner(){
        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_category_options,android.R.layout.simple_spinner_item);

        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        categorySpinner.setAdapter(categorySpinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selection = (String) adapterView.getItemAtPosition(position);
                if(!TextUtils.isEmpty(selection)){
                    if(selection.equals(getString(R.string.category_book))){
                        category = ProductEntry.CATEGORY_BOOK;
                    } else if (selection.equals(getString(R.string.category_periodical))) {
                        category = ProductEntry.CATEGORY_PERIODICAL;
                    } else if (selection.equals(getString(R.string.category_e_device))){
                        category = ProductEntry.CATEGORY_E_DEVICE;
                    } else if (selection.equals(getString(R.string.category_office_supply))) {
                        category = ProductEntry.CATEGORY_OFFICE_SUPPLY;
                    } else {
                        category = ProductEntry.CATEGORY_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                category = ProductEntry.CATEGORY_UNKNOWN;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Insert product in table
                saveProduct();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Confirm delete with user
//                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
/*
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity) if the pet hasn't changed
                if (!petHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
*/

                // Otherwise if there are changes, set up a dialog to warn the user
                // Create a click listener to handle the user confirming that changes
                // should be discarded
/*
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked 'Discard'
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show dialog to notify user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // Hide the 'Delete' menu item if a new product
        if (currentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }

        return true;
    }


    private void saveProduct(){
        // Get values from fields, convert to String, and remove leading and trailing whitespace
        String prodName = nameET.getText().toString().trim();
        int prodCategory = category;
        String prodDesc = productDescriptionET.getText().toString().trim();
        String priceStr = priceET.getText().toString().trim();
        //convert priceStr value to integer to insert into table
        //TODO: add logic to convert decimal values to integer
        Integer prodPrice = 0;
        if(!priceStr.isEmpty()){
            prodPrice = Integer.parseInt(priceStr);
        }
        String quantInStockStr = quantityInStockET.getText().toString().trim();
        //convert Quantity In Stock String value to integer
        Integer prodQuantInStock = 0;
        if(!quantInStockStr.isEmpty()){
            prodQuantInStock = Integer.parseInt(quantInStockStr);
        }

        String quantOnOrder = quantityOnOrderET.getText().toString().trim();
        Integer prodQuantOnOrder = 0;
        if(!quantOnOrder.isEmpty()){
            prodQuantOnOrder = Integer.parseInt(quantOnOrder);
        }

        String supplierName = supplierNameET.getText().toString().trim();
        String supplierPhoneNum = supplierPhoneET.getText().toString().trim();

        // TODO: Add data validation and communicate back to user if data is invalid

        // Map of key-value pairs
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME,prodName);
        values.put(ProductEntry.COLUMN_PRODUCT_CATEGORY,prodCategory);
        values.put(ProductEntry.COLUMN_PRODUCT_DESCRIPTION,prodDesc);
        values.put(ProductEntry.COLUMN_PRICE,prodPrice);
        values.put(ProductEntry.COLUMN_QUANTITY_IN_STOCK,prodQuantInStock);
        values.put(ProductEntry.COLUMN_QUANTITY_ON_ORDER,prodQuantOnOrder);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME,supplierName);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE,supplierPhoneNum);
        // Insert new record
        if(currentProductUri==null){
            // Insert new row of values. Return new URI
            Uri newProdUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            // Parse ID returned in URI. If error on insert, -1 will be returned
            long newID = ContentUris.parseId(newProdUri);

            //TODO: Add toast for insert result
        } else /* Update existing record */ {
            int rowsUpdated = getContentResolver().update(currentProductUri,values,
                    null, null);
            //TODO: Add toast for update result
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // declare the projection and assign all the columns in the table to return
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_CATEGORY,
                ProductEntry.COLUMN_PRODUCT_DESCRIPTION,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY_IN_STOCK,
                ProductEntry.COLUMN_QUANTITY_ON_ORDER,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE
        };

        return new CursorLoader(this,
                currentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Exit early if cursor is null or empty
        if(cursor == null || cursor.getCount() < 1){
            return;
        }
        // Move cursor to position 0 before extracting values
        if(cursor.moveToFirst()){
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            nameET.setText(cursor.getString(nameColumnIndex));

            int categoryColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_CATEGORY);
            switch(cursor.getInt(categoryColumnIndex)){
                case ProductEntry.CATEGORY_UNKNOWN:
                    categorySpinner.setSelection(0);
                    break;
                case ProductEntry.CATEGORY_BOOK:
                    categorySpinner.setSelection(1);
                    break;
                case ProductEntry.CATEGORY_E_DEVICE:
                    categorySpinner.setSelection(2);
                    break;
                case ProductEntry.CATEGORY_OFFICE_SUPPLY:
                    categorySpinner.setSelection(3);
                    break;
                case ProductEntry.CATEGORY_PERIODICAL:
                    categorySpinner.setSelection(4);
                    break;
                default:
                    categorySpinner.setSelection(0);
                    break;
            }

            int prodDescColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_DESCRIPTION);
            productDescriptionET.setText(cursor.getString(prodDescColumnIndex));

            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            priceET.setText(String.valueOf(cursor.getInt(priceColumnIndex)));

            int quantInStockColumnIndex = cursor.getColumnIndex(
                    ProductEntry.COLUMN_QUANTITY_IN_STOCK);
            quantityInStockET.setText(String.valueOf(cursor.getInt(quantInStockColumnIndex)));

            int quantOnOrderColumnIndex = cursor.getColumnIndex(
                    ProductEntry.COLUMN_QUANTITY_ON_ORDER);
            quantityOnOrderET.setText(String.valueOf(cursor.getInt(quantOnOrderColumnIndex)));

            int supplierNameColumnIndex = cursor.getColumnIndex(
                    ProductEntry.COLUMN_SUPPLIER_NAME);
            supplierNameET.setText(cursor.getString(supplierNameColumnIndex));

            int supplierPhoneColumnIndex = cursor.getColumnIndex(
                    ProductEntry.COLUMN_SUPPLIER_PHONE);
            supplierPhoneET.setText(cursor.getString(supplierPhoneColumnIndex));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameET.setText("");
        categorySpinner.setSelection(0);
        productDescriptionET.setText("");
        priceET.setText("");
        quantityInStockET.setText("");
        quantityOnOrderET.setText("");
        supplierNameET.setText("");
        supplierPhoneET.setText("");

    }
}
