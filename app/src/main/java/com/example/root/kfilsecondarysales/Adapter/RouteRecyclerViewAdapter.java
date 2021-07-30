package com.example.root.kfilsecondarysales.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.root.kfilsecondarysales.Activity.SalesOutletList;
import com.example.root.kfilsecondarysales.Modal.ProductModal;
import com.example.root.kfilsecondarysales.Modal.RouteModal;
import com.example.root.kfilsecondarysales.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 9/24/19.
 */

public class RouteRecyclerViewAdapter extends RecyclerView.Adapter<RouteRecyclerViewAdapter.ViewHolder> {


    List<RouteModal> modalList;
    ArrayList<RouteModal> modalArrayList;
    Context mContext;

    public RouteRecyclerViewAdapter(List<RouteModal> modalList, Context mContext) {
        this.modalList = modalList;
        this.mContext = mContext;
        this.modalArrayList = new ArrayList<>();
        this.modalArrayList.addAll(modalList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_listitem_vertical,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.routeList.setText(modalList.get(position).getRouteName());
        holder.routeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SalesOutletList.class);
                intent.putExtra("routeName",modalList.get(position).getRouteName());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return modalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView routeList;
        public ViewHolder(View itemView) {
            super(itemView);
            routeList = itemView.findViewById(R.id.routeList_TextId);
        }
    }


    public void searchFilter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        modalList.clear();
        if(charText.length()== 0){
            modalList.addAll(modalArrayList);
        }else{
            for (RouteModal modal: modalArrayList) {
                if(modal.getRouteName().toLowerCase(Locale.getDefault()).contains(charText)){
                    modalList.add(modal);
                }
            }
        }

        notifyDataSetChanged();

    }
}
