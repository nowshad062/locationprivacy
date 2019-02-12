package com.example.chava.locationprivacy;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;


public class SearchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    @NotEmpty(messageId = R.string.validation_not_empty, order = 1)
    EditText edSearch;
    Button searchButton;
    RadioButton type1, type2, type3, type4;
    RadioGroup radioGroup;
    Double lat;
    Double lon;
    Boolean isValid;
    GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    String lat1, lon1, searchTerm, username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchButton = (Button)findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(SearchActivity.this, "Hello", Toast.LENGTH_LONG).show();
                //Toast.makeText(SearchActivity.this, "Hello", Toast.LENGTH_LONG).show();

                isValid = FormValidator.validate(SearchActivity.this, new SimpleErrorPopupCallback(getApplicationContext(), true));
                if (isValid) {

                    int selectedID = radioGroup.getCheckedRadioButtonId();
                    if (selectedID == type1.getId()) {
                        Intent intent = new Intent(SearchActivity.this, MapsActivity.class);
                        intent.putExtra("flag", 1);
                        sendIntentData(intent);
                    } else if (selectedID == type2.getId()) {
                        Intent intent = new Intent(SearchActivity.this, MapsActivity.class);
                        intent.putExtra("flag", 2);
                        sendIntentData(intent);
                    } else if (selectedID == type3.getId()){
                        Intent intent = new Intent(SearchActivity.this, MapsActivity.class);
                        intent.putExtra("flag", 3);
                        sendIntentData(intent);
                    } else if (selectedID == type4.getId()){
                        Intent intent = new Intent(SearchActivity.this, MapsActivity.class);
                        intent.putExtra("flag", 4);
                        sendIntentData(intent);
                    }
                    else{
                        //searchButton.setEnabled(false);
                        Toast.makeText(SearchActivity.this, "Please select any option",Toast.LENGTH_SHORT).show();
                        //
                        // searchButton.setEnabled(true);
                    }
                }

            }
        });


        buildGoogleApiClient();
       edSearch = (EditText)findViewById(R.id.search);
        radioGroup = (RadioGroup)findViewById(R.id.group);
        type1 = (RadioButton)findViewById(R.id.radio1);
        type2 = (RadioButton)findViewById(R.id.radio2);
        type3 = (RadioButton)findViewById(R.id.radio3);
        type4 = (RadioButton)findViewById(R.id.radio4);

        username = getIntent().getStringExtra("username");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
            lat1 = Double.toString(lat);
            lon1 = Double.toString(lon);
        }


        /*searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(SearchActivity.this, "Hello", Toast.LENGTH_LONG).show();

                isValid = FormValidator.validate(SearchActivity.this, new SimpleErrorPopupCallback(getApplicationContext(), true));
                if (isValid) {

                    int selectedID = radioGroup.getCheckedRadioButtonId();
                    if (selectedID == type1.getId()) {
                        Intent intent = new Intent(SearchActivity.this, MapsActivity.class);
                        intent.putExtra("flag", 1);
                        sendIntentData(intent);
                    } else if (selectedID == type2.getId()) {
                        Intent intent = new Intent(SearchActivity.this, MapsActivity.class);
                        intent.putExtra("flag", 2);
                        sendIntentData(intent);
                    } else if (selectedID == type3.getId()){
                        Intent intent = new Intent(SearchActivity.this, MapsActivity.class);
                        intent.putExtra("flag", 3);
                        sendIntentData(intent);
                    } else if (selectedID == type4.getId()){
                        Intent intent = new Intent(SearchActivity.this, MapsActivity.class);
                        intent.putExtra("flag", 4);
                        sendIntentData(intent);
                    }
                    else{
                        //searchButton.setEnabled(false);
                        Toast.makeText(SearchActivity.this, "Please select any option",Toast.LENGTH_SHORT).show();
                        //
                        // searchButton.setEnabled(true);
                    }
                }
            }
        });*/
    }

    public void sendIntentData(Intent intent){


        double curLat = getIntent().getDoubleExtra("lat", 95.9899);
        double curLon = getIntent().getDoubleExtra("lon", 30.0939);
        //double curLat = 30.09194;
        //double curLon = -95.98944;
        String uname  = getIntent().getStringExtra("username");

        searchTerm = edSearch.getText().toString();
        searchTerm = searchTerm.replace(" ","");
        intent.putExtra("searchTerm", searchTerm);
        intent.putExtra("lat", curLat);
        intent.putExtra("lon", curLon);
        intent.putExtra("username", uname);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        moveTaskToBack(true);
                        SearchActivity.this.finish();

                    }


                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat=mLastLocation.getLatitude();
            lon=mLastLocation.getLongitude();
            lat1 = Double.toString(lat);
            lon1=Double.toString(lon);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
