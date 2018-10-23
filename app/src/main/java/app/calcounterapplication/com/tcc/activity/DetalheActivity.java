package app.calcounterapplication.com.tcc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.calcounterapplication.com.tcc.R;

public class DetalheActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        //Mostrar o botão
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Ativar o botão
        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setTitle("Seu titulo aqui");
    }
}
