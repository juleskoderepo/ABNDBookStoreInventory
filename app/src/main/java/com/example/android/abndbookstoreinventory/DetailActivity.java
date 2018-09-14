package com.example.android.abndbookstoreinventory;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.abndbookstoreinventory.data.ProductContract.ProductEntry;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri currentProductUri;

    private EditText nameET;
    private Spinner categorySpinner;
    private EditText productDescriptionET;
    private EditText priceET;
    private EditText quantityInStockET;
    private String quantInStockStr;
    private EditText quantityOnOrderET;
    private String quantOnOrderStr;
    private EditText supplierNameET;
    private EditText supplierPhoneET;
    private Button deleteButton;
    private Button orderButton;
    private Button qisAddButton;
    private Button qisSubtractButton;
    private Button qooAddButton;
    private Button qooSubtractButton;
    // Declare and initialize variable for category
    private int category = ProductEntry.CATEGORY_UNKNOWN;
    // Variable to store the changed state of the product details
    private boolean productHasChanged = false;
    // Listens for any user touches on a View, i.e. modifying a value, and
    // updates productHasChanged to true.
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }
    };

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
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
        if (currentProductUri == null) {
            // Update title for new product
            setTitle(getString(R.string.detail_title_new_product));
        } else {
            // Update title for selected product
            setTitle(getString(R.string.title_activity_detail));
            // initialize loader
            getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
        }

        nameET = findViewById(R.id.detail_name);
        productDescriptionET = findViewById(R.id.detail_prod_description);
        priceET = findViewById(R.id.detail_price);
        quantityInStockET = findViewById(R.id.detail_quant_in_stock);
        quantityOnOrderET = findViewById(R.id.detail_quant_on_order);
        supplierNameET = findViewById((R.id.detail_supplier_name));
        supplierPhoneET = findViewById(R.id.detail_supplier_phone);
        deleteButton = findViewById(R.id.delete_button);
        orderButton = findViewById(R.id.order_button);
        qisAddButton = findViewById(R.id.qis_increase_button);
        qisSubtractButton = findViewById(R.id.qis_decrease_button);
        qooAddButton = findViewById(R.id.qoo_increase_button);
        qooSubtractButton = findViewById(R.id.qoo_decrease_button);

        // Listen for quantity in stock add button click
        qisAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantInStockStr = quantityInStockET.getText().toString().trim();
                incrementQuantity(R.id.qis_increase_button);
            }
        });

        // Listen for quantity in stock subtract button click
        qisSubtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantInStockStr = quantityInStockET.getText().toString().trim();
                // Check that value is > 0 before decrementing
                if (Integer.parseInt(quantInStockStr) > 0) {
                    decrementQuantity(R.id.qis_decrease_button);
                } else {
                    Toast.makeText(DetailActivity.this,
                            getString(R.string.quantity_negative_value_warning),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Listen for quantity in stock add button click
        qooAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantOnOrderStr = quantityOnOrderET.getText().toString().trim();
                incrementQuantity(R.id.qoo_increase_button);
            }
        });

        // Listen for quantity in stock subtract button click
        qooSubtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantOnOrderStr = quantityOnOrderET.getText().toString().trim();
                // Check that value is > 0 before decrementing
                if (Integer.parseInt(quantOnOrderStr) > 0) {
                    decrementQuantity(R.id.qoo_decrease_button);
                } else {
                    Toast.makeText(DetailActivity.this,
                            getString(R.string.quantity_negative_value_warning),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize price and quantity fields for new product
        if (currentProductUri == null) {
            priceET.setText("0");
            quantityInStockET.setText("0");
            quantityOnOrderET.setText("0");
        }

        // Display the 'Delete' button only for an existing product
        // Enable the 'Order' button for an existing product with supplier phone number
        //  field populated.
        if (currentProductUri != null) {
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }
        // Call the showDeleteConfirmationDialog method when the 'Delete' button is clicked
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        // Disable 'Order' button by default
        orderButton.setEnabled(false);
        // Call the phoneInOrder method when the 'Order' button is clicked
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneInOrder();
            }
        });

        // Set up listeners to monitor changes on all editable fields
        nameET.setOnTouchListener(touchListener);
        categorySpinner.setOnTouchListener(touchListener);
        productDescriptionET.setOnTouchListener(touchListener);
        priceET.setOnTouchListener(touchListener);
        quantityInStockET.setOnTouchListener(touchListener);
        quantityOnOrderET.setOnTouchListener(touchListener);
        supplierNameET.setOnTouchListener(touchListener);
        supplierPhoneET.setOnTouchListener(touchListener);
        qisAddButton.setOnTouchListener(touchListener);
        qisSubtractButton.setOnTouchListener(touchListener);
        qooAddButton.setOnTouchListener(touchListener);
        qooSubtractButton.setOnTouchListener(touchListener);

    }

    /**
     * Set up the dropdown spinner that allows user to select the product category
     */
    private void setUpSpinner() {
        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_category_options, android.R.layout.simple_spinner_item);

        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        categorySpinner.setAdapter(categorySpinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selection = (String) adapterView.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.category_book))) {
                        category = ProductEntry.CATEGORY_BOOK;
                    } else if (selection.equals(getString(R.string.category_periodical))) {
                        category = ProductEntry.CATEGORY_PERIODICAL;
                    } else if (selection.equals(getString(R.string.category_e_device))) {
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
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity if the data hasn't changed
                if (!productHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
            // Otherwise if there are changes, set up a dialog to warn the user
            // Create a click listener to handle the user confirming that changes
            // should be discarded
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked 'Discard'
                                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                            }
                        };

                // Show dialog to notify user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
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
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Move cursor to position 0 before extracting values
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            nameET.setText(cursor.getString(nameColumnIndex));

            int categoryColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_CATEGORY);
            switch (cursor.getInt(categoryColumnIndex)) {
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

            // Enable the 'Order' button only if a phone number is present
            if (!supplierPhoneET.getText().toString().isEmpty()) {
                orderButton.setEnabled(true);
            }
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

    private void saveProduct() {
        // Get values from fields, convert to String, and remove leading and trailing whitespace
        String prodName = nameET.getText().toString().trim();
        int prodCategory = category;
        String prodDesc = productDescriptionET.getText().toString().trim();
        String priceStr = priceET.getText().toString().trim();
        //convert priceStr value to integer to insert into table
        //TODO: add logic to convert decimal values to integer
        Integer prodPrice = 0;
        if (!priceStr.isEmpty()) {
            prodPrice = Integer.parseInt(priceStr);
        }
        quantInStockStr = quantityInStockET.getText().toString().trim();
        //convert Quantity In Stock String value to integer
        Integer prodQuantInStock = 0;
        if (!quantInStockStr.isEmpty()) {
            prodQuantInStock = Integer.parseInt(quantInStockStr);
        }

        quantOnOrderStr = quantityOnOrderET.getText().toString().trim();
        Integer prodQuantOnOrder = 0;
        if (!quantOnOrderStr.isEmpty()) {
            prodQuantOnOrder = Integer.parseInt(quantOnOrderStr);
        }

        String supplierName = supplierNameET.getText().toString().trim();
        String supplierPhoneNum = supplierPhoneET.getText().toString().trim();

        // Map of key-value pairs
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, prodName);
        values.put(ProductEntry.COLUMN_PRODUCT_CATEGORY, prodCategory);
        values.put(ProductEntry.COLUMN_PRODUCT_DESCRIPTION, prodDesc);
        values.put(ProductEntry.COLUMN_PRICE, prodPrice);
        values.put(ProductEntry.COLUMN_QUANTITY_IN_STOCK, prodQuantInStock);
        values.put(ProductEntry.COLUMN_QUANTITY_ON_ORDER, prodQuantOnOrder);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneNum);
        // Insert new record
        if (currentProductUri == null) {
            // Insert new row of values. Return new URI
            Uri newProdUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            if (newProdUri != null) {
                // Parse ID returned in URI. If error on insert, -1 will be returned
                long newId = ContentUris.parseId(newProdUri);
                if (newId == -1) {
                    Toast.makeText(this, getString(R.string.save_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.save_successful),
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        } else /* Update existing record */ {
            int rowsUpdated = getContentResolver().update(currentProductUri, values,
                    null, null);
            if (rowsUpdated == 1) {
                Toast.makeText(this, getString(R.string.save_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the 'Keep editing' button, so dismiss the dialog and
                // continue editing.
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, set up a dialog to the user
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked the 'Discard' button, close the current activity
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        // Set up positive button
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {
                // 'Delete' button clicked, delete product
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 'Cancel' clicked, so dismiss dialog and continue editing product
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (currentProductUri != null) {
            int rowDeleted = getContentResolver().delete(currentProductUri,
                    null,
                    null);

            if (rowDeleted == 0) {
                Toast.makeText(this, getString(R.string.edit_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.edit_delete_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Exit activity
        finish();
    }

    private void incrementQuantity(int buttonId) {
        switch (buttonId) {
            case R.id.qis_increase_button:
                //quantInStockStr = quantityInStockET.getText().toString().trim();
                Log.i(LOG_TAG + ".incrementQuantity()",
                        "Quantity in stock string value is: " + quantInStockStr);
                int quantityInStock = Integer.parseInt(quantInStockStr);
                Log.i(LOG_TAG + ".incrementQuantity()",
                        "Quantity in stock int value is: " + quantityInStock);
                quantityInStock += 1;
                Log.i(LOG_TAG + ".incrementQuantity()",
                        "Quantity in stock incremented value is: " + quantityInStock);
                quantityInStockET.setText(String.valueOf(quantityInStock));
                break;
            case R.id.qoo_increase_button:
                int quantityOnOrder = Integer.parseInt(quantOnOrderStr);
                quantityOnOrder += 1;
                quantityOnOrderET.setText(String.valueOf(quantityOnOrder));
                break;
            default:
                break;
        }
    }

    private void decrementQuantity(int buttonId) {
        switch (buttonId) {
            case R.id.qis_decrease_button:
                // Convert field value (String) to int so calculation can be performed on it
                int quantityInStock = Integer.parseInt(quantInStockStr);
                quantityInStock -= 1;
                quantityInStockET.setText(String.valueOf(quantityInStock));
                break;
            case R.id.qoo_decrease_button:
                int quantityOnOrder = Integer.parseInt(quantOnOrderStr);
                quantityOnOrder -= 1;
                quantityOnOrderET.setText(String.valueOf(quantityOnOrder));
                break;
            default:
                break;
        }
    }

    private void phoneInOrder() {
        if (supplierPhoneET.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.order_phone_num_required_msg),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + supplierPhoneET.getText().toString()));
        if (dialIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(dialIntent);
        }
    }

}
