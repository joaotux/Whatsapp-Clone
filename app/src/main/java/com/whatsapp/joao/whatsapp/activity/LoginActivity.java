package com.whatsapp.joao.whatsapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.whatsapp.joao.whatsapp.Manifest;
import com.whatsapp.joao.whatsapp.R;
import com.whatsapp.joao.whatsapp.config.ConfiguracaoFirebase;
import com.whatsapp.joao.whatsapp.helper.Base64Custom;
import com.whatsapp.joao.whatsapp.helper.Permissoes;
import com.whatsapp.joao.whatsapp.helper.Preferencias;
import com.whatsapp.joao.whatsapp.model.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText senha;
    private Button btnLogar;

    private DatabaseReference firebaseDB;
    private FirebaseAuth autenticacao;

    private String nomeUsuarioLogado;
    private ValueEventListener valueEventListener;

    private Preferencias preferencias;
    private String identificadorUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuarioJaAutenticado();

        email = findViewById(R.id.editEmail);
        senha = findViewById(R.id.editSenha);
        btnLogar = findViewById(R.id.btnLogar);

        //pega nome do usuario logado
        firebaseDB = ConfiguracaoFirebase.getFirebase();

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUsuario(email.getText().toString(), senha.getText().toString());
            }
        });
    }

    public void usuarioJaAutenticado() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        if(autenticacao.getCurrentUser() != null) {
            abrirTelaPrincipal();
        }
    }

    public void loginUsuario(final String email, String senha) {
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

                    preferencias = new Preferencias(LoginActivity.this);
                    identificadorUsuarioLogado = Base64Custom.codificarBase64(email);

                    firebaseDB.child("usuarios").child(identificadorUsuarioLogado);

                    firebaseDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            /*
                             se o usuario existir, pega o nome dele e guarda junto
                             com o identificador do mesmo
                             */
                            if(dataSnapshot.exists()) {
                                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                                nomeUsuarioLogado = usuario.getNome();

                                preferencias.salvarDados(identificadorUsuarioLogado, nomeUsuarioLogado);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    abrirTelaPrincipal();

                } else {
                    Toast.makeText(getApplicationContext(), "Erro ao tentar realizar o login!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void abrirTelaPrincipal() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void abrirCadastroUsuario(View view) {

     Intent intent = new Intent(getApplicationContext(), CadastroUsuarioActivity.class);
     startActivity(intent);

    }
}
