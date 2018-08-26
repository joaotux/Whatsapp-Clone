package com.whatsapp.joao.whatsapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.whatsapp.joao.whatsapp.R;
import com.whatsapp.joao.whatsapp.config.ConfiguracaoFirebase;
import com.whatsapp.joao.whatsapp.helper.Base64Custom;
import com.whatsapp.joao.whatsapp.helper.Preferencias;
import com.whatsapp.joao.whatsapp.model.Usuario;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText editNome;
    private EditText editEmail;
    private EditText editSenha;
    private Button btnCadastrar;

    private DatabaseReference reference;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        editNome = findViewById(R.id.editNome);
        editSenha = findViewById(R.id.editSenha);
        editEmail = findViewById(R.id.editEmail);
        btnCadastrar = findViewById(R.id.btnCadastro);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Usuario usuario = new Usuario(editNome.getText().toString(), editEmail.getText().toString(), editSenha.getText().toString());
                cadastrarUsuario(usuario);
            }
        });
    }

    public void cadastrarUsuario(final Usuario usuario) {
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        try {

            autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                    .addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {

                        FirebaseUser referenceUsuario = task.getResult().getUser();

                        String indentificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                        usuario.setId(indentificadorUsuario);

                        usuario.salvar();

                        Toast.makeText(getApplicationContext(), "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

                        Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);
                        preferencias.salvarDados(indentificadorUsuario, usuario.getNome());

                        logarUsuario();

                    } else {
                        String mensagem = "";

                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            mensagem = "Senha muito fraca!";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            mensagem = "Este e-mail é inválido!";
                        } catch (FirebaseAuthUserCollisionException e) {
                            mensagem = "Este e-mail já esta em uso!";
                        } catch (Exception e) {
                            e.printStackTrace();
                            mensagem = "Erro ao tentar cadastrar usuário!";
                        }

                        Toast.makeText(getApplicationContext(), "Erro: " + mensagem, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void logarUsuario() {
        Intent intent = new Intent(CadastroUsuarioActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
