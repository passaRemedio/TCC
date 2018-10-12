package app.calcounterapplication.com.tcc.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfigFirebase {

    private static DatabaseReference databaseReference;
    private static FirebaseAuth mAuth;

    //Criando a referência para o banco de dados
    //com  essa referencia conseguiremos adicionar e remover itens do firebase
    public static DatabaseReference getFirebaseDatabase(){
        if(databaseReference == null){
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

    //Esse metodo foi criado com o intuito de conseguir pegar a autorização para
    //alterar, incluir e excluir informações do banco.
    public static FirebaseAuth getFirebaseAuth(){

        if(mAuth == null){
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth;
    }

}
