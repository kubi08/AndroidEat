package com.example.lenovo.androideat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.lenovo.androideat.Common.Common;
import com.example.lenovo.androideat.Database.Database;
import com.example.lenovo.androideat.Interface.ItemClickListener;
import com.example.lenovo.androideat.Model.Food;
import com.example.lenovo.androideat.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String categoryId="";

    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    List<String> suggestList=new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    Database localDB;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/yazi.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_food_list);

        database=FirebaseDatabase.getInstance();
        foodList=database.getReference("Food");

        localDB=new Database(this);



        recyclerView=(RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        if(getIntent() !=null)
            categoryId=getIntent().getStringExtra("CategoryId");

        if (!categoryId.isEmpty() && categoryId !=null)
        {
             if(Common.isConnectedToInterner(getBaseContext()))
            loadListFood(categoryId);
             else {


                 Toast.makeText(FoodList.this, "Internet yok", Toast.LENGTH_SHORT).show();
                 return;
             }
        }

        materialSearchBar=(MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Yiyicek giriniz");

        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest=new ArrayList<>();
                for(String search:suggestList){

                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);


                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean b) {

                if(!b)
                    recyclerView.setAdapter(adapter);

            }

            @Override
            public void onSearchConfirmed(CharSequence charSequence) {

                startSearch(charSequence);

            }

            @Override
            public void onButtonClicked(int i) {

            }
        });






    }

    private void startSearch(CharSequence charSequence) {
        searchAdapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(

                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("name").equalTo(charSequence.toString())

        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.foodd_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())

                        .into(viewHolder.foodd_image);

                final Food local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void ocClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail=new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }


                });


            }
        };
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        foodList.orderByChild("menuID").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                            Food item=postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void loadListFood(String categoryId) {
        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {
                viewHolder.foodd_name.setText(model.getName());
                viewHolder.food_price.setText(String.format(model.getPrice().toString())+"TL");
                Picasso.with(getBaseContext()).load(model.getImage())

                        .into(viewHolder.foodd_image);

                if(localDB.isFavorite(adapter.getRef(position).getKey()))
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!localDB.isFavorite(adapter.getRef(position).getKey()))
                        {
                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, ""+model.getName()+" Favorilere Eklendi", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {

                            localDB.removeFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this, ""+model.getName()+" Favorilerden Çıkartıldı", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

                final Food local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void ocClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail=new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }


                });

            }
        };

        recyclerView.setAdapter(adapter);
    }
}
