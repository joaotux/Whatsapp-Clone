package com.whatsapp.joao.whatsapp.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.whatsapp.joao.whatsapp.R;
import com.whatsapp.joao.whatsapp.adapter.MensagensAdapter;
import com.whatsapp.joao.whatsapp.config.ConfiguracaoFirebase;
import com.whatsapp.joao.whatsapp.helper.Base64Custom;
import com.whatsapp.joao.whatsapp.helper.Preferencias;
import com.whatsapp.joao.whatsapp.model.Conversa;
import com.whatsapp.joao.whatsapp.model.Mensagem;

import java.util.ArrayList;

public class ConversasActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editMensagem;
    private ImageButton btnEnvia;
    private DatabaseReference firebaseDB;
    private ListView listView;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private ValueEventListener valueEventListener;

    //dados destino
    private String nomeDestino;
    private String emailDestino;

    //dados remetente
    private String emailRemetente;
    private String nomeDestinatario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversas);

        toolbar = findViewById(R.id.tb_toolbar);
        editMensagem = findViewById(R.id.editMensagem);
        btnEnvia = findViewById(R.id.btnEnvia);
        listView = findViewById(R.id.lv_conversas);

        //recupera o email e nome do remetente
        Preferencias preferencias = new Preferencias(ConversasActivity.this);
        emailRemetente = preferencias.getIdentificadorUsuario();
        nomeDestinatario = preferencias.getNomeUsuario();

        //recupera dados passados entre activitys
        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            nomeDestino = bundle.getString("nome");
            String email = bundle.getString("email");
            emailDestino = Base64Custom.codificarBase64(email);
        }

        toolbar.setTitle(nomeDestino);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        //envia mensagem
        btnEnvia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textoMensagem = editMensagem.getText().toString();

                if(textoMensagem.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Digite uma mensagem para envia", Toast.LENGTH_SHORT).show();
                } else {

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdentificadorDestino(emailDestino);
                    mensagem.setMensagem(textoMensagem);

                    //salva mensagem de envio remetente
                    salvaMensagem(emailRemetente, emailDestino, mensagem);

                    //salva mensagem de envio destinatario
                    salvaMensagem(emailDestino, emailRemetente, mensagem);

                    //cria mensagem
                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario(emailDestino);
                    conversa.setNome(nomeDestino);
                    conversa.setMensagem(textoMensagem);

                    //salvar conversa remetente
                    salvaConversa(emailRemetente, emailDestino, conversa);

                    //modifica conversa para destino
                    conversa.setIdUsuario(emailRemetente);
                    conversa.setNome(nomeDestinatario);

                    //salvar conversa remetente
                    salvaConversa(emailDestino, emailRemetente, conversa);

                }

            }
        });


        //cria listagem de mensagens
        mensagens = new ArrayList<Mensagem>();
        adapter = new MensagensAdapter(ConversasActivity.this, mensagens);

        listView.setAdapter(adapter);

        firebaseDB = ConfiguracaoFirebase.getFirebase().child("mensagens").child(emailRemetente).child(emailDestino);

        valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mensagens.clear();

                if(dataSnapshot.exists()) {

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Mensagem mensagem = snapshot.getValue(Mensagem.class);

                        mensagens.add(mensagem);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private boolean salvaConversa(String idRemetente, String idDestinatario, Conversa conversa) {

        try{
            firebaseDB = ConfiguracaoFirebase.getFirebase().child("conversas");
            firebaseDB.child(idRemetente)
                    .child(idDestinatario)
                    .setValue(conversa);

            return true;
        } catch (Exception e) {
            e.getStackTrace();
            return false;
        }

    }

    private boolean salvaMensagem(String emailRemetente, String emailDestino, Mensagem mensagem) {

        try {

            firebaseDB = ConfiguracaoFirebase.getFirebase().child("mensagens");
            firebaseDB.child(emailRemetente).child(emailDestino).push().setValue(mensagem);
            editMensagem.setText("");

            return true;
        } catch (Exception e) {
            e.getStackTrace();
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseDB.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        firebaseDB.removeEventListener(valueEventListener);
    }
}
