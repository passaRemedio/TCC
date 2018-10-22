package app.calcounterapplication.com.tcc.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.calcounterapplication.com.tcc.Adapter.AdapterProduto;
import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.model.Produto;
import dmax.dialog.SpotsDialog;

public class ClienteMenu extends Fragment {

    View myView;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerProdutoPublico;
    private Button BTRegiao, BTCategoria;
    private AdapterProduto adapterProduto;
    private List<Produto> listaProduto = new ArrayList<>();
    private DatabaseReference produtoPublicoRef;
    private AlertDialog dialog;
    private Context context;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.cliente_menu, container, false);

        mAuth = ConfigFirebase.getFirebaseAuth();
        produtoPublicoRef = ConfigFirebase.getFirebase()
                .child("produtos");

        inicializarComponentes();

        //configurar recyclerView
        recyclerProdutoPublico.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerProdutoPublico.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(listaProduto, getActivity());
        recyclerProdutoPublico.setAdapter(adapterProduto);

        recuperarProdutoPublico();

        return myView
    }

    private void recuperarProdutoPublico() {

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setMessage("Carregando produtos !")
                .setCancelable(false)
                .build();
        dialog.show();

        listaProduto.clear();
        produtoPublicoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot regiao : dataSnapshot.getChildren()) {
                    for (DataSnapshot categorias : regiao.getChildren()) {
                        for (DataSnapshot produtos : categorias.getChildren()) {

                            Produto produto = produtos.getValue(Produto.class);
                            listaProduto.add(produto);

                        }
                    }

                    Collections.reverse(listaProduto);
                    adapterProduto.notifyDataSetChanged();
                    dialog.dismiss();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void inicializarComponentes() {

        recyclerProdutoPublico = myView.findViewById(R.id.recyclerProdutosCliente);
        BTRegiao = myView.findViewById(R.id.BTRegiao);
        BTCategoria = myView.findViewById(R.id.BTCategoria);
    }

}
