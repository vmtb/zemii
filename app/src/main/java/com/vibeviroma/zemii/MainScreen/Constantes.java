package com.vibeviroma.zemii.MainScreen;

import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class Constantes {

    public static String user_information_file="USER_INFORMATION.vv";

    public static void afficherMessage(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
    public static void afficherMessage(View view, String message, boolean duration_long){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static void ecrire(Context ctx, String fichier, String texte){
        try {
            FileOutputStream fil= ctx.openFileOutput(fichier, MODE_PRIVATE);
            fil.write(texte.getBytes());
            fil.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String lire_le_fichier(Context ctx, String fichier){
        String texto="";
        try {
            FileInputStream fil_i=ctx.openFileInput(fichier);  // new FileInputStream(fichier);
            byte[] buffer= new byte[fil_i.available()];
            fil_i.read(buffer);
            texto= new String(buffer);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return texto;
    }


}
