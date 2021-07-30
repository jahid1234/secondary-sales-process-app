package com.example.root.kfilsecondarysales.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.root.kfilsecondarysales.Modal.AsoToDealerProductModal;
import com.example.root.kfilsecondarysales.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 10/28/19.
 */

public class InsertProductToDealerRecyclerViewAdapter extends RecyclerView.Adapter<InsertProductToDealerRecyclerViewAdapter.ViewHolder>  {

    private static final String TAG = "InsertProductToDealerRe";
    Context mContext;
    List<AsoToDealerProductModal> asoToDealerProductModals;
    ArrayList<AsoToDealerProductModal> asoToDealerProductModalArrayList;

    public InsertProductToDealerRecyclerViewAdapter(Context mContext, List<AsoToDealerProductModal> asoToDealerProductModals) {
        this.mContext = mContext;
        this.asoToDealerProductModals = asoToDealerProductModals;
        this.asoToDealerProductModalArrayList = new ArrayList<>();
        this.asoToDealerProductModalArrayList.addAll(asoToDealerProductModals);
        //Log.d(TAG, "InsertProductToDealerRecyclerViewAdapter: inside constructor");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       // Log.d(TAG, "onCreateViewHolder: inside onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.initial_product_to_dealer_list,parent,false);
        return new InsertProductToDealerRecyclerViewAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
       // Log.d(TAG, "onBindViewHolder: inside BindView holder");
        holder.setCheckbox(position);
        holder.productName.setText(asoToDealerProductModals.get(position).getProduct_name());
        holder.cartonEditText.setText(String.valueOf(asoToDealerProductModals.get(position).getCarton()));
        holder.piceEditText.setText(String.valueOf(asoToDealerProductModals.get(position).getPice()));
        holder.totalAmaount.setText(asoToDealerProductModals.get(position).getTotalPice());

    }

    @Override
    public int getItemCount() {
       // Log.d(TAG, "getItemCount: inside getcount");
        return asoToDealerProductModals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView productName,totalAmaount;
        CheckBox checkProduct;
        EditText cartonEditText,piceEditText;

        public ViewHolder(View itemView){
            super(itemView);

            productName = itemView.findViewById(R.id.initial_product_name);
            totalAmaount = itemView.findViewById(R.id.total_number_of_product);
            checkProduct = itemView.findViewById(R.id.checkbox_confirm);
            cartonEditText = itemView.findViewById(R.id.carton_amount);
            piceEditText = itemView.findViewById(R.id.pice_amount);


            checkProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkProduct.isChecked()){
                        asoToDealerProductModals.get(getAdapterPosition()).setChecked(true);
                    }else {
                        asoToDealerProductModals.get(getAdapterPosition()).setChecked(false);
                    }
                }
            });

            cartonEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!cartonEditText.getText().toString().equals("")){
                        asoToDealerProductModals.get(getAdapterPosition()).setCarton(Integer.parseInt(cartonEditText.getText().toString()));
                        totalAmaount.setText(asoToDealerProductModals.get(getAdapterPosition()).getTotalPice());
                    }else{
                        asoToDealerProductModals.get(getAdapterPosition()).setCarton(0);
                        totalAmaount.setText(asoToDealerProductModals.get(getAdapterPosition()).getTotalPice());
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            piceEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!piceEditText.getText().toString().equals("")){
                        asoToDealerProductModals.get(getAdapterPosition()).setPice(Integer.parseInt(piceEditText.getText().toString()));
                        totalAmaount.setText(asoToDealerProductModals.get(getAdapterPosition()).getTotalPice());
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }

        void setCheckbox(int position){
            if(asoToDealerProductModals.get(position).isChecked()){
                checkProduct.setChecked(true);
            }else{
                checkProduct.setChecked(false);
            }

        }


    }


    public void searchFilter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        asoToDealerProductModals.clear();
        if(charText.length()== 0){
            asoToDealerProductModals.addAll(asoToDealerProductModalArrayList);
        }else{
            for (AsoToDealerProductModal modal: asoToDealerProductModalArrayList) {
                if(modal.getProduct_name().toLowerCase(Locale.getDefault()).contains(charText)){
                    asoToDealerProductModals.add(modal);
                }
            }
        }

        notifyDataSetChanged();

    }

}
