package app.calcounterapplication.com.tcc.model;

import com.google.firebase.database.DatabaseReference;

import app.calcounterapplication.com.tcc.config.ConfigFirebase;

public class Cliente extends PessoaFisica {
    private String cartaoDeCredido;

    public String getCartaoDeCredido() {
        return cartaoDeCredido;
    }

    public void setCartaoDeCredido(String cartaoDeCredido) {
        cartaoDeCredido = cartaoDeCredido;
    }

    public void salvar() {
        //super.salvar();
        DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference usuarios = firebaseRef.child("usuarios").child("clientes")
                .child(getId());
        usuarios.setValue(this);
    }

}

