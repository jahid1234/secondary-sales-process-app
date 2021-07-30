package com.example.root.kfilsecondarysales.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.root.kfilsecondarysales.Modal.ProductModalOfOrderActivity;
import com.example.root.kfilsecondarysales.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 11/3/19.
 */

public class DealerOrderOfProductRecyclerViewAdapter extends RecyclerView.Adapter<DealerOrderOfProductRecyclerViewAdapter.ViewHolder> {

    Context mContext;
    ArrayList<ProductModalOfOrderActivity> modalArrayList;
    List<ProductModalOfOrderActivity> productModalOfOrderActivityList;

    public DealerOrderOfProductRecyclerViewAdapter(Context mContext, List<ProductModalOfOrderActivity> productModalOfOrderActivityList) {
        this.mContext = mContext;
        this.productModalOfOrderActivityList = productModalOfOrderActivityList;
        this.modalArrayList = new ArrayList<>();
        this.modalArrayList.addAll(productModalOfOrderActivityList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_of_dealer_order_recyclerview,parent,false);
        return new DealerOrderOfProductRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(productModalOfOrderActivityList.get(position).getCheckStatus()){
            holder.checkBox.setChecked(true);
        }else {
            holder.checkBox.setChecked(false);
        }

        holder.pName_textView.setText(productModalOfOrderActivityList.get(position).getP_name());
        holder.qty_editText.setText(productModalOfOrderActivityList.get(position).getQty());
        holder.unit_textView.setText(productModalOfOrderActivityList.get(position).getP_unit());
        holder.price_textView.setText(productModalOfOrderActivityList.get(position).getP_price());
        holder.totalPrice_textView.setText(String.valueOf(productModalOfOrderActivityList.get(position).getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return productModalOfOrderActivityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        TextView pName_textView,unit_textView,price_textView,totalPrice_textView;
        EditText qty_editText;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox       = itemView.findViewById(R.id.checkbox_confirm_order);
            pName_textView = itemView.findViewById(R.id.order_product_name);
            qty_editText   = itemView.findViewById(R.id.carton_amount_order);
            unit_textView  = itemView.findViewById(R.id.product_unit);
            price_textView = itemView.findViewById(R.id.price);
            totalPrice_textView = itemView.findViewById(R.id.total_price_of_product);


            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBox.isChecked()){
                        productModalOfOrderActivityList.get(getAdapterPosition()).setCheckStatus(true);
                    }else{
                        productModalOfOrderActivityList.get(getAdapterPosition()).setCheckStatus(false);
                    }
                }
            });


            qty_editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    productModalOfOrderActivityList.get(getAdapterPosition()).setQty(qty_editText.getText().toString());
                    totalPrice_textView.setText(String.valueOf(productModalOfOrderActivityList.get(getAdapterPosition()).getTotalPrice()));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
         }
    }


    public void searchFilter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        productModalOfOrderActivityList.clear();
        if(charText.length()== 0){
            productModalOfOrderActivityList.addAll(modalArrayList);
        }else{
            for (ProductModalOfOrderActivity modal: modalArrayList) {
                if(modal.getP_name().toLowerCase(Locale.getDefault()).contains(charText)){
                    productModalOfOrderActivityList.add(modal);
                }
            }
        }

        notifyDataSetChanged();

    }
}

