package com.whatsapp.joao.whatsapp.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.HashMap;

public class Preferencias {

    private Context contexto;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private static final String NOME_ARQUIVO = "whatsapp.preferencias";
    private static final int MODE = 0;
    private static final String CHAVE_IDENTIFICADOR = "identificador";
    private static final String CHAVE_NOME = "nome";



    public Preferencias(Context contexto) {
        this.contexto = contexto;
        this.preferences = this.contexto.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = this.preferences.edit();
    }

    public void salvarDados(String identificado, String nome) {
        editor.putString(CHAVE_IDENTIFICADOR, identificado);
        editor.putString(CHAVE_NOME, nome);

        editor.commit();
    }

    public String getIdentificadorUsuario() {
        return preferences.getString(CHAVE_IDENTIFICADOR, null);
    }

    public String getNomeUsuario() {
        return preferences.getString(CHAVE_NOME, null);
    }

    public boolean enviaSms(String telefone, String mensagem) {

        try {

            SmsManager envioSms = SmsManager.getDefault();
            envioSms.sendTextMessage(telefone, null, mensagem, null, null);

            return true;
        } catch (Exception e) {
            e.getStackTrace();
            return false;
        }

    }
}
