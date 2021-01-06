package com.example.lenovo.androideat.Service;

import android.media.session.MediaSession;

import com.example.lenovo.androideat.Common.Common;
import com.example.lenovo.androideat.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by LENOVO on 03.02.2018.
 */

public class MyFirebaseService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed= FirebaseInstanceId.getInstance().getToken();
        updateTokenToFirebase(tokenRefreshed);
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token token= new Token(tokenRefreshed,false);
        tokens.child(Common.currentUser.getPhone()).setValue(token);

    }


}
