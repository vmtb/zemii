package com.vibeviroma.zemii;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.vibeviroma.zemii.MainScreen.BottomMainActivity;
import com.vibeviroma.zemii.MainScreen.Constantes;
import com.vibeviroma.zemii.MainScreen.MainActivity;

import static java.lang.Thread.sleep;

public class Welcome extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mAuth=FirebaseAuth.getInstance();
        Thread thread= new Thread(){
            @Override
            public void run() {
                executeSleep(1500);
            }
        };
        thread.start();
    }

    private void executeSleep(int timeMillis){

        try {
            sleep(timeMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if (mAuth.getCurrentUser()==null){
                Intent goToLoginPage=new Intent(Welcome.this, LoginSignInActivity.class);
                goToLoginPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                overridePendingTransition(0,0);
                startActivity(goToLoginPage);
                finish();
            }else{
                String user_name= Constantes.lire_le_fichier(this, Constantes.user_information_file);
                if(user_name.isEmpty()){
                    Intent goToLoginPage=new Intent(Welcome.this, User_information.class);
                    goToLoginPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    overridePendingTransition(0,0);
                    startActivity(goToLoginPage);
                    finish();
                }else{
                    Intent goToMainPage=new Intent(Welcome.this, BottomMainActivity.class);
                    goToMainPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    overridePendingTransition(0,0);
                    startActivity(goToMainPage);
                    finish();
                }
            }
        }
    }


}
