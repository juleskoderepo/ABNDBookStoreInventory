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
        public static final String COLUMN_PRODUCT_NAME = "product_name";

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

    }
}
