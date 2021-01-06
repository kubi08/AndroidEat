package com.example.lenovo.androideat;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.androideat.Common.Common;
import com.example.lenovo.androideat.Database.Database;
import com.example.lenovo.androideat.Model.Order;
import com.example.lenovo.androideat.Model.Request;
import com.example.lenovo.androideat.ViewHolder.CartAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    FButton btnPlace;

    List<Order> cart =new ArrayList<>();
    CartAdapter adapter;


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

        setContentView(R.layout.activity_cart);

        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        recyclerView=(RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice=(TextView)findViewById(R.id.total);
        btnPlace=(FButton)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart.size()>0)
                
        showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Sipariş yok", Toast.LENGTH_SHORT).show();
              

            }
        });
        
        loadListFood();


    }

    private void showAlertDialog() {
        AlertDialog.Builder  alertDialog=new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Bir sonraki adım");
        alertDialog.setMessage("Adresinizi giriniz:");

        LayoutInflater inflater=this.getLayoutInflater();
        View order_address_comment=inflater.inflate(R.layout.order_address_comment,null);

        final MaterialEditText edtAddress=(MaterialEditText)order_address_comment.findViewById(R.id.edtAddress);
        final MaterialEditText edtComment=(MaterialEditText)order_address_comment.findViewById(R.id.edtComment);

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request =new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        "0",
                        edtComment.getText().toString(),
                        cart

                );
                String order_number=String.valueOf(System.currentTimeMillis());
                requests.child(order_number)
                        .setValue(request);

                new Database(getBaseContext()).cleanCart();

                sendNotificationOrder(order_number);


               // Toast.makeText(Cart.this,"Siparişiniz için teşekkürler",Toast.LENGTH_SHORT).show();
               // finish();
            }
        });

        alertDialog.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private void sendNotificationOrder(String order_number) {

        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query data=tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private void loadListFood() {
        cart=new Database(this).getCarts();
        adapter=new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        int total=0;
        for (Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale=new Locale("en","US");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int order) {
        cart.remove(order);

        new Database(this).cleanCart();

        for(Order item:cart)
            new Database(this).addToCart(item);
         loadListFood();
    }
}
