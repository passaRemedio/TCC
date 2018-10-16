package app.calcounterapplication.com.tcc.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import app.calcounterapplication.com.tcc.config.ConfigFirebase;

public class Farmacia implements Serializable {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String tipo;

    private String latitude;
    private String longitude;

    public Farmacia() {
    }

    public void salvar() {
        DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference farmacia = firebaseRef.child("usuarios")
                .child("farmacia").child(getId());

        farmacia.setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
