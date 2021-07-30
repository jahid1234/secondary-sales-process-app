package com.example.root.kfilsecondarysales.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;
import com.example.root.kfilsecondarysales.Modal.OrderModal;
import com.example.root.kfilsecondarysales.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 10/14/19.
 */

public class OrderRecordDealerRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecordDealerRecyclerViewAdapter.ViewHolder>  {

    Context mContext;
    List<OrderModal> orderModalList;
    ArrayList<OrderModal> orderModalArrayList;

    public OrderRecordDealerRecyclerViewAdapter(Context mContext, List<OrderModal> orderModalList) {
        this.mContext = mContext;
        this.orderModalList = orderModalList;
        this.orderModalArrayList = new ArrayList<>();
        this.orderModalArrayList.addAll(orderModalList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dealer_order_record_list_item,parent,false);
        return new OrderRecordDealerRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.productTextView.setText(orderModalList.get(position).getProductName());
        holder.quantityTextView.setText(orderModalList.get(position).getQuantity());
        holder.unitTextView.setText(orderModalList.get(position).getUnit());

        holder.tableRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final SqliteDbHelper dbHelper = new SqliteDbHelper(mContext);

                final Dialog dialog = new Dialog(mContext);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.custom_delete_dialog);

                Button yesBtn = dialog.findViewById(R.id.dialogButtonOK);
                Button noBtn  = dialog.findViewById(R.id.cancel);

                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbHelper.deleteOrderRecord(orderModalList.get(position).getpKey());
                        orderModalList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,orderModalList.size());
                        dialog.dismiss();

                    }
                });

                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderModalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView productTextView,quantityTextView,unitTextView;
        TableRow tableRow;
        public ViewHolder(View itemView){
            super(itemView);
            tableRow         = itemView.findViewById(R.id.order_record_row);
            productTextView  = itemView.findViewById(R.id.product_name_order);
            quantityTextView = itemView.findViewById(R.id.quantity_of_order);
            unitTextView     = itemView.findViewById(R.id.unit_of_order);
        }
    }

    public void searchFilter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        orderModalList.clear();
        if(charText.length()== 0){
            orderModalList.addAll(orderModalArrayList);
        }else{
            for (OrderModal modal: orderModalArrayList) {
                if(modal.getProductName().toLowerCase(Locale.getDefault()).contains(charText)){
                    orderModalList.add(modal);
                }
            }
        }

        notifyDataSetChanged();

    }
}
