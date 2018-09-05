package com.example.android.abndbookstoreinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name
     * TextView in the list item layout.
     *
     * @param view Existing view, returned by newView() method
     * @param context app context
     * @param cursor The cursor from which to get the data. The cursor is already moved to the
     *               correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ProductViewHolder holder;

        holder = new ProductViewHolder();

        // Find fields to populate in inflated template
        holder.nameTV = (TextView) view.findViewById(R.id.product_name);
        holder.priceTV = (TextView) view.findViewById(R.id.price);
        holder.qisTV = (TextView) view.findViewById(R.id.quantity_in_stock);
        holder.saleButton = (Button) view.findViewById(R.id.sale_button);

        // store the holder with the view
        view.setTag(holder);

        // Find the columns of product attributes that we want
        int idColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_ID);
        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE);
        int qisColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY_IN_STOCK);

        // Extract properties from cursor
        final Integer id = cursor.getInt(idColumnIndex);
        String productName = cursor.getString(nameColumnIndex);
        Integer productPrice = cursor.getInt(priceColumnIndex);
        final Integer quantityInStock = cursor.getInt(qisColumnIndex);

        // Populate fields with extracted properties
        holder.nameTV.setText(productName);
        holder.priceTV.setText(Integer.toString(productPrice));
        holder.qisTV.setText(Integer.toString(quantityInStock));
        holder.saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer updatedQuantity;

                if(quantityInStock > 0){
                    updatedQuantity = quantityInStock - 1;

                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_QUANTITY_IN_STOCK,
                            updatedQuantity);

                    Uri currentProductUri = ContentUris.withAppendedId(
                            ProductContract.ProductEntry.CONTENT_URI, id);

                    int rowsUpdated = context.getContentResolver().update(
                            currentProductUri,
                            values,
                            null,
                            null
                    );

                    if(rowsUpdated == 0){
                        Toast.makeText(context,"Error updating product quantity.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,"Sale recorded.",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context,"Sale invalid. Product is not in-stock.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // ViewHolder for the list_item layout
    static class ProductViewHolder{
        TextView nameTV;
        TextView priceTV;
        TextView qisTV;
        Button saleButton;
    }

}
