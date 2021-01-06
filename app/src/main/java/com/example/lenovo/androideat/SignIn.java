package com.example.lenovo.androideat;

import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lenovo.androideat.Common.Common;
import com.example.lenovo.androideat.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignIn extends AppCompatActivity {

    EditText edtPhone,edtPassword;
    Button  btnSignIn;

    CheckBox checkBox;
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
        setContentView(R.layout.activity_sign_in);

        edtPassword=(MaterialEditText)findViewById(R.id.edtPssword);
        edtPhone=(MaterialEditText)findViewById(R.id.edtPhone);
        btnSignIn=(Button)findViewById(R.id.btnSignIn);
        checkBox=(CheckBox)findViewById(R.id.ckbRemember);

        Paper.init(this);

        //init firebase
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference( "User");
        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInterner(getBaseContext())) {

                    if(checkBox.isChecked())
                    {
                   Paper.book().write(Common.USER_KEY,edtPhone.getText().toString());
                   Paper.book().write(Common.PWD_KEY,edtPassword.getText().toString());


                    }



                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Lütfen Bekleyiniz");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {


                                //get user information
                                mDialog.dismiss();
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);

                                user.setPhone(edtPhone.getText().toString());
                                if (user.getPassword().equals(edtPassword.getText().toString())) {

                                    Intent homeIntent = new Intent(SignIn.this, Home.class);
                                    Common.currentUser = user;
                                    startActivity(homeIntent);
                                    finish();

                                } else {

                                    Toast.makeText(SignIn.this, "Şifre yanlış", Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "Kayıtlı kullanıcı değil", Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {

                    Toast.makeText(SignIn.this, "Internet yok", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        });
    }
}
