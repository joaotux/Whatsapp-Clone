package com.whatsapp.joao.whatsapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.whatsapp.joao.whatsapp.R;
import com.whatsapp.joao.whatsapp.model.Contato;

import java.util.ArrayList;
import java.util.List;

public class ContatosAdapter extends ArrayAdapter<Contato> {

    private ArrayList<Contato> contatos;
    private Context context;

    public ContatosAdapter(@NonNull Context c, @NonNull ArrayList<Contato> objects) {
        super(c, 0, objects);

        this.contatos = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        //verifica se a lista não esta vazia
        if(contatos != null) {

            //inicia objeto para montar a view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //monta view apartar do xml
            view = inflater.inflate(R.layout.lista_contatos, parent, false);

            //recupera elemento para exibição
            TextView txtNome = view.findViewById(R.id.tv_nome);
            TextView txtEmail = view.findViewById(R.id.tv_email);

            Contato contato = contatos.get(position);
            txtNome.setText(contato.getNome());
            txtEmail.setText(contato.getEmail());
        }

        return view;
    }
}
