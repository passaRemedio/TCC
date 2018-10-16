package app.calcounterapplication.com.tcc.helper;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import app.calcounterapplication.com.tcc.activity.ClienteNavigationDrawer;
import app.calcounterapplication.com.tcc.activity.MainActivity;
import app.calcounterapplication.com.tcc.activity.MapsActivity;
import app.calcounterapplication.com.tcc.activity.MenuClienteActivity;
import app.calcounterapplication.com.tcc.activity.MenuEntregadorActivity;
import app.calcounterapplication.com.tcc.activity.MenuFarmaciaActivity;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.model.Usuario;

public class UsuarioFirebase {


    public static FirebaseUser getUsuarioAtual() {
        FirebaseAuth usuario = ConfigFirebase.getFirebaseAuth();
        return usuario.getCurrentUser();
    }

    public static Usuario getDadosUsuarioLogado() {
        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setId(firebaseUser.getUid());
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());

        return usuario;
    }

    public static boolean atualizarNomeUsuario(String nome) {

        try {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profileChangeRequest = new
                    UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();

            user.updateProfile(profileChangeRequest).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Log.d("Perfil ", "Erro ao atualizar o nome do perfil.");
                            }
                        }
                    });

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void redirecionaUsuarioLogado(final Activity activity) {

        FirebaseUser user = getUsuarioAtual();
        if (user != null) {

            DatabaseReference usuariosRef = ConfigFirebase.getFirebaseDatabase()
                    .child("usuarios")
                    .child(getIdentificadorUsuario());
            usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Usuario usuario = dataSnapshot.getValue(Usuario.class);

                    String tipoUsuario = usuario.getTipo();
                    if (tipoUsuario.equals("M")) {

                        activity.startActivity(new Intent(activity, MenuEntregadorActivity.class));

                    } else if (tipoUsuario.equals("C")) {

                        activity.startActivity(new Intent(activity, ClienteNavigationDrawer.class));

                    } else {
                        activity.startActivity(new Intent(activity, MenuFarmaciaActivity.class));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


    public static String getIdentificadorUsuario() {
        return getUsuarioAtual().getUid();
    }


}