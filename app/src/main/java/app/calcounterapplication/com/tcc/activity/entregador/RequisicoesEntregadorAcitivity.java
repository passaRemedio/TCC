package app.calcounterapplication.com.tcc.activity.entregador;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import app.calcounterapplication.com.tcc.Adapter.RequisicoesAdapter;
import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.activity.CorridaActivity;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.helper.RecyclerItemClickListener;
import app.calcounterapplication.com.tcc.helper.UsuarioFirebase;
import app.calcounterapplication.com.tcc.model.Requisicao;
import app.calcounterapplication.com.tcc.model.Usuario;

public class RequisicoesEntregadorAcitivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference firebaseRef;

    private RecyclerView recyclerRequisicoes;
    private TextView textResultado;
    private List<Requisicao> listaRequisicoes = new ArrayList<>();
    private RequisicoesAdapter adapter;
    private Usuario entregador;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisicoes_entregador);

        inicializarComponentes();

        //Recuperar localização do usuario
        recuperarLocalizacaoUsuario();

    }

    private void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                if( !latitude.isEmpty() && !longitude.isEmpty() ){
                    entregador.setLatitude(latitude);
                    entregador.setLongitude(longitude);

                    locationManager.removeUpdates(locationListener);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //Solicitar atualizacoes de localizacao
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locationListener
            );
        }

    }


    private void inicializarComponentes(){

        getSupportActionBar().setTitle("Requisições");

        //Configurações inicias
        entregador = UsuarioFirebase.getDadosUsuarioLogado();
        mAuth = ConfigFirebase.getFirebaseAuth();
        firebaseRef = ConfigFirebase.getFirebaseDatabase();

        //Configuração de componentes
        recyclerRequisicoes = findViewById(R.id.recyclerRequisicoes);
        textResultado = findViewById(R.id.textResultado);

        //Configurar RecyclerView
        adapter = new RequisicoesAdapter(listaRequisicoes, getApplicationContext(), entregador);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerRequisicoes.setLayoutManager(layoutManager);
        recyclerRequisicoes.setHasFixedSize(true);
        recyclerRequisicoes.setAdapter(adapter);

        //Adiciona evento de clique no recycler
        recyclerRequisicoes.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerRequisicoes,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Requisicao requisicao = listaRequisicoes.get(position);
                                Intent i  = new Intent(RequisicoesEntregadorAcitivity.this, CorridaActivity.class);
                                i.putExtra("idRequisicao", requisicao.getId());
                                i.putExtra("entregador", entregador);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );


        recuperarRequisicoes();

    }

    private void recuperarRequisicoes(){
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        Query requisicaoPesquisa = requisicoes.orderByChild("status")
                .equalTo(Requisicao.STATUS_AGUARDANDO);

        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if( dataSnapshot.getChildrenCount() > 0 ){
                    textResultado.setVisibility(View.GONE);
                    recyclerRequisicoes.setVisibility(View.VISIBLE);
                } else {
                    textResultado.setVisibility(View.VISIBLE);
                    recyclerRequisicoes.setVisibility(View.GONE);
                }

                listaRequisicoes.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Requisicao requisicao = ds.getValue(Requisicao.class);
                    listaRequisicoes.add(requisicao);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
