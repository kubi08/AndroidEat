package com.example.lenovo.androideat.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.androideat.Interface.ItemClickListener;
import com.example.lenovo.androideat.R;

/**
 * Created by LENOVO on 03.01.2018.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName;
    public ImageView imageView;


    private ItemClickListener ıtemClickListener;


    public MenuViewHolder(View itemView) {
        super(itemView);

        txtMenuName=(TextView)itemView.findViewById(R.id.menu_name);
        imageView=(ImageView)itemView.findViewById(R.id.menu_image);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener ıtemClickListener) {
        this.ıtemClickListener = ıtemClickListener;
    }

    @Override
    public void onClick(View v) {
        ıtemClickListener.ocClick(v,getAdapterPosition(),false);

    }
}
