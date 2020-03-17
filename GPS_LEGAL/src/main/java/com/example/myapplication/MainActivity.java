package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int REQUEST_CODE_GPS = 1001;
    private TextView locationTextView;

    private double latitudeAtual;
    private double longitudeAtual;

    private double latitude;
    private double longitude;
    private EditText valorPesquisa;
    private String valorTextPesquisa;
    private Button valorTempo;
    private Button gpsPermissao;
    private boolean gpsStatus = false;
    boolean vInicioPercurso = false;
    boolean atualPercurso = false;
    private Chronometer chronometer;
    private TextView distanciaPercorrida;
    private Location localAnt;
    private double distancia = 0d;
    private TextView btnsearch;
    private TextView btnP;

    private void configurarGPS() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                if (vInicioPercurso) {
                    if (atualPercurso) {
                        localAnt = location;
                        atualPercurso = false;
                    }

                    double distanciaAnt = localAnt.distanceTo(location);
                    distancia += distanciaAnt;

                    String formatoKM = String.format(Locale.getDefault(), "%.2f Km", distancia / 1000d); // transformando em KM.
                    distanciaPercorrida.setText(formatoKM);

                    localAnt = location;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
    }



    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationTextView = findViewById(R.id.locationTextView);

                    btnP=  findViewById(R.id.ValorTempoPassado);
        chronometer=(Chronometer)btnP;
            valorPesquisa = findViewById(R.id.valorPesquisa);
            distanciaPercorrida = findViewById(R.id.distanciaPercorrida);
            btnsearch = findViewById(R.id.btnsearch);
          //  gpsPermissao = findViewById(R.id.gpsPermissao);



            }

        //metodo do btn conceder permissao
        public void mandarPermissaoAndroid(View view){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, getString(R.string.permissao_concedida), Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String [] { Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_GPS
                );
            }
        }

        //metodo do btn parar o gps
        public void pararGps(View view){
                desativarGPS();

        }
        public void desativarGPS(){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsStatus) {

                        locationManager.removeUpdates(locationListener);
                        gpsStatus= false;
                        Toast.makeText(this, getString(R.string.gps_desligado), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, getString(R.string.gps_ja_desativado), Toast.LENGTH_SHORT).show();
                }
            }
        }

        //metodo do btn ativar o gps
        public void ativarGps(View view){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsStatus) {
                    Toast.makeText(this, getString(R.string.msgGpsAtivado), Toast.LENGTH_SHORT).show();

                } else {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,
                            0,
                            locationListener
                    );
                    Toast.makeText(this, getString(R.string.msgGpsAtivado), Toast.LENGTH_SHORT).show();
                    gpsStatus = true;
                }
            } else {
                Toast.makeText(this, getString(R.string.Nao_apertou_em_conceder), Toast.LENGTH_SHORT).show();
            }
        }

        public void iniciarPercurso(View view){
            if (gpsStatus) {

                if (vInicioPercurso) {
                    Toast.makeText(this, getString(R.string.percurso_ja_ativado), Toast.LENGTH_SHORT).show();
                } else {
                        atualPercurso = vInicioPercurso = true;
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();
                        Toast.makeText(this, getString(R.string.percurso_iniciado), Toast.LENGTH_SHORT).show();
                    }
            } else {
                    Toast.makeText(this, getString(R.string.gps_desligado), Toast.LENGTH_SHORT).show();
                }

        }
        public void terminarPercurso(View view){
             if (vInicioPercurso) {
                 chronometer.stop();
                 Toast.makeText(this, chronometer.getText() +" - " + distanciaPercorrida.getText(), Toast.LENGTH_SHORT).show();
                 chronometer.setText("");
                 distanciaPercorrida.setText("0 Km");
                 vInicioPercurso= false;
             } else {
                 Toast.makeText(this, getString(R.string.sem_rota), Toast.LENGTH_SHORT).show();
             }

        }

        public void btnsearch(View view){
            Uri uri =
                    Uri.parse(String.format(Locale.getDefault(), "geo:%f,%f?q="+valorPesquisa.getText(), latitude, longitude));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
            valorPesquisa.setText("");

        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,
                            0,
                            locationListener
                    );
                    Toast.makeText(this, getString(R.string.permissao_concedida), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.no_gps_no_app), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


        @Override protected void onStart() {
            super.onStart();

        }
        @Override protected void onStop() {
            super.onStop();
            desativarGPS();

        }





}

