package com.example.lenovo.androideat.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.lenovo.androideat.Interface.ItemClickListener;
import com.example.lenovo.androideat.R;

/**
 * Created by LENOVO on 11.01.2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

     public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress;

     private ItemClickListener itemClickListener;



    public OrderViewHolder(View itemView) {
        super(itemView);
     txtOrderAddress=(TextView)itemView.findViewById(R.id.order_address);
     txtOrderId=(TextView)itemView.findViewById(R.id.order_id);
     txtOrderStatus=(TextView)itemView.findViewById(R.id.order_status);
     txtOrderPhone=(TextView)itemView.findViewById(R.id.order_phone);

     itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener ıtemClickListener) {
        this.itemClickListener = ıtemClickListener;
    }

    @Override
    public void onClick(View view) {

   itemClickListener.ocClick(view,getAdapterPosition(),false);

    }
}
