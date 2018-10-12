package app.calcounterapplication.com.tcc.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import app.calcounterapplication.com.tcc.R;

public class TipoDeCadastro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_de_cadastro);

    }

    public void  cadastroPassageiro(View view){
        startActivity(new Intent(this, CadastroClienteActivity.class));
    }

    public void cadastroMotorista(View view ){
        startActivity(new Intent(this, CadastroMotoristaActivity.class));
    }

    public void cadastroFarmacia(View view){
        startActivity(new Intent(this, MapsActivity.class));
    }

}