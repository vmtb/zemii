package com.vibeviroma.zemii.MainScreen;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vibeviroma.zemii.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class BottomMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_chat, R.id.navigation_history, R.id.navigation_notifications, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        showWelcomeMessage();
    }

    private void showWelcomeMessage(){
        if(getIntent().getExtras()!=null && getIntent().getExtras().get("new_user")!=null) {
            View view = LayoutInflater.from(this).inflate(R.layout.greating_message_layout, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .create();

            Button next = (Button) view.findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }


    }

}
