package com.vibeviroma.zemii.MainScreen.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vibeviroma.zemii.MainScreen.Constantes;
import com.vibeviroma.zemii.R;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;


public class BottomSearchSheet extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private MaterialEditText start, end;
    private Button verif;
    private String start_adress="", end_adress="";
    private String start_coordinate="", end_coordinate="";
    private Context context;
    private FirebaseAuth mAuth;
    private View parent;
    private Activity activity;

    public BottomSearchSheet(View parent, Activity activity) {
        this.parent = parent;
        this.activity=activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v=inflater.inflate(R.layout.search,container,false);
        context= getContext();

        mAuth=FirebaseAuth.getInstance();
        mAuth.setLanguageCode("fr");

        start = (MaterialEditText) v.findViewById(R.id.start);
        end = (MaterialEditText) v.findViewById(R.id.end);
        verif=(Button)v.findViewById(R.id.go);
        start.setFocusable(false);
        end.setFocusable(false);
        Places.initialize(context, getResources().getString(R.string.google_api_key));


        verif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               verifiationClicked();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPlace(100);
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPlace(200);
            }
        });


        return v;
    }

    private void verifiationClicked(){
        start_adress=start.getText().toString().trim();
        end_adress=end.getText().toString().trim();
        if(start_adress.isEmpty() || end_adress.isEmpty())
            Constantes.afficherMessage(parent, "Veuillez remplir tous les champs");
        else{
            mListener.onSearchLaunched(start_adress, start_coordinate, end_adress, end_coordinate);
            this.dismiss();
        }
    }

    private void searchPlace(int request_code){
        List<Place.Field> fieldList= Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
        Intent intent= new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fieldList).build(context);
        try {
            startActivityForResult(intent, request_code);
        }catch(IllegalStateException i){

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode== 100 || requestCode==200)&& resultCode==RESULT_OK){
            Place place= Autocomplete.getPlaceFromIntent(data);
            if(requestCode==100){
                start_adress= String.format("%s", place.getName());
                start_coordinate= String.valueOf(place.getLatLng());
                start.setText(start_adress);
            }else{
                end_adress= String.format("%s", place.getName());
                end_coordinate= String.valueOf(place.getLatLng());
                end.setText(end_adress);
            }
        }else{
            Constantes.afficherMessage(parent, "Impossible de trouver cette adresse !");
        }
    }

    public interface BottomSheetListener {
        void onSearchLaunched(String start_name, String start_coordinate, String end_name, String end_coordinate);
    }

    public void setmListener(BottomSheetListener mListener) {
        this.mListener = mListener;
    }



}
