package app.calcounterapplication.com.tcc.activity.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.helper.UsuarioFirebase;
import app.calcounterapplication.com.tcc.model.Destino;
import app.calcounterapplication.com.tcc.model.Requisicao;
import app.calcounterapplication.com.tcc.model.Usuario;

public class PedidosCliente extends Fragment
        implements OnMapReadyCallback {


    private AlertDialog dialog;
    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DatabaseReference firebaseRef;
    private LatLng localCliente;
    private boolean entregadorChamado = false;
    private Requisicao requisicao;
    private String enderecoFarmacia;
    private Button buttonCancelarEntrega;
    private TextView acompanharPedido, acompanharEntregador;
    private LinearLayout pedidosFeitos;

    private View myView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.pedidos_cliente, container, false);

//        enderecoFarmacia = getActivity().getIntent().getExtras().getString("enderecoFarmacia");
        Bundle bundle = getArguments();

        inicializarComponentes();

        try{
            enderecoFarmacia = bundle.getString("enderecoFarmacia");
        } catch (Exception e){
            verificaStatusRequisicao();
        }

//        inicializarComponentes();

        return myView;
    }

    private void verificaStatusRequisicao(){

        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference requisicoes  = firebaseRef.child("requisicoes");
        Query requisicaoPesquisa = requisicoes.orderByChild("cliente/id")
                .equalTo(usuarioLogado.getId());

        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Requisicao> lista = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    requisicao = ds.getValue(Requisicao.class);
                    lista.add(requisicao);
                }

                Collections.reverse(lista);
                if(lista != null && lista.size() > 0){
                    requisicao = lista.get(0);

                    Log.d("resultado", "onDataChange: " + requisicao.getId());

                    switch (requisicao.getStatus()) {
                        case Requisicao.STATUS_AGUARDANDO:
                            buttonCancelarEntrega.setVisibility(View.VISIBLE);
                            entregadorChamado = true;
                            break;
                    }
                } else {

                    acompanharPedido.setText("Não foi realizado nenhum pedido :(");
                    acompanharEntregador.setVisibility(View.INVISIBLE);
                    pedidosFeitos.setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Recuperar localizacao do usuario
        recuperarLocalizacaoCliente();
        if(localCliente == null){
            Toast.makeText(getActivity(),
                    "Aguarde um instante",
                    Toast.LENGTH_SHORT).show();
        } else {
            recuperaEnderecoFarmacia(enderecoFarmacia);
        }
    }

    private void salvarRequisicao(Destino destino){
        Requisicao requisicao = new Requisicao();
        requisicao.setDestino(destino);

        Usuario usuarioCliente = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioCliente.setLatitude( String.valueOf(localCliente.latitude) );
        usuarioCliente.setLongitude( String.valueOf(localCliente.longitude) );

        requisicao.setCliente(usuarioCliente);
        requisicao.setStatus(Requisicao.STATUS_AGUARDANDO);
        requisicao.salvar();

        buttonCancelarEntrega.setVisibility(View.VISIBLE);
    }

    private void recuperaEnderecoFarmacia(String enderecoFarmacia) {


        if(enderecoFarmacia != null){
            if (!enderecoFarmacia.equals("") || enderecoFarmacia != null) {
                Address addressDestino = recuperarEndereco(enderecoFarmacia);
                if (addressDestino != null) {
                    final Destino destino = new Destino();
                    destino.setCidade(addressDestino.getAdminArea());
                    destino.setCep(addressDestino.getPostalCode());
                    destino.setBairro(addressDestino.getSubLocality());
                    destino.setRua(addressDestino.getThoroughfare());
                    destino.setNumero(addressDestino.getFeatureName());
                    destino.setLatitude(String.valueOf(addressDestino.getLatitude()));
                    destino.setLongitude(String.valueOf(addressDestino.getLongitude()));

                    salvarRequisicao(destino);
                    entregadorChamado = true;

                } else {
                    Toast.makeText(getActivity(),
                            "Por algum motivo a farmacia não possui endereço!",
                            Toast.LENGTH_SHORT).show();
                }
                //Fim
            }
        }
    }

    private void recuperarLocalizacaoCliente() {

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                System.out.println("Latitute: " + latitude);
                System.out.println("Longitude: " + longitude);
                localCliente = new LatLng(latitude, longitude);

                mMap.clear();
                mMap.addMarker(
                        new MarkerOptions()
                                .position(localCliente)
                                .title("Meu Local")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario))
                );
                mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(localCliente, 20)
                );

                if(entregadorChamado != true){
                    recuperaEnderecoFarmacia(enderecoFarmacia);
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
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10,
                    10,
                    locationListener
            );
        } else {
            System.out.println("VAI TOMAR NO CU");
        }

    }

    private Address recuperarEndereco(String endereco) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            List<Address> listaEndereco = geocoder.getFromLocationName(endereco, 1);

            if (listaEndereco != null && listaEndereco.size() > 0) {
                Address address = listaEndereco.get(0);

                return address;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void inicializarComponentes() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (MapFragment) getFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseRef = ConfigFirebase.getFirebaseDatabase();

        buttonCancelarEntrega = myView.findViewById(R.id.cancelarEntrega);
        acompanharEntregador = myView.findViewById(R.id.acompanharEntregadorID);
        acompanharPedido = myView.findViewById(R.id.acompanharPedidoID);
        pedidosFeitos = myView.findViewById(R.id.linearLayoutPedidos);

    }


}
