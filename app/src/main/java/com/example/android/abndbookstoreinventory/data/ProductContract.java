package com.example.android.abndbookstoreinventory.data;

import android.provider.BaseColumns;

public final class ProductContract {

    /* Inner class that defines the contents of the products table */
    public static final class ProductEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "products";

        // Column names
        // ID, primary key for the table
        public static final String COLUMN_ID = BaseColumns._ID;

        // product name
        public static final String COLUMN_PRODUCT_NAME = "productName";

        // product price
        public static final String COLUMN_PRICE = "productPrice";

        // product quantity
        public static final String COLUMN_QUANTITY = "productQuantity";

        // product supplier name
        public static final String COLUMN_SUPPLIER_NAME = "supplierName";

        // product supplier phone
        public static final String COLUMN_SUPPLIER_PHONE = "supplierPhone";

    }
}
