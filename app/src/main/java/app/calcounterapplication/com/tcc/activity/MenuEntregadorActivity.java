package app.calcounterapplication.com.tcc.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;

public class MenuEntregadorActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ImageButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_entregador);

        mAuth = ConfigFirebase.getFirebaseAuth();

        logoutButton = findViewById(R.id.logoutImageButtonID);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutEntregador();
            }
        });

    }

    public void logoutEntregador(){
        mAuth.signOut();
        startActivity(new Intent( this, MainActivity.class));
        finish();
    }
}
