package com.vibeviroma.zemii;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vibeviroma.zemii.MainScreen.Constantes;
import com.vibeviroma.zemii.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class BottomStatusSheet extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private TextView compteur;
    private MaterialEditText phone__, conf;
    private Button verif;
    private String contact="";
    private Context context;
    private Handler handler=new Handler();
    private Runnable rn;
    private int total=90;
    private boolean codeIsSent=false;
    private FirebaseAuth mAuth;
    private View parent;
    private Activity activity;

    public BottomStatusSheet(View parent, Activity activity) {
        this.parent = parent;
        this.activity=activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v=inflater.inflate(R.layout.phone,container,false);
        context= getContext();

        mAuth=FirebaseAuth.getInstance();
        mAuth.setLanguageCode("fr");

        compteur = (TextView) v.findViewById(R.id.compt);
        phone__ = (MaterialEditText) v.findViewById(R.id.mp_connecter);
        conf = (MaterialEditText) v.findViewById(R.id.code);
        verif =(Button)v.findViewById(R.id.btn_time);

        conf.setVisibility(View.GONE);
        compteur.setVisibility(View.GONE);

        verif.setText("Envoyer le code");
        verif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               verifiationClicked();
            }
        });


        return v;
    }

    private void verifiationClicked(){
        contact= phone__.getText().toString().trim();
        if(contact.isEmpty())
            Constantes.afficherMessage(parent, "Contact vide..");
        else if (contact.length()<8)
            Constantes.afficherMessage(parent, "Le numéro de téléphone doit contenir 8 chiffres..");
        else{
            contact="+229"+contact;
            verifier_numero();
        }
    }

    public interface BottomSheetListener {
        void onInscriptionFinished(String contact);
        void showOrDimissProgressDialog(boolean showProgressDialog, String message);
    }

    public void setmListener(BottomSheetListener mListener) {
        this.mListener = mListener;
    }

    private void verifier_numero(){
        mListener.showOrDimissProgressDialog(true, "Envoi de code de vérification...");
        PhoneAuthProvider.OnVerificationStateChangedCallbacks changedCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                mListener.showOrDimissProgressDialog(false, "");
                codeIsSent=false;
                mListener.onInscriptionFinished(contact);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mListener.showOrDimissProgressDialog(false, "");
                codeIsSent=false;
                AlertDialog alertDialog= new AlertDialog.Builder(context.getApplicationContext())
                        .setMessage("Echec de la vérification du numéro de téléphone...! Vérifiez votre connexion internet")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                alertDialog.show();
            }

            @Override
            public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.d("Resuslt", s);
                compteur.setVisibility(View.VISIBLE);
                mListener.showOrDimissProgressDialog(false, "");
                conf.setVisibility(View.VISIBLE);
                phone__.setEnabled(false);
                verif.setText("Vérifier");

                verif.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String code= conf.getText().toString().trim();
                        if( code.isEmpty() || code.length()<6) {
                            Constantes.afficherMessage(parent, "Code à 8 chiffres");
                            return;
                        }
                        codeIsSent=false;
                        mListener.showOrDimissProgressDialog(true,"Vérification...");
                        handler.removeCallbacks(rn);
                        PhoneAuthCredential p= PhoneAuthProvider.getCredential(s, code);
                        mAuth.signInWithCredential(p)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        mListener.showOrDimissProgressDialog(false, "");
                                        if(task.isSuccessful()){
                                            mListener.onInscriptionFinished(contact);
                                        }else{
                                            Constantes.afficherMessage(parent, "Une erreur s'est produite... Les codes ne correspondent pas ou le code est expiré !");
                                        }
                                    }
                                });
                    }
                });
                Constantes.afficherMessage(getView(), "Un code de vérification a été envoyé sur ce numéro ! Veuillez bien saisir le code !", true);
            }
        };


        PhoneAuthProvider.getInstance().verifyPhoneNumber (
                contact,
                90,
                TimeUnit.SECONDS,
                activity,
                changedCallbacks
        );
        codeIsSent=true;
        total=90;
        compter(compteur);
        handler.post(rn);
    }



    private void compter(final TextView view){
        total=90;
        rn= new Runnable() {
            @Override
            public void run() {
                if(codeIsSent) {
                    view.setText("Expire dans "+(total--)+" s");
                    handler.postDelayed(rn, 1000);
                    if(total==0) {
                        handler.removeCallbacks(rn);
                        Constantes.afficherMessage(getView(), "Le code est expiré !");
                    }
                } else {
                    handler.removeCallbacks(rn);
                }
            }
        };
    }




}
