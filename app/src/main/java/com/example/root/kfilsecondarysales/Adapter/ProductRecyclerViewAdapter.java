package com.example.root.kfilsecondarysales.Adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.root.kfilsecondarysales.Modal.ProductModal;
import com.example.root.kfilsecondarysales.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 9/15/19.
 */

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder> {


    List<ProductModal> modalList;
    ArrayList<ProductModal> modalArrayList;
    Context mContext;

    public ProductRecyclerViewAdapter(List<ProductModal> modalList, Context mContext) {
        this.modalList = modalList;
        this.mContext = mContext;
        this.modalArrayList = new ArrayList<>();
        this.modalArrayList.addAll(modalList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_listitem_vertical,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.product_name.setText(modalList.get(position).getProductName());
        holder.quantity.setText(modalList.get(position).getQuantity());
        holder.date.setText(modalList.get(position).getDate());
        holder.unit.setText(modalList.get(position).getUnit());
    }

    @Override
    public int getItemCount() {
        return modalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView product_name,quantity,unit,date;
        public ViewHolder(View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.quantity);
            unit     = itemView.findViewById(R.id.unit);
            date     = itemView.findViewById(R.id.date);
        }
    }

    public void searchFilter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        modalList.clear();
        if(charText.length()== 0){
            modalList.addAll(modalArrayList);
        }else{
            for (ProductModal modal: modalArrayList) {
                if(modal.getProductName().toLowerCase(Locale.getDefault()).contains(charText)){
                    modalList.add(modal);
                }
            }
        }

        notifyDataSetChanged();

    }
}
