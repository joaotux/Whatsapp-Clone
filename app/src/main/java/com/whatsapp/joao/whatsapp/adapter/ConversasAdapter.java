package com.whatsapp.joao.whatsapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.flags.Flag;
import com.whatsapp.joao.whatsapp.R;
import com.whatsapp.joao.whatsapp.model.Conversa;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class ConversasAdapter extends ArrayAdapter<Conversa> {

    private Context context;
    private ArrayList<Conversa> conversas;
    private TextView txtNome;
    private TextView txtMensagem;

    public ConversasAdapter(@NonNull Context c, @NonNull ArrayList<Conversa> objects) {
        super(c, 0, objects);

        this.context = c;
        this.conversas = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        if(!conversas.isEmpty()) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_conversas, parent, false);

            txtNome = view.findViewById(R.id.txt_nome);
            txtMensagem = view.findViewById(R.id.txt_mensagem);

            Conversa conversa = conversas.get(position);
            txtNome.setText(conversa.getNome());
            txtMensagem.setText(conversa.getMensagem());

        }

        return view;
    }
}
