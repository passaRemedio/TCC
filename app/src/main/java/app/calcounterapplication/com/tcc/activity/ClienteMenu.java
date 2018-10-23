package app.calcounterapplication.com.tcc.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
import app.calcounterapplication.com.tcc.helper.RecyclerItemClickListener;
import app.calcounterapplication.com.tcc.model.Produto;
import dmax.dialog.SpotsDialog;

public class ClienteMenu extends Fragment {

    View myView;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerProdutoPublico;
    private Button BTRegiao, BTCategoria;
    private ImageView BTreset;
    private AdapterProduto adapterProduto;
    private List<Produto> listaProduto = new ArrayList<>();
    private DatabaseReference produtoPublicoRef;
    private AlertDialog dialog;
    private Context context;
    private String filtroRegiao = "";
    private String filtroCategoria = "";
    private boolean filtrandoPorRegiao = false;


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

        //configurando filtros
        BTRegiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtrarRegiao();
            }
        });

        BTCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtrarCategoria();
            }
        });

        BTreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recuperarProdutoPublico();
            }
        });

        recyclerProdutoPublico.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(), recyclerProdutoPublico,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Produto produtoSelecionado = listaProduto.get(position);
                                Intent i = new Intent(getActivity(), DetalheActivity.class);
                                i.putExtra("produtoSelecionado", produtoSelecionado);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }));

        return myView;
    }

    public void filtrarRegiao() {

        AlertDialog.Builder dialogEstado = new AlertDialog.Builder(getActivity());
        dialogEstado.setTitle("Selecione a região desejada");

        //configurar spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        //spinner estados
        final Spinner spinnerRegiao = viewSpinner.findViewById(R.id.spinnerFiltro);
        String[] regiao = getResources().getStringArray(R.array.regiao);

        //adicionar valores do spinner
        ArrayAdapter<String> adapterRegiao = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item,
                regiao
        );

        adapterRegiao.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinnerRegiao.setAdapter(adapterRegiao);

        dialogEstado.setView(viewSpinner);

        dialogEstado.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filtroRegiao = spinnerRegiao.getSelectedItem().toString();
                recuperarProutoRegiao();
                filtrandoPorRegiao = true;
            }
        });

        dialogEstado.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = dialogEstado.create();
        dialog.show();
    }

    public void filtrarCategoria() {

        if (filtrandoPorRegiao == true) {

            AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(getActivity());
            dialogCategoria.setTitle("Selecione a categoria");

            //configurar spinner
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

            //spinner estados
            final Spinner spinnerCategoria = viewSpinner.findViewById(R.id.spinnerFiltro);
            //spinner Categorias
            String[] categorias = getResources().getStringArray(R.array.categorias);

            //adicionar valores do spinner
            ArrayAdapter<String> adapterCategorias = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item,
                    categorias
            );

            adapterCategorias.setDropDownViewResource(android.R.layout
                    .simple_spinner_dropdown_item);
            spinnerCategoria.setAdapter(adapterCategorias);

            dialogCategoria.setView(viewSpinner);

            dialogCategoria.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                    recuperarProdutoCategoria();
                }
            });

            dialogCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogCategoria.create();
            dialog.show();

        } else {
            Toast.makeText(getActivity(), "Escolha primeiro uma região",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void recuperarProdutoCategoria() {

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setMessage("Filtrando Categoria")
                .setCancelable(false)
                .build();
        dialog.show();

        //configura nó por categoria
        produtoPublicoRef = ConfigFirebase.getFirebase()
                .child("produtos")
                .child(filtroRegiao)
                .child(filtroCategoria);

        produtoPublicoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaProduto.clear();
                for (DataSnapshot produtos : dataSnapshot.getChildren()) {

                    Produto produto = produtos.getValue(Produto.class);
                    listaProduto.add(produto);

                }

                Collections.reverse(listaProduto);
                adapterProduto.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void recuperarProutoRegiao() {

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setMessage("Filtrando Região")
                .setCancelable(false)
                .build();
        dialog.show();

        //configura nó por estado
        produtoPublicoRef = ConfigFirebase.getFirebase()
                .child("produtos")
                .child(filtroRegiao);

        produtoPublicoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaProduto.clear();
                for (DataSnapshot categorias : dataSnapshot.getChildren()) {
                    for (DataSnapshot produtos : categorias.getChildren()) {

                        Produto produto = produtos.getValue(Produto.class);
                        listaProduto.add(produto);

                    }
                }

                Collections.reverse(listaProduto);
                adapterProduto.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                }

                Collections.reverse(listaProduto);
                adapterProduto.notifyDataSetChanged();
                dialog.dismiss();

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
        BTreset = myView.findViewById(R.id.imageReset);
    }

}
