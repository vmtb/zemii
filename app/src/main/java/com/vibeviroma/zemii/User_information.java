package com.vibeviroma.zemii;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vibeviroma.zemii.MainScreen.BottomMainActivity;
import com.vibeviroma.zemii.MainScreen.Constantes;
import com.vibeviroma.zemii.MainScreen.Models.User;

public class User_information extends AppCompatActivity {

    private MaterialEditText nom, prenom;
    private EditText contact;
    private Spinner spinner_sexe;
    private String [] the_sexes={"M", "F"};
    private String selected_sexe="", online_user_id="";
    private Button validation;
    private DatabaseReference user_ref;
    private RelativeLayout root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        online_user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();
        user_ref = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);

        nom=(MaterialEditText)findViewById(R.id.Nom);
        prenom=(MaterialEditText)findViewById(R.id.Prenom);
        contact=(EditText)findViewById(R.id.Phone);
        spinner_sexe=(Spinner)findViewById(R.id.sexe);
        validation=(Button)findViewById(R.id.valider);
        root=(RelativeLayout)findViewById(R.id.root);

        setContact();
        load_spinner();

        validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });


    }

    private void validation() {
        String user_name=nom.getText().toString().trim();
        String user_surname=prenom.getText().toString().trim();
        String user_phone=contact.getText().toString().trim();

        if(user_name.isEmpty()||user_surname.isEmpty()){
            Constantes.afficherMessage(root, "Vous devez remplir les champs NOM ET PRENOM");
        }else{
            ProgressDialog pdial= new ProgressDialog(this);
            pdial.setCanceledOnTouchOutside(false);
            pdial.setMessage("Sauvegarde en cours...");
            sauvegarder(user_name, user_surname, selected_sexe, user_phone, pdial);
        }

    }

    private void sauvegarder(String user_name, String user_surname, String selected_sexe, String user_phone, final ProgressDialog pdial) {
        pdial.show();
        final User user= new User(user_name, user_surname, user_phone, selected_sexe,selected_sexe.equals("M")?"Default 1":"Default 2", selected_sexe.equals("M")?"Default 1":"Default 2" );
        user_ref.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pdial.dismiss();
                Constantes.ecrire(User_information.this, Constantes.user_information_file, user.toString());
                Toast.makeText(User_information.this, "Inscription terminée avec succès !", Toast.LENGTH_SHORT).show();
                Intent goToMainPage=new Intent(User_information.this, BottomMainActivity.class);
                goToMainPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                overridePendingTransition(0,0);
                goToMainPage.putExtra("new_user", online_user_id);
                startActivity(goToMainPage);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pdial.dismiss();
                Constantes.afficherMessage(root, "Une erreur s'est produite: "+e.getMessage());
            }
        });
    }

    private void setContact(){
        if(getIntent()!=null && getIntent().getExtras().getString("contact")!=null){
            contact.setText(getIntent().getExtras().getString("contact"));
        }else {
            user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    contact.setText(user.getContact());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void load_spinner(){
        ArrayAdapter array= new ArrayAdapter(this, android.R.layout.simple_spinner_item, the_sexes);
        array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_sexe.setAdapter(array);
        spinner_sexe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_sexe=the_sexes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


}
