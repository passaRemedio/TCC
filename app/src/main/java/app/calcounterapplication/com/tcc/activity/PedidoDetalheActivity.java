package app.calcounterapplication.com.tcc.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import app.calcounterapplication.com.tcc.R;

public class PedidoDetalheActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DatabaseReference firebaseRef;
    private LatLng localCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_detalhe);

        inicializarComponentes();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        System.out.println("PORRAAAAA");
        //Recuperar localizacao do usuario
        recuperarLocalizacaoCliente();
        System.out.println("CUUU");
    }

    private void recuperarLocalizacaoCliente() {

        System.out.println("Aqui1!");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        System.out.println("Aqui2!");

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                System.out.println("Aqui3!");
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

    private void inicializarComponentes(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


}
