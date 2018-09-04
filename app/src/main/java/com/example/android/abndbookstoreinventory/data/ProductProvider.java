package com.example.android.abndbookstoreinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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
     * Returns the MIME type of data for the content URI
     * @param uri uri value passed to the content provider
     * @return
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
        // TODO: Add validation

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

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        // Notify listeners that the data has changed for the product content URI
        // if a row is deleted
        getContext().getContentResolver().notifyChange(uri,null);
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        // Notify listeners that the data has changed for the product content URI
        // if a row is updated
        getContext().getContentResolver().notifyChange(uri,null);

        return 0;
    }
}
