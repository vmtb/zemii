package com.vibeviroma.zemii;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vibeviroma.zemii.MainScreen.BottomMainActivity;
import com.vibeviroma.zemii.MainScreen.Constantes;
import com.vibeviroma.zemii.MainScreen.MainActivity;
import com.vibeviroma.zemii.MainScreen.Models.User;

public class LoginSignInActivity extends AppCompatActivity {

    private CardView btn_connect;
    private TextView createAcccout, zemi;
    private FirebaseAuth mAuth;
    private DatabaseReference user_ref;
    private String online_user_id="";
    private ProgressDialog loading;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_in);
        root=(RelativeLayout)findViewById(R.id.root);
        loading= new ProgressDialog(this);
        loading.setCanceledOnTouchOutside(false);

        mAuth= FirebaseAuth.getInstance();
        user_ref=FirebaseDatabase.getInstance().getReference().child("Users");

        btn_connect=(CardView)findViewById(R.id.btn_connect);
        createAcccout= (TextView)findViewById(R.id.createAccount);
        zemi=(TextView)findViewById(R.id.zemi);

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showDialogForLogin();
            }
        });

        createAcccout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForLogin();
            }
        });

        zemi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToZemiFbPage();
            }
        });
    }

    private void showDialogForLogin(){
        BottomStatusSheet bottomStatusSheet= new BottomStatusSheet(root, this);
        bottomStatusSheet.setCancelable(true);
        bottomStatusSheet.show(getSupportFragmentManager(), "");
        bottomStatusSheet.setmListener(new BottomStatusSheet.BottomSheetListener() {
            @Override
            public void onInscriptionFinished(final String contact) {
                if(mAuth.getCurrentUser()!=null){
                    online_user_id=mAuth.getCurrentUser().getUid();
                    user_ref.child(online_user_id).child("contact").setValue(contact);
                    user_ref.child(online_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild("user_name")){
                                Intent goToLoginPage=new Intent(LoginSignInActivity.this, User_information.class);
                                goToLoginPage.putExtra("contact", contact);
                                goToLoginPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                overridePendingTransition(0,0);
                                startActivity(goToLoginPage);
                                finish();
                            }else{
                                User user= dataSnapshot.getValue(User.class);
                                String user_info= user.toString();
                                Constantes.ecrire(LoginSignInActivity.this, Constantes.user_information_file, user_info);
                                Intent goToLoginPage=new Intent(LoginSignInActivity.this, BottomMainActivity.class);
                                overridePendingTransition(0,0);
                                startActivity(goToLoginPage);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void showOrDimissProgressDialog(boolean showProgressDialog, String message) {
                if(showProgressDialog){
                    loading.show();
                    loading.setMessage(message);
                }else{
                    loading.dismiss();
                }
            }
        });
    }

    private void goToZemiFbPage(){
        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/zemii"));
        startActivity(intent);
    }

}
