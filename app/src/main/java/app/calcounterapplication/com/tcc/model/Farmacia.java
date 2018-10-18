package app.calcounterapplication.com.tcc.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import app.calcounterapplication.com.tcc.config.ConfigFirebase;

public class Farmacia extends Usuario implements Serializable {

    private String cnpj;
    private String cep;

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

//    public void salvar() {
//        //super.salvar();
//        DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
//        DatabaseReference usuarios = firebaseRef.child("usuarios").child("farmacias")
//                .child(getId());
//        usuarios.setValue(this);
//    }
}
