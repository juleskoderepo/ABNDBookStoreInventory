package com.example.android.abndbookstoreinventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.abndbookstoreinventory.data.ProductContract;

public class ProductCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ProductCursorAdapter}
     *
     * @param context The context
     * @param cursor The cursor from which to get the data
     * @param flags Flag to determine behavior of the adpater. No behavior specified = 0
     */
    public ProductCursorAdapter(Context context,
                                Cursor cursor,
                                int flags){
        super(context, cursor, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet
     *
     * @param context app context
     * @param cursor The cursor from which to get the data
     * @param viewGroup The parent to which the new view is attached
     * @return The newly created list item view
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,viewGroup,false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name
     * TextView in the list item layout.
     *
     * @param view Existing view, returned by newView() method
     * @param context app context
     * @param cursor The cursor from which to get the data. The cursor is already moved to the
     *               correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView nameTV = (TextView) view.findViewById(R.id.product_name);
        TextView priceTV = (TextView) view.findViewById(R.id.price);
        TextView qisTV = (TextView) view.findViewById(R.id.quantity_in_stock);

        // Find the columns of product attributes that we want
        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE);
        int qisColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY_IN_STOCK);

        // Extract properties from cursor
        String productName = cursor.getString(nameColumnIndex);
        Integer productPrice = cursor.getInt(priceColumnIndex);
        Integer quantityInStock = cursor.getInt(qisColumnIndex);

        // Populate fields with extracted properties
        nameTV.setText(productName);
        priceTV.setText(Integer.toString(productPrice));
        qisTV.setText(Integer.toString(quantityInStock));
    }
}
