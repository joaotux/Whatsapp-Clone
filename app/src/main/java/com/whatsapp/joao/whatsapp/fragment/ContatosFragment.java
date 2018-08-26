package com.whatsapp.joao.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.whatsapp.joao.whatsapp.R;
import com.whatsapp.joao.whatsapp.activity.ConversasActivity;
import com.whatsapp.joao.whatsapp.adapter.ContatosAdapter;
import com.whatsapp.joao.whatsapp.config.ConfiguracaoFirebase;
import com.whatsapp.joao.whatsapp.helper.Preferencias;
import com.whatsapp.joao.whatsapp.model.Contato;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ListView listView;
    private ContatosAdapter adapter;
    private ArrayList<Contato> contatos;
    private DatabaseReference firebaseDB;
    private ValueEventListener valueEventListener;

    public ContatosFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        //attribue o evento listener quando o fragmento é iniciado
        firebaseDB.addValueEventListener(valueEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        //remove o evento listener quando o fragmento é stopado
        firebaseDB.removeEventListener(valueEventListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        listView = view.findViewById(R.id.lv_contatos);

        contatos = new ArrayList<Contato>();

        adapter = new ContatosAdapter(
                getActivity(),
                contatos
        );

        listView.setAdapter(adapter);

        //pega usuario logado.
        Preferencias preferencias = new Preferencias(getActivity());
        String identificadorUsuarioLogado = preferencias.getIdentificadorUsuario();

        //recupera contatos do firebase.
        firebaseDB = ConfiguracaoFirebase.getFirebase().child("contatos").child(identificadorUsuarioLogado);

        //cria um evento listener para ficar escutando o nó contatos.
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                contatos.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contato contato = snapshot.getValue(Contato.class);

                    contatos.add(contato);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        //adiciona evento de clique na lista para abrir conversa com contato
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ConversasActivity.class);

                Contato contato = contatos.get(i);

                intent.putExtra("nome", contato.getNome());
                intent.putExtra("email", contato.getEmail());

                startActivity(intent);
            }
        });

        return view;
    }

}
