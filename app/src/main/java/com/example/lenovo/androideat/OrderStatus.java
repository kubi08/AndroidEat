package com.example.lenovo.androideat;

import android.app.DownloadManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.lenovo.androideat.Common.Common;
import com.example.lenovo.androideat.Model.Request;
import com.example.lenovo.androideat.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.lenovo.androideat.Common.Common.convertCodeToStatus;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference request;
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
        setContentView(R.layout.activity_order_status);

        database=FirebaseDatabase.getInstance();
        request=database.getReference("Requests");

        recyclerView=(RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if(getIntent()==null)
        loadOrders(Common.currentUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("userPhone"));

    }

    private void loadOrders(String phone) {
      adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
              Request.class,
              R.layout.order_layout,
              OrderViewHolder.class,
              request.orderByChild("phone")
                .equalTo(phone)

      ) {
          @Override
          protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
              viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
              viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
              viewHolder.txtOrderPhone.setText(model.getPhone());
              viewHolder.txtOrderAddress.setText(model.getAddress());


          }
      };

      recyclerView.setAdapter(adapter);
    }



}
