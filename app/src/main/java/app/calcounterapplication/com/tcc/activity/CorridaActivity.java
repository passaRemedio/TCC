package app.calcounterapplication.com.tcc.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.activity.entregador.RequisicoesEntregadorAcitivity;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.helper.UsuarioFirebase;
import app.calcounterapplication.com.tcc.model.Cliente;
import app.calcounterapplication.com.tcc.model.Destino;
import app.calcounterapplication.com.tcc.model.Requisicao;
import app.calcounterapplication.com.tcc.model.Usuario;

public class CorridaActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    /*
    *Localizaçao
    *   entregador: Latitude -> -15.811 / Longitude -> -47.8896714
    *   farmacia: Latitude -> -15.8149985 / Longitude -> -47.8896714
    *   cliente: Latitude -> -15.813 / Longitude -> -47.8896714
     */

    //Componente
    private Button buttonAceitarCorrida;
    private FloatingActionButton fabRota;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localEntregador;
    private LatLng localCliente;
    private LatLng localFarmacia;
    private Usuario entregador;
    private Usuario cliente;
    private Usuario farmacia;
    private String idRequisicao;
    private Requisicao requisicao;
    private DatabaseReference firebaseRef;
    private TextView nomeCliente, enderecoCliente;
    private Marker marcadorEntregador;
    private Marker marcadorCliente;
    private Marker marcadorFarmacia;
    private String nomeFarmacia;
    private String statusRequisicao;
    private Boolean requisicaoAtiva;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corrida);

        inicializarComponentes();

        //Recuperar dados do usuário
        if(getIntent().getExtras().containsKey("idRequisicao")
                && getIntent().getExtras().containsKey("entregador")){
            Bundle extras = getIntent().getExtras();
            entregador = (Usuario) extras.getSerializable("entregador");
            localEntregador = new LatLng(
                    Double.parseDouble(entregador.getLatitude()),
                    Double.parseDouble(entregador.getLatitude())
            );
            idRequisicao = extras.getString("idRequisicao");
            requisicaoAtiva = extras.getBoolean("requisicaoAtiva");
            verificaStatusRequisicao();
        }

    }

    private void verificaStatusRequisicao(){

        final DatabaseReference requisicoes = firebaseRef.child("requisicoes")
                .child(idRequisicao);
        requisicoes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Recupera a requisicao
                requisicao = dataSnapshot.getValue(Requisicao.class);
                cliente = requisicao.getCliente();
                localCliente = new LatLng(
                        Double.parseDouble(cliente.getLatitude()),
                        Double.parseDouble(cliente.getLongitude())
                );

                Destino destino = requisicao.getDestino();
                localFarmacia = new LatLng(
                        Double.parseDouble(destino.getLatitude()),
                        Double.parseDouble(destino.getLongitude())
                );

                statusRequisicao = requisicao.getStatus();
                alteraInterfaceStatusRequisicao(statusRequisicao);

                nomeCliente.setText("Cliente: " + requisicao.getCliente().getNome());

                nomeFarmacia = requisicao.getDestino().getNomeDestino();
                enderecoCliente.setText("Nome farmácia: " + nomeFarmacia);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void alteraInterfaceStatusRequisicao(String status){
        switch ( status ){
            case Requisicao.STATUS_AGUARDANDO :
                requisicaoAguardando();
                break;
            case Requisicao.STATUS_A_CAMINHO :
                requisicaoAcaminho();
                break;
            case Requisicao.STATUS_VIAGEM:
                requisicaoViagem();
                break;
        }
    }

    private void requisicaoAguardando(){
        buttonAceitarCorrida.setText("Realizar Entrega");

        //Exibe marcador do entregador
        adicionaMarcadorEntregador(localEntregador, entregador.getNome() );

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localEntregador, 20));
    }

    private void requisicaoAcaminho(){
        buttonAceitarCorrida.setText("A caminho da farmácia");
        fabRota.setVisibility(View.VISIBLE);

        //Exibe marcador do entregador
        adicionaMarcadorEntregador(localEntregador, entregador.getNome() );

        //Exibe marcador cliente
        adicionaMarcadorCliente(localCliente, cliente.getNome());

//        //Exibe marcador farmacia
        adicionaMarcadorFarmacia(localFarmacia, nomeFarmacia);

        //Centralizar dois marcadores
//        centralizarDoisMarcadores(marcadorEntregador, marcadorCliente);

        //Centralizar Tres marcadores
        centralizarTresMarcadores(marcadorEntregador, marcadorCliente, marcadorFarmacia);

        //Inicia monitoramento do entregador / farmácia
        iniciarMonitoramento(entregador, localFarmacia, Requisicao.STATUS_VIAGEM);
    }

    private void requisicaoViagem(){

        //Alterar interface
        fabRota.setVisibility(View.VISIBLE);
        buttonAceitarCorrida.setText("A caminho do destino");

        //Exibe marcador do motorista
        adicionaMarcadorEntregador(localEntregador, entregador.getNome());

        //Exibe marcador de cliente
        LatLng localCliente = new LatLng(
                Double.parseDouble(cliente.getLatitude()),
                Double.parseDouble(cliente.getLongitude())
        );
        adicionaMarcadorClienteViagem(localCliente, "Cliente");

        //Centraliza marcadores motorista / destino
        iniciarMonitoramento(entregador, localCliente, Requisicao.STATUS_FINALIZADA);


    }

    private void iniciarMonitoramento(final Usuario usuarioOrigem, LatLng localDestino, final String status) {

        //Inicializar Geofire
        DatabaseReference localUsuario = ConfigFirebase.getFirebaseDatabase()
                .child("local_usuario");
        GeoFire geoFire = new GeoFire(localUsuario);

        //Adiciona círculo no destino
        final Circle circulo = mMap.addCircle(
                new CircleOptions()
                        .center( localDestino )
                        .radius( 50 ) //em metros
                        .fillColor(Color.argb(90, 255, 153, 0))
                        .strokeColor(Color.argb(190, 255, 152, 0))
        );

        final GeoQuery geoQuery = geoFire.queryAtLocation(
                new GeoLocation(localDestino.latitude, localDestino.longitude),
                0.05 //em km (0.05 50 metros)
        );
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(key.equals(usuarioOrigem.getId())){
                    //Log.d("onKeyEntered", "onKeyEntered: motorista esta dentro da area");


                    //Altera status da requisicao
                    requisicao.setStatus(status);
                    requisicao.atualizarStatus();

                    //Remove listener
                    geoQuery.removeAllListeners();
                    circulo.remove();

                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void centralizarTresMarcadores(Marker marcador1, Marker marcador2, Marker marcador3){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(marcador1.getPosition());
        builder.include(marcador2.getPosition());
        builder.include(marcador3.getPosition());

        LatLngBounds bounds = builder.build();

        int largura = getResources().getDisplayMetrics().widthPixels;
        int altura = getResources().getDisplayMetrics().heightPixels;
        int espacoInterno = (int) (largura * 0.20);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(bounds,largura,altura,espacoInterno)
        );
    }

    private void centralizarDoisMarcadores(Marker marcador1, Marker marcador2){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include( marcador1.getPosition() );
        builder.include( marcador2.getPosition() );

        LatLngBounds bounds = builder.build();

        int largura = getResources().getDisplayMetrics().widthPixels;
        int altura = getResources().getDisplayMetrics().heightPixels;
        int espacoInterno = (int) (largura * 0.20);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(bounds,largura,altura,espacoInterno)
        );

    }

    private void adicionaMarcadorFarmacia(LatLng localizacao, String titulo){
        if( marcadorFarmacia != null )
            marcadorFarmacia.remove();

        marcadorFarmacia = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pharmacy))
        );
    }

    private void adicionaMarcadorEntregador(LatLng localizacao, String titulo){

        if( marcadorEntregador != null )
            marcadorEntregador.remove();

        marcadorEntregador = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.scooter))
        );

    }

    private void adicionaMarcadorCliente(LatLng localizacao, String titulo){

        if( marcadorCliente != null )
            marcadorCliente.remove();

        marcadorCliente = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario))
        );

    }

    private void adicionaMarcadorClienteViagem(LatLng localizacao, String titulo){

        if( marcadorCliente != null )
            marcadorFarmacia.remove();

        marcadorCliente = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario))
        );

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Recuperar localizacao do usuario
        recuperarLocalizacaoUsuario();
    }

    private void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                localEntregador = new LatLng(latitude, longitude);

