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
import com.whatsapp.joao.whatsapp.helper.Preferencias;
import com.whatsapp.joao.whatsapp.model.Mensagem;

import java.util.ArrayList;

public class MensagensAdapter extends ArrayAdapter<Mensagem> {

    private Context context;
    private ArrayList<Mensagem> mensagems;
    private TextView txtMensagem;

    public MensagensAdapter(@NonNull Context c, @NonNull ArrayList<Mensagem> objects) {
        super(c, 0, objects);

        this.context = c;
        this.mensagems = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        if(!mensagems.isEmpty()) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //recupera id do remetente
            Preferencias preferencias = new Preferencias(context);
            String idRemetente = preferencias.getIdentificadorUsuario();

            //pega mensagem atual
            Mensagem mensagem = mensagems.get(position);

            //monta view atraves do xml
            if(idRemetente.equals(mensagem.getIdentificadorDestino())) {

                view = inflater.inflate(R.layout.mensagem_esquesda, parent, false);

            } else {

                view = inflater.inflate(R.layout.mensagem_direita, parent, false);

            }

            txtMensagem = view.findViewById(R.id.txtMensagem);

            txtMensagem.setText(mensagem.getMensagem());
        }

        return view;
    }
}
