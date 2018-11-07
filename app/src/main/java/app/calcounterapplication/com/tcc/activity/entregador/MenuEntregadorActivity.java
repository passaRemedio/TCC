package app.calcounterapplication.com.tcc.activity.entregador;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.activity.MainActivity;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;

public class MenuEntregadorActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ImageView logoutButton;
    private ImageButton minhasRequisicoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_entregador);

        mAuth = ConfigFirebase.getFirebaseAuth();

        logoutButton = findViewById(R.id.logoutFarmacia);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutFarmacia();
            }
        });

        minhasRequisicoes = findViewById(R.id.requisicoesEntregador);
        minhasRequisicoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaRequisicoes();
            }
        });

        getSupportActionBar().setTitle("Entregador");
    }

    public void logoutFarmacia(){
        mAuth.signOut();
        startActivity(new Intent( this, MainActivity.class));
        finish();
    }

    public void abrirTelaRequisicoes() {

        startActivity(new Intent(this, RequisicoesEntregadorAcitivity.class));
    }
}
