package com.whatsapp.joao.whatsapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whatsapp.joao.whatsapp.R;
import com.whatsapp.joao.whatsapp.adapter.TabAdapter;
import com.whatsapp.joao.whatsapp.config.ConfiguracaoFirebase;
import com.whatsapp.joao.whatsapp.helper.Base64Custom;
import com.whatsapp.joao.whatsapp.helper.Preferencias;
import com.whatsapp.joao.whatsapp.helper.SlidingTabLayout;
import com.whatsapp.joao.whatsapp.model.Contato;
import com.whatsapp.joao.whatsapp.model.Usuario;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Toolbar toolbar;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    private String identificadorUsuario;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        slidingTabLayout = findViewById(R.id.st_tabs);
        viewPager = findViewById(R.id.vp_tabs);

        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));

        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sair :

                deslogarUsuario();

                return true;
            case R.id.menu_cadastro_usuario :

                abrirCadastroContato();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void abrirCadastroContato() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Novo Usuário");
        builder.setMessage("E-mail do usuário");
        builder.setCancelable(false);

        final EditText edContato = new EditText(MainActivity.this);
        builder.setView(edContato);

        //Adiciona contato
        builder.setPositiveButton("CADASTRAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                identificadorUsuario = Base64Custom.codificarBase64(edContato.getText().toString());

                databaseReference = ConfiguracaoFirebase.getFirebase().child("usuarios").child(identificadorUsuario);

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {

                            Preferencias preferencias = new Preferencias(MainActivity.this);
                            String usuarioLogado = preferencias.getIdentificadorUsuario();

                            databaseReference = ConfiguracaoFirebase.getFirebase();
                            databaseReference = databaseReference.child("contatos").child(usuarioLogado).child(identificadorUsuario);

                            Usuario usuarioContato = dataSnapshot.getValue(Usuario.class);

                            Contato contato = new Contato();

                            contato.setIdentificadorUsuario(identificadorUsuario);
                            contato.setEmail(usuarioContato.getEmail());
                            contato.setNome(usuarioContato.getNome());

                            //salva contato no banco de dados
                            databaseReference.setValue(contato);

                        } else {
                            Toast.makeText(MainActivity.this, "Usuário não existe!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create();
        builder.show();
    }

    public void deslogarUsuario() {
        firebaseAuth.signOut();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
