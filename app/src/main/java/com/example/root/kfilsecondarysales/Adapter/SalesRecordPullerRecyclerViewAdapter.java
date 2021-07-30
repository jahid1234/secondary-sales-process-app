package com.example.root.kfilsecondarysales.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;


import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;

import com.example.root.kfilsecondarysales.Modal.SalesRecordPullerModal;
import com.example.root.kfilsecondarysales.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.media.CamcorderProfile.get;

/**
 * Created by root on 10/7/19.
 */

public class SalesRecordPullerRecyclerViewAdapter extends RecyclerView.Adapter<SalesRecordPullerRecyclerViewAdapter.ViewHolder>  {

   // HashMap<String, String> availableProductQuantityHash = new HashMap<>();
    List<SalesRecordPullerModal> modalList;
    ArrayList<SalesRecordPullerModal> modalArrayList;
    Context mContext;

    public SalesRecordPullerRecyclerViewAdapter(List<SalesRecordPullerModal> modalList, Context mContext) {
        this.modalList = modalList;
        this.mContext = mContext;
      //  this.availableProductQuantityHash = availableProductQuantityHash;
        this.modalArrayList = new ArrayList<>();
        this.modalArrayList.addAll(modalList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.puller_sales_record_list_item,parent,false);
        return new SalesRecordPullerRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final List<String> printItem = new ArrayList<String>();
        holder.outletTextView.setText(modalList.get(position).getOutletName());
        holder.productTextView.setText(modalList.get(position).getProductName());
        holder.quantityTextView.setText(modalList.get(position).getQuantity());
        holder.unitTextView.setText(modalList.get(position).getUnit());

        printItem.add(modalList.get(position).getpKey());
        printItem.add(modalList.get(position).getOutletName());
        printItem.add(modalList.get(position).getProductName());
        printItem.add(modalList.get(position).getQuantity());
        printItem.add(modalList.get(position).getUnit());

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
                      //  if(!availableProductQuantityHash.isEmpty()) {
//                            String availableQty = availableProductQuantityHash.get(modalList.get(position).getProductName());
//                            String soldQuantity = modalList.get(position).getQuantity();
//                            int availQty = Integer.parseInt(availableQty);
//                            int soldQty = Integer.parseInt(soldQuantity);
//                            int restoreQuantity = availQty + soldQty;
//                            availableProductQuantityHash.put(modalList.get(position).getProductName(), String.valueOf(restoreQuantity));
//
//                            dbHelper.upadtePullerInventory(modalList.get(position).getProductName(), restoreQuantity);
                            dbHelper.deleteSalesRecord(modalList.get(position).getpKey());
                            modalList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, modalList.size());
                            dialog.dismiss();
                       // }
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

//        holder.tableRow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext,DocumentsToPrint.class);
//                intent.putStringArrayListExtra("printItem", (ArrayList<String>) printItem);
//                mContext.startActivity(intent);
//            }
//        });
    }



    @Override
    public int getItemCount() {
        return modalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView outletTextView,productTextView,quantityTextView,unitTextView;
        TableRow tableRow;
        public ViewHolder(View itemView){
            super(itemView);
            tableRow         = itemView.findViewById(R.id.record_row);
            outletTextView   = itemView.findViewById(R.id.outlet_name_of_sales);
            productTextView  = itemView.findViewById(R.id.product_name_of_sales);
            quantityTextView = itemView.findViewById(R.id.quantity_of_sales);
            unitTextView     = itemView.findViewById(R.id.unit_of_sales);
        }
    }

    public void searchFilter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        modalList.clear();
        if(charText.length()== 0){
            modalList.addAll(modalArrayList);
        }else{
            for (SalesRecordPullerModal modal: modalArrayList) {
                if(modal.getOutletName().toLowerCase(Locale.getDefault()).contains(charText)){
                    modalList.add(modal);
                }
            }
        }

        notifyDataSetChanged();

    }
}
