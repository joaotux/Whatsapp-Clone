package com.whatsapp.joao.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.whatsapp.joao.whatsapp.R;
import com.whatsapp.joao.whatsapp.activity.ConversasActivity;
import com.whatsapp.joao.whatsapp.adapter.ConversasAdapter;
import com.whatsapp.joao.whatsapp.config.ConfiguracaoFirebase;
import com.whatsapp.joao.whatsapp.helper.Base64Custom;
import com.whatsapp.joao.whatsapp.helper.Preferencias;
import com.whatsapp.joao.whatsapp.model.Contato;
import com.whatsapp.joao.whatsapp.model.Conversa;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private ListView listView;
    private ConversasAdapter adapter;
    private ArrayList<Conversa> conversas;
    private DatabaseReference firebaseDB;

    public ConversasFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        listView = view.findViewById(R.id.lv_conversas);
        conversas = new ArrayList<>();

        adapter = new ConversasAdapter(
                getActivity(),
                conversas
        );

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ConversasActivity.class);

                //recupera dados do destino
                Conversa conversa = conversas.get(i);

                //decodifica email do destino
                String email = Base64Custom.decodificarBase64(conversa.getIdUsuario());
                Log.i("DADOSUSU", conversa.getIdUsuario());
                Log.i("DADOSUSU", email);

                String nome = conversa.getNome();

                intent.putExtra("nome", nome);
                intent.putExtra("email", email);

                startActivity(intent);
            }
        });

        //pega identificação do usuário logado
        Preferencias preferencias = new Preferencias(getActivity());
        String idUsuarioLogado = preferencias.getIdentificadorUsuario();

        //faz referencia ao nó de conversas do usuário logado
        firebaseDB = ConfiguracaoFirebase.getFirebase().child("conversas").child(idUsuarioLogado);

        //adiciona um evento que ficara escutando as conversas do usuário
        firebaseDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    conversas.clear();

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Conversa conversa = snapshot.getValue(Conversa.class);

                        conversas.add(conversa);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;

    }

}