//                mMap.clear();
//                mMap.addMarker(
//                        new MarkerOptions()
//                                .position(localEntregador)
//                                .title("Meu Local")
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.scooter))
//                );
//                mMap.moveCamera(
//                        CameraUpdateFactory.newLatLngZoom(localEntregador, 20)
//                );

                //Atualizar Geofire
                UsuarioFirebase.atualizarDadosLocalizacao(latitude, longitude);


                alteraInterfaceStatusRequisicao(statusRequisicao);

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
                    10000,
                    10,
                    locationListener
            );
        }


    }

    public void realizarEntrega(View view) {

        //Configurar Requisicao
        requisicao = new Requisicao();
        requisicao.setId(idRequisicao);
        requisicao.setEntregador(entregador);
        requisicao.setStatus(Requisicao.STATUS_A_CAMINHO);

        requisicao.atualizar();
    }

    private void inicializarComponentes(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Iniciar Entrega");

        //Configuracoes iniciais
        buttonAceitarCorrida = findViewById(R.id.buttonAceitarCorrida);
        firebaseRef = ConfigFirebase.getFirebaseDatabase();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        nomeCliente = findViewById(R.id.nomeCliente);
        enderecoCliente = findViewById(R.id.enderecoCliente);

        //Adiciona evento de clique no FabRota
        fabRota = findViewById(R.id.fabRota);
        fabRota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = statusRequisicao;

                if(status != null && !status.isEmpty()) {
                    String lat = "";
                    String lon = "";

                    switch (status) {
                        case Requisicao.STATUS_A_CAMINHO:
                            lat = String.valueOf(localFarmacia.latitude);
                            lon = String.valueOf(localFarmacia.longitude);
                            break;
                        case Requisicao.STATUS_VIAGEM:
                            lat = String.valueOf(localCliente.latitude);
                            lon = String.valueOf(localCliente.longitude);
                            break;
                    }

                    //Abrir rota
                    String latLong = lat + "," + lon;
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latLong + "&mode=d");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        System.out.println("Olha aqui: " + requisicaoAtiva);
        if(requisicaoAtiva){
            System.out.println("Olha aqui: " + requisicaoAtiva);
            Toast.makeText(CorridaActivity.this,
                    "Necessário encerrar a requisição atual!",
                    Toast.LENGTH_SHORT).show();
        } else{
            Intent i = new Intent(CorridaActivity.this, RequisicoesEntregadorAcitivity.class);
            startActivity(i);
        }

        return false;
    }

}
