package com.example.lenovo.androideat;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.lenovo.androideat.Common.Common;
import com.example.lenovo.androideat.Database.Database;
import com.example.lenovo.androideat.Model.Food;
import com.example.lenovo.androideat.Model.Order;
import com.example.lenovo.androideat.Model.Rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;


import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodDetail extends AppCompatActivity implements

        RatingDialogListener {

    TextView foodd_name,foodd_price,foodd_description;
    ImageView foodd_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton bttnCart,btnRating;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;


    String foodId="";


    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTbl;
    Food currentFood;

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

        setContentView(R.layout.activity_food_detail);

        database=FirebaseDatabase.getInstance();
        foods=database.getReference("Food");
        ratingTbl=database.getReference("Rating");


        numberButton=(ElegantNumberButton)findViewById(R.id.number_button);
        bttnCart=(FloatingActionButton)findViewById(R.id.btncart);
        btnRating=(FloatingActionButton)findViewById(R.id.btn_rating);
         ratingBar=(RatingBar)findViewById(R.id.ratingBar);

         btnRating.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 showRatingDialog();

             }
         });

        bttnCart.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 new Database(getBaseContext()).addToCart(new Order(
                         foodId,
                         currentFood.getName(),
                         numberButton.getNumber(),
                         currentFood.getPrice(),
                         currentFood.getDiscount()





                 ));

                 Toast.makeText(FoodDetail.this,"Sepete eklendi", Toast.LENGTH_SHORT).show();

             }
         });//buraya bak btnncart olabilir.

        foodd_description=(TextView)findViewById(R.id.food_description);
        foodd_name=(TextView)findViewById(R.id.food_name);
        foodd_price=(TextView)findViewById(R.id.food_price);
        foodd_image=(ImageView)findViewById(R.id.img_food);

        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsetAppBar);


        if(getIntent() !=null)
            foodId=getIntent().getStringExtra("FoodId");

        if (!foodId.isEmpty()){
            if(Common.isConnectedToInterner(getBaseContext())) {
                getDetailFood(foodId);
                getRatingFood(foodId);
            }
            else {


                Toast.makeText(FoodDetail.this, "Internet yok", Toast.LENGTH_SHORT).show();
                return;
            }

        }






    }

    private void getRatingFood(String foodId) {
        Query foodRating=ratingTbl.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())

                {
                Rating item=postSnapshot.getValue(Rating.class);
                sum+=Integer.parseInt(item.getRateValue());
                count++;

                }

                if(count !=0)
                {

                    float average=sum/count;
                    ratingBar.setRating(average);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Onay")
                .setNegativeButtonText("İptal")
                .setNoteDescriptions(Arrays.asList("Çok kötü","Kötü değil","İdare eder","Güzel","Çok güzel"))
                .setDefaultRating(1)
                .setTitle("Puan ver")
                .setDescription("Lütfen oy kullanınız")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Buraya lütfen yorum yazınız")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();

    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentFood=dataSnapshot.getValue(Food.class);

                Picasso.with(getBaseContext()).load(currentFood.getImage())
                        .into(foodd_image);
              //  collapsingToolbarLayout.setCollapsedTitleTextAppearance(Integer.parseInt(food.getName()));

                 foodd_price.setText(currentFood.getPrice());

                 foodd_name.setText(currentFood.getName());

                 foodd_description.setText(currentFood.getDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onPositiveButtonClicked(int value,String commnets){
        final Rating rating=new Rating(Common.currentUser.getPhone(),
                foodId,
                String.valueOf(value),
                commnets);
        ratingTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.currentUser.getPhone()).exists())
                {
                 ratingTbl.child(Common.currentUser.getPhone()).removeValue();
                 ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);

                }
                else
                    {
                        ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);

                }

                Toast.makeText(FoodDetail.this, "Puanlamanız için teşekkürker", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    @Override
    public void onNegativeButtonClicked()
    {


    }
}
