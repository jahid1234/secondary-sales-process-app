package com.example.root.kfilsecondarysales.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.root.kfilsecondarysales.Modal.OutletDiscountNameModal;
import com.example.root.kfilsecondarysales.R;

import java.util.List;

/**
 * Created by root on 12/22/19.
 */

public class DiscountNameRecyclerViewAdapter extends RecyclerView.Adapter<DiscountNameRecyclerViewAdapter.ViewHolder> {
    Context mContext;
    List<OutletDiscountNameModal> outletDiscountNameModals;

    public DiscountNameRecyclerViewAdapter(Context mContext, List<OutletDiscountNameModal> outletDiscountNameModals) {
        this.mContext = mContext;
        this.outletDiscountNameModals = outletDiscountNameModals;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discount_name_list,parent,false);
        return new DiscountNameRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.discountName.setText(String.valueOf(position+1) +" . "+ outletDiscountNameModals.get(position).getDiscountName());
    }

    @Override
    public int getItemCount() {
        return outletDiscountNameModals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView discountName;
        public ViewHolder(View itemView) {
            super(itemView);
            discountName = itemView.findViewById(R.id.discount_name);
        }
    }
}
