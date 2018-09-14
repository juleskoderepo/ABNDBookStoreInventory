package com.example.android.abndbookstoreinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.android.abndbookstoreinventory.DetailActivity;
import com.example.android.abndbookstoreinventory.R;

public class ProductProvider extends ContentProvider {

    // Tag for log messages
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    ProductDbHelper prodDbHelper;

    // Code constants associated with the valid URI patterns
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;

    // exception string constants
    private static final String QUERY_EXCEPTION = "Cannot query unknown URI";
    private static final String INSERT_NOT_SUPPORTED_EXCEPTION = "Insert not supported for URI ";
    private static final String DELETE_EXCEPTION = "Deletion not supported for URI: ";
    private static final String UPDATE_EXCEPTION = "Update not supported for URI: ";

    // Create the UriMatcher
    private static final UriMatcher productUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Add URI patterns that ContentProvider will accept to the URI matcher
    static {
        // URI for all products
        productUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,
                ProductContract.PATH_PRODUCTS,PRODUCTS);
        // URI for a single product
        productUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,
                ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }


    @Override
    public boolean onCreate() {
        prodDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    /**
     * Query the db table if the URI is valid.
     *
     * @param uri URI used to perform query
     * @param projection Columns to be returned in query results
     * @param selection Columns to be used in WHERE clause of query statement
     * @param selectionArgs Values corresponding to the columns in the WHERE clause
     * @param sortOrder The way results are to be sorted when returned
     * @return Cursor containing db query results
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // get readable db
        SQLiteDatabase db = prodDbHelper.getReadableDatabase();

        // declare cursor to hold query result
        Cursor cursor;

        // determine if URI is valid and then follow the correct path to perform the query
        int match = productUriMatcher.match(uri);

        switch(match){
            case PRODUCTS:
                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, projection,
                        selection, selectionArgs,null, null, sortOrder);
                break;
            case PRODUCT_ID:
                // extract out the product ID from the URI
                // specify the value to prevent SQL injection
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(QUERY_EXCEPTION + uri);
        }

        // Set notification URI on the cursor, so we know what content URI
        // the cursor was created for. If the data at this URI changes, then we need
        // to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        // Return the cursor
        return cursor;
    }

    /**
     * Returns the MIME type of data at the content URI
     * @param uri uri value passed to the content provider
     * @return MIME type of the data for either a single record or multiple items
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = productUriMatcher.match(uri);
        switch(match){
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri + "with code" + match);
        }
    }

    /**
     * Insert new data into the db through the provider with the given ContentValues
     * if the URI is valid.
     * This method calls a helper method to perform the actual insert
     *
     * @param uri content URI used to perform the db insert
     * @param contentValues values to insert into the db
     * @return new URI appended with ID assigned to the inserted row
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = productUriMatcher.match(uri);
        switch(match){
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException(INSERT_NOT_SUPPORTED_EXCEPTION + uri);
        }
    }

    /**
     * Helper method to perform db insert.
     * @param uri content URI used to perform the db insert
     * @param contentValues values to insert into the db
     * @return new URI appended with ID assigned to the inserted row
     */
    private Uri insertProduct(Uri uri, ContentValues contentValues){
        if(!validateData(contentValues)){
            return null;
        };

        // get a writable db instance
        SQLiteDatabase db = prodDbHelper.getWritableDatabase();

        // Insert new row. Returns row ID of the new row inserted or -1 if unsuccessful
        long newRowId = db.insert(ProductContract.ProductEntry.TABLE_NAME,null,
                contentValues);

        // Log error if newRowId = -1 meaning insert failed
        if (newRowId == -1){
            Log.e(LOG_TAG,"Failed to insert row for URI: " + uri);
        }

        // Notify listeners that the data has changed for the product content URI
        getContext().getContentResolver().notifyChange(uri,null);

        // return the new URI with the ID assigned to the new row
        return ContentUris.withAppendedId(uri,newRowId);
    }

    /**
     * Delete row or rows given the selection and selectionArgs passed in
     * if the URI is valid.
     *
     * @param uri Content URI used to perform the delete
     * @param selection Column to be used in the WHERE clause of the delete statement
     * @param selectionArgs Value corresponding to the column in the WHERE clause
     * @return The number of rows deleted.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        // Get writable db
        SQLiteDatabase db = prodDbHelper.getWritableDatabase();
        // number of rows deleted
        int rowsDeleted;

        final int match = productUriMatcher.match(uri);
        switch(match){
            case PRODUCTS:
                // Perform delete of all records
                rowsDeleted = db.delete(ProductContract.ProductEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Specify product to delete given the ID from the URI
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // Perform delete of specified product
                rowsDeleted = db.delete(ProductContract.ProductEntry.TABLE_NAME,
                        selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(DELETE_EXCEPTION + uri);
        }

        // Notify listeners that the data has changed for the product content URI
        // if a row is deleted
        getContext().getContentResolver().notifyChange(uri,null);

        return rowsDeleted;
    }

    /**
     * Update data in the db through the provider with the given ContentValues
     * if the URI is valid.
     * This method calls a helper method to perform the actual update.
     *
     * @param uri Content URI used to perform db update
     * @param contentValues Key-value pairs of table columns and corresponding values to update.
     * @param selection Columns to be used in the WHERE clause of the update statement.
     *                  In this case, the ID column will be used to identify the specific row
     *                  to be updated.
     * @param selectionArgs Values corresponding to the columns to be used in the WHERE clause.
     *                      In this case, the ID value will be parsed from URI passed in.
     * @return The number of rows updated.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = productUriMatcher.match(uri);
        switch(match){
            case PRODUCTS:
                return updateProduct(uri,contentValues,selection,selectionArgs);
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return updateProduct(uri,contentValues,selection,selectionArgs);
            default:
                throw new IllegalArgumentException(UPDATE_EXCEPTION + uri);
        }
    }

    /**
     * Helper method to perform db update.
     *
     * @param uri Content URI used to perform db update
     * @param contentValues Key-value pairs of table columns and corresponding values to update.
     * @param selection Columns to be used in the WHERE clause of the update statement.
     *                  In this case, the ID column will be used to identify the specific row
     *                  to be updated.
     * @param selectionArgs Values corresponding to the columns to be used in the WHERE clause.
     *                      In this case, the ID value will be parsed from URI passed in.
     * @return The number of rows updated.
     */
    private int updateProduct(Uri uri, ContentValues contentValues, String selection,
                              String[] selectionArgs){

        // Return 0 (rows updated) if there are no columns and corresponding values passed in
        if(contentValues.size() == 0){
            return 0;
        }
        // Validate all data fields when save initiated from detail screen
        if(contentValues.size() > 1) {
            // Return 0 (rows updated) if any invalid data is detected
            if (!validateData(contentValues)) {
                return 0;
            }
        }

        // Get writable db
        SQLiteDatabase db = prodDbHelper.getWritableDatabase();
        // Perform update
        int rowsUpdated = db.update(ProductContract.ProductEntry.TABLE_NAME, contentValues,
                selection, selectionArgs);

        if(rowsUpdated != 0){
            // Notify listeners that the data has changed for the product content URI
            // if a row is updated
            getContext().getContentResolver().notifyChange(uri,null);
        }

        // Return number of rows updated
        return rowsUpdated;

    }

    /**
     * Determine if data values are null or empty or otherwise invalid.
     * Display a toast message informing the user of correct data entry.
     *
     * @param contentValues Key-value pairs of table columns and corresponding values to be validated
     * @return true, if all values are valid, or false, if any value is invalid
     */
    private boolean validateData(ContentValues contentValues){
        boolean dataIsValid = false;
        // Validate that all values are not null or empty
        String name = contentValues.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        Integer category = contentValues.getAsInteger(
                ProductContract.ProductEntry.COLUMN_PRODUCT_CATEGORY);
        String description = contentValues.getAsString(
                ProductContract.ProductEntry.COLUMN_PRODUCT_DESCRIPTION);
        Integer price = contentValues.getAsInteger(ProductContract.ProductEntry.COLUMN_PRICE);
        Integer quantityInStock = contentValues.getAsInteger(
                ProductContract.ProductEntry.COLUMN_QUANTITY_IN_STOCK);
        Integer quantityOnOrder = contentValues.getAsInteger(
                ProductContract.ProductEntry.COLUMN_QUANTITY_ON_ORDER);
        String supplierName = contentValues.getAsString(
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
        String supplierPhone = contentValues.getAsString(
                ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE);

        if(name == null || name.isEmpty()){
            Toast.makeText(getContext(),"Please enter a name before saving.",
                    Toast.LENGTH_SHORT).show();
            return dataIsValid;
        }

        if(category == null || !ProductContract.ProductEntry.isValidCategory(category)) {
            Log.i(LOG_TAG,"Category in contentValues is: " + category);
            Toast.makeText(getContext(),"Please select a valid category",
                    Toast.LENGTH_SHORT).show();
            return dataIsValid;
        }

        if(description == null || description.isEmpty()){
            Toast.makeText(getContext(), "Please enter a product description",
                    Toast.LENGTH_SHORT).show();
            return dataIsValid;
        }

        if(price == null || price < 0){
            Toast.makeText(getContext(), "Please enter a valid price",
                    Toast.LENGTH_SHORT).show();
            return dataIsValid;
        }

        if(quantityInStock == null || quantityInStock < 0){
            Toast.makeText(getContext(), "Please enter a valid quantity in stock",
                    Toast.LENGTH_SHORT).show();
            return dataIsValid;
        }

        if(quantityOnOrder == null || quantityOnOrder < 0){
            Toast.makeText(getContext(), "Please enter a valid quantity on order",
                    Toast.LENGTH_SHORT).show();
            return dataIsValid;
        }

        if(supplierName == null || supplierName.isEmpty()){
            Toast.makeText(getContext(), "Please enter supplier name",
                    Toast.LENGTH_SHORT).show();
            return dataIsValid;
        }

        if(supplierPhone == null || supplierPhone.isEmpty()){
            Toast.makeText(getContext(), "Please enter supplier phone number",
                    Toast.LENGTH_SHORT).show();
            return dataIsValid;
        }

        dataIsValid = true;
        return dataIsValid;
    }
}
