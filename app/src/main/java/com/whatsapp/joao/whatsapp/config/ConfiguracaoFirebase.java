package com.whatsapp.joao.whatsapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class ConfiguracaoFirebase {

    private static DatabaseReference reference;
    private static FirebaseAuth autenticacao;

    public static DatabaseReference getFirebase() {

        if(reference == null)
         reference = FirebaseDatabase.getInstance().getReference();

        return reference;
    }

    public static FirebaseAuth getFirebaseAuth() {
        if(autenticacao == null)
            autenticacao = FirebaseAuth.getInstance();

        return  autenticacao;
    }
}
