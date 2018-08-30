package com.example.android.abndbookstoreinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ProductContract {

    // Empty constructor to prevent instantiating the contract class
    private ProductContract() {}

    // Content authority for URI
    public static final String CONTENT_AUTHORITY = "com.example.android.abndbookstoreinventory.data";
    // Base to use for all Content URIs.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Path to the type of data
    public static final String PATH_PRODUCTS = "products";

    /* Inner class that defines the contents of the products table */
    public static final class ProductEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "products";

        // Column names
        // ID, primary key for the table
        public static final String COLUMN_ID = BaseColumns._ID;

        // product name
        public static final String COLUMN_PRODUCT_NAME = "product_name";

        // product description
        public static final String COLUMN_PRODUCT_DESCRIPTION = "product_description";

        // product category
        public static final String COLUMN_PRODUCT_CATEGORY = "category";

        // product price
        public static final String COLUMN_PRICE = "product_price";

        // product quantity in stock
        public static final String COLUMN_QUANTITY_IN_STOCK = "quantity_in_stock";

        // product quantity on order
        public static final String COLUMN_QUANTITY_ON_ORDER = "quantity_on_order";

        // product supplier name
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";

        // product supplier phone
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";

        // Category value constants
        public static final int CATEGORY_UNKNOWN = 0;
        public static final int CATEGORY_BOOK = 1;
        public static final int CATEGORY_PERIODICAL = 2;
        public static final int CATEGORY_E_DEVICE = 3;
        public static final int CATEGORY_OFFICE_SUPPLY = 4;

        // Constant for Content URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PRODUCTS);

        // Constant for the MIME type of the {@link #CONTENT_URI} for a list of products
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        // Constant for the MIME type of the {@link #CONTENT_URI} for a single prodduct
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        // Returns whether or not the given category is valid
        public static boolean isValidCategory (int category){
            switch(category){
                case CATEGORY_UNKNOWN:
                    return true;
                case CATEGORY_BOOK:
                    return true;
                case CATEGORY_PERIODICAL:
                    return true;
                case CATEGORY_E_DEVICE:
                    return true;
                case CATEGORY_OFFICE_SUPPLY:
                    return true;
                default:
                    return false;
            }
        }
    }
}
