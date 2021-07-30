package com.example.root.kfilsecondarysales.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.root.kfilsecondarysales.Modal.RouteModal;
import com.example.root.kfilsecondarysales.Modal.SalesRecordModal;
import com.example.root.kfilsecondarysales.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 9/25/19.
 */

public class OutletRecyclerViewAdapter extends RecyclerView.Adapter<OutletRecyclerViewAdapter.ViewHolder> {

    List<SalesRecordModal> modalList;
    ArrayList<SalesRecordModal> modalArrayList;
    Context mContext;

    public OutletRecyclerViewAdapter(List<SalesRecordModal> modalList, Context mContext) {
        this.modalList = modalList;
        this.modalArrayList = new ArrayList<>();
        this.mContext = mContext;
        this.modalArrayList.addAll(modalList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outlet_wise_sales_record,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.outletTextView.setText(modalList.get(position).getOutletName());
        holder.productTextView.setText(modalList.get(position).getProductName());
       holder.quantityTextView.setText(modalList.get(position).getQuantity());
       holder.unitTextView.setText(modalList.get(position).getUnit());
    }

    @Override
    public int getItemCount() {
        return modalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView outletTextView,productTextView,quantityTextView,unitTextView;

        public ViewHolder(View itemView){
            super(itemView);
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
            for (SalesRecordModal modal: modalArrayList) {
                    if(modal.getOutletName().toLowerCase(Locale.getDefault()).contains(charText)){
                    modalList.add(modal);
                }
            }
        }

        notifyDataSetChanged();

    }
}
