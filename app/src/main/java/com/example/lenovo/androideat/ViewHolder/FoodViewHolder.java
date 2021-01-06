package com.example.lenovo.androideat.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.androideat.Interface.ItemClickListener;
import com.example.lenovo.androideat.R;

/**
 * Created by LENOVO on 04.01.2018.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{


    public TextView foodd_name,food_price;
    public ImageView foodd_image,fav_image;


    private ItemClickListener ıtemClickListener;




    public FoodViewHolder(View itemView) {
        super(itemView);
        foodd_name=(TextView)itemView.findViewById(R.id.food_name);
        foodd_image=(ImageView)itemView.findViewById(R.id.food_image);
        fav_image=(ImageView)itemView.findViewById(R.id.faverites);
        food_price=(TextView)itemView.findViewById(R.id.food_price);

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
