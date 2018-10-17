package app.calcounterapplication.com.tcc.activity.Farmacia;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.helper.UsuarioFirebase;
import app.calcounterapplication.com.tcc.model.Usuario;

public class CadastroFarmaciaActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button BTFarmaciaCadastro;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_farmacia);

        inicializarComponentes();

        BTFarmaciaCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome = campoNome.getText().toString();
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if (!nome.isEmpty()) {

                    if (!email.isEmpty()) {

                        if (!senha.isEmpty()) {

                            final Usuario usuario = new Usuario();
                            usuario.setNome(nome);
                            usuario.setEmail(email);
                            usuario.setSenha(senha);
                            usuario.setTipo("F");

                            mAuth = ConfigFirebase.getFirebaseAuth();
                            mAuth.createUserWithEmailAndPassword(
                                    usuario.getEmail(), usuario.getSenha()
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        try {

                                            String idUsuario = task.getResult().getUser().getUid();
                                            usuario.setId(idUsuario);
                                            usuario.salvar();

                                            //Atualizar nome do Usuario no UserProfile
                                            UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());

                                            startActivity(new Intent(CadastroFarmaciaActivity.this,
                                                    MenuFarmaciaActivity.class));
                                            finish();

                                            Toast.makeText(CadastroFarmaciaActivity.this,
                                                    "Sucesso ao cadastrar Farmácia!",
                                                    Toast.LENGTH_SHORT).show();


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    } else {

                                        String excecao = "";

                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            excecao = "Digite uma senha mais forte!";
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            excecao = "Por favor, digite um e-mail válido";
                                        } catch (FirebaseAuthUserCollisionException e) {
                                            excecao = "Esta conta já foi cadastrada!";
                                        } catch (Exception e) {
                                            excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                                            e.printStackTrace();
                                        }

                                        Toast.makeText(CadastroFarmaciaActivity.this,
                                                excecao,
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        } else {

                            Toast.makeText(getApplicationContext(),
                                    "Preencha a Senha",
                                    Toast.LENGTH_LONG).show();
                        }

                    } else {

                        Toast.makeText(getApplicationContext(),
                                "Preencha o Email",
                                Toast.LENGTH_LONG).show();
                    }

                } else {

                    Toast.makeText(getApplicationContext(),
                            "Preencha o Nome",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void inicializarComponentes() {
        campoNome = findViewById(R.id.editFarmaciaNome);
        campoEmail = findViewById(R.id.editEntregadorEmail);
        campoSenha = findViewById(R.id.editFarmaciaSenha);
        BTFarmaciaCadastro = findViewById(R.id.BTFarmaciaCadastro);
    }
}
