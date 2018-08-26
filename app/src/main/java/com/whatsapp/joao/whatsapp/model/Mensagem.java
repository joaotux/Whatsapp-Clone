package com.whatsapp.joao.whatsapp.model;

public class Mensagem {

    private String identificadorDestino;
    private String mensagem;

    public Mensagem() {
    }

    public String getIdentificadorDestino() {
        return identificadorDestino;
    }

    public void setIdentificadorDestino(String identificadorDestino) {
        this.identificadorDestino = identificadorDestino;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
