package app.calcounterapplication.com.tcc.activity.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.util.Calendar;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.activity.ClienteNavigationDrawer;
import app.calcounterapplication.com.tcc.activity.MainActivity;
import app.calcounterapplication.com.tcc.activity.MenuEntregadorActivity;
import app.calcounterapplication.com.tcc.activity.farmacia.MenuFarmaciaActivity;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.helper.DatePickerFragment;
import app.calcounterapplication.com.tcc.helper.DatePickerFragmentDois;
import app.calcounterapplication.com.tcc.model.Cliente;
import app.calcounterapplication.com.tcc.model.Usuario;

public class DadosCliente extends Fragment{

    //ta foda essa classe

    private FirebaseAuth mAuth;
    private String cep, cidade, cpf, dtNascimento, email, nome, numero, rg, rua, uf;
    private EditText clienteCep, clienteCidade, clienteCpf, clienteDtNascimento, clienteEmail,
                    clienteNome, clienteNumero, clienteRg, clienteRua, clienteUf;
    private Button selecionarData, alterarCadastro;
    private String selectedDate;

    private static String dataNascimento = "";
    public static final int REQUEST_CODE = 11; // Used to identify the result
    private OnFragmentInteractionListener mListener;

    public DadosCliente(){
        //Required empty public constructor
    }

    public static DadosCliente newInstance() {
        DadosCliente fragment = new DadosCliente();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.dados_cliente, container, false);
        setHasOptionsMenu(true);
        inicializarComponentes(myView);
        puxarDados();

        // get fragment manager so we can launch from fragment
        final FragmentManager fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager();

        selecionarData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create the datePickerFragment
                AppCompatDialogFragment newFragment = new DatePickerFragmentDois();
                // set the targetFragment to receive the results, specifying the request code
                newFragment.setTargetFragment(DadosCliente.this, REQUEST_CODE);
                // show the datePicker
                newFragment.show(fm, "datePicker");
            }
        });

        alterarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alterarCadastro();
            }
        });

        return myView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //check for results
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get date from string
            selectedDate = data.getStringExtra("selectedDate");
            // set the value of the editText
            clienteDtNascimento.setHint(selectedDate);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                mAuth = ConfigFirebase.getFirebaseAuth();
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void puxarDados(){

        FirebaseUser user = getUsuarioAtual();
        if(user != null){
            DatabaseReference usuariosRef = ConfigFirebase.getFirebaseDatabase()
                    .child("usuarios")
                    .child( getIdentificadorUsuario() );
            usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Cliente cliente = dataSnapshot.getValue(Cliente.class);

                    cep = cliente.getCep();
                    cidade = cliente.getCidade();
                    cpf = cliente.getCpf();
                    dtNascimento = cliente.getDtNascimento();
                    email = cliente.getEmail();
                    nome = cliente.getNome();
                    numero = cliente.getNumero();
                    rg = cliente.getRg();
                    rua = cliente.getRua();
                    uf = cliente.getUf();


                    clienteCep.setHint(cep);
                    clienteCidade.setHint(cidade);
                    clienteCpf.setHint(cpf);
                    clienteDtNascimento.setHint(dtNascimento);
                    clienteEmail.setHint(email);
                    clienteNome.setHint(nome);
                    clienteNumero.setHint(numero);
                    clienteRg.setHint(rg);
                    clienteRua.setHint(rua);
                    clienteUf.setHint(uf);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public static FirebaseUser getUsuarioAtual() {
        FirebaseAuth usuario = ConfigFirebase.getFirebaseAuth();
        return usuario.getCurrentUser();
    }

    public static String getIdentificadorUsuario() {
        return getUsuarioAtual().getUid();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragmentDois();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void alterarCadastro(){

        System.out.println("Olha aqui: " + clienteNome.getText());

//        FirebaseUser user = getUsuarioAtual();
//
//        FirebaseUser user = getUsuarioAtual();
//        if(user != null){
//            DatabaseReference usuariosRef = ConfigFirebase.getFirebaseDatabase()
//                    .child("usuarios")
//                    .child( getIdentificadorUsuario() );
//            usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                    Cliente cliente = dataSnapshot.getValue(Cliente.class);
//
//                    if(clienteCep.)
//                    cliente.setCep(cep);
//
//                    cidade = cliente.getCidade();
//                    cpf = cliente.getCpf();
//                    dtNascimento = cliente.getDtNascimento();
//                    email = cliente.getEmail();
//                    nome = cliente.getNome();
//                    numero = cliente.getNumero();
//                    rg = cliente.getRg();
//                    rua = cliente.getRua();
//                    uf = cliente.getUf();
//
//
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
    }


    public void inicializarComponentes(View view){
        clienteCep = (EditText) view.findViewById(R.id.editarClienteCEP);
        clienteCidade = (EditText) view.findViewById(R.id.editarClienteCidade);
        clienteCpf  = (EditText) view.findViewById(R.id.editarClienteCPF);
        clienteDtNascimento = (EditText) view.findViewById(R.id.editarDataID);
        clienteEmail = (EditText) view.findViewById(R.id.editarClienteEmail);
        clienteNome = (EditText) view.findViewById(R.id.editarClienteNome);
        clienteNumero = (EditText) view.findViewById(R.id.editarClienteNumero);
        clienteRg = (EditText) view.findViewById(R.id.editarClienteRG);
        clienteRua = (EditText) view.findViewById(R.id.editarClienteRua);
        clienteUf = (EditText) view.findViewById(R.id.editarClienteUF);
        selecionarData = (Button) view.findViewById(R.id.selecionarData);
        alterarCadastro = (Button) view.findViewById(R.id.alterarCadastroBtn);
    }
}
