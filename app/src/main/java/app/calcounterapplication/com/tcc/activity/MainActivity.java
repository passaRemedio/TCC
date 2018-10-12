package app.calcounterapplication.com.tcc.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import app.calcounterapplication.com.tcc.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void criarCadastro(View view){
        startActivity(new Intent(this, TipoDeCadastro.class));
    }

    public void abrirLogin(View view ){
        startActivity(new Intent( this, LoginActivity.class));
    }

}
