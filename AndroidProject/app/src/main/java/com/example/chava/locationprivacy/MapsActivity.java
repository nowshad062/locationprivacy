package com.example.chava.locationprivacy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import HttpConnection.CustomAlertDialog;
import HttpConnection.ServiceMethodListener;
import HttpConnection.ServiceWithoutParameters;
import android.database.Cursor;
//import com.google.android.gms.drive.internal.StringListResponse;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.MaxLength;
import eu.inmite.android.lib.validations.form.annotations.MaxNumberValue;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;
import com.example.chava.locationprivacy.LocationGenerator;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ServiceMethodListener {

    private GoogleMap mMap=null;
    private DBHelper myDB;
    double curLat, curLon, resLat1, resLon1;
    String searchTerm;
    int flag;
    String[] list1;
    String placeName, resLat, resLon, username, encoded;
    Marker aliasLocation = null;
    CustomAlertDialog alertDialog;
    LinearLayout layout, numberLayout;
    LocationGenerator locationGenerator = new LocationGenerator();
    //@MaxLength(value = 30, order = 1, messageId = R.string.validation_max_number1)
    @NotEmpty(messageId = R.string.validation_not_empty , order = 1)
    @MaxNumberValue(value = "30", order = 2, messageId = R.string.validation_max_number1)
    EditText edRadius, edNumber, edRadiusNumber;
    Button submit, numberSearch;
    int i = 0;
    double lat3, lon3;
    String locationValues="";
    List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
    List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
    List<Marker> markerArrayList= new ArrayList<Marker>();
    Context mapsContext = this;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        layout = (LinearLayout)findViewById(R.id.radiusLayout);
        numberLayout = (LinearLayout)findViewById(R.id.numberLayout);
        edRadius = (EditText)findViewById(R.id.radius);
        //edRadiusNumber = (EditText)findViewById(R.id.edNumberRadius);
        edNumber = (EditText)findViewById(R.id.edNumber);
        submit = (Button)findViewById(R.id.radiusSearch);
        numberSearch = (Button)findViewById(R.id.numberSearch);
        curLat = getIntent().getDoubleExtra("lat", 0.0);
        curLon = getIntent().getDoubleExtra("lon", 0.0);
        flag = getIntent().getIntExtra("flag", 0);
        username = getIntent().getStringExtra("username");
        searchTerm = getIntent().getStringExtra("searchTerm");
        alertDialog = new CustomAlertDialog(MapsActivity.this);
        myDB = new DBHelper(this);

        Log.d("oncreate", curLat + "    " + curLon);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        mMap = googleMap;
        LatLng currentLocation = new LatLng(curLat, curLon);

        Log.d("onmapready", curLat + "    " + curLon);

        final Marker currentMarker = mMap.addMarker(new MarkerOptions().position((currentLocation)).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        currentMarker.showInfoWindow();


        switch (flag) {
            case 1:
                //alertDialog.showOkDialog("selected no Anonymization");
                String url1 = getResources().getString(R.string.base_url) + "singleLocation.php?" + "username=" + username + "&lattitude=" + curLat + "&longitude=" + curLon + "&searchTerm=" + searchTerm + "";
                String url12 = getResources().getString(R.string.base_url1) + "process.php?" + "username=" + username + "&lattitude=" + curLat + "&longitude=" + curLon + "&searchTerm=" + searchTerm +"&option=" + "no" +"&slat=" + curLat +"&slon=" + curLon +"&radius=" + 0 + "";
                ServiceWithoutParameters postmethods1 = new ServiceWithoutParameters(MapsActivity.this, url12, "Case2class", "Case2method");
                ServiceWithoutParameters postmethods = new ServiceWithoutParameters(MapsActivity.this, url1, "Case1class", "Case1method");
                postmethods1.execute();
                postmethods.execute();

                break;
            case 2:
                alertDialog.showOkDialog("Please select an alias location to be sent to google server.");
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng point) {
                        if (i == 0) {
                            // TODO Auto-generated method stub
                            lat3 = point.latitude;
                            lon3 = point.longitude;
                            aliasLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(lat3, lon3)).title("Selected alias location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            aliasLocation.showInfoWindow();
                            String url1 = getResources().getString(R.string.base_url) + "singleLocation.php?" + "username=" + username + "&lattitude=" + lat3 + "&longitude=" + lon3 + "&searchTerm=" + searchTerm + "";
                            Log.d("url1",url1 );
                            String url12 = getResources().getString(R.string.base_url1) + "process.php?" + "username=" + username + "&lattitude=" + curLat + "&longitude=" + curLon + "&searchTerm=" + searchTerm +"&option=" + "partial" +"&slat=" + lat3 +"&slon=" + lon3 +"&radius=" + 0 + "";
                            ServiceWithoutParameters postmethods1 = new ServiceWithoutParameters(MapsActivity.this, url12, "Case2class", "Case2method");
                            ServiceWithoutParameters postmethods = new ServiceWithoutParameters(MapsActivity.this, url1, "Case1class", "Case1method");
                            postmethods1.execute();
                            postmethods.execute();
                            i++;
                        }
                    }
                });
                break;
            case 3:
                alertDialog.showOkDialog("Please select an alias location and radius to be sent to the google server");
                layout.setVisibility(View.VISIBLE);
                //edNumber.setVisibility(View.GONE);

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng point) {
                        if (i == 0) {
                            // TODO Auto-generated method stub
                            lat3 = point.latitude;
                            lon3 = point.longitude;
                            aliasLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(lat3, lon3)).title("Selected alias location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            aliasLocation.showInfoWindow();
                            i++;
                        }
                    }
                });
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lat3 != 0 && lon3 != 0) {
                            Double r = Double.parseDouble(edRadius.getText().toString());
                            if (r < 0) {
                                alertDialog.showOkDialog("Please enter radius value greater than 0");
                            } else if (r > 30) {
                                alertDialog.showOkDialog("please enter radius value less than 30");
                            } else {


                                r = r * 1609.34;
                                final String radius = r.toString();
                                String url2 = getResources().getString(R.string.base_url) + "multiplelocations.php?" + "username=" + username + "&lattitude=" + lat3 + "&longitude=" + lon3 + "&radius=" + radius + "&searchTerm=" + searchTerm + "";
                                Log.d("url2", url2);
                                String url12 = getResources().getString(R.string.base_url1) + "process.php?" + "username=" + username + "&lattitude=" + curLat + "&longitude=" + curLon + "&searchTerm=" + searchTerm + "&option=" + "complete" + "&slat=" + lat3 + "&slon=" + lon3 + "&radius=" + radius + "";
                                ServiceWithoutParameters postmethods1 = new ServiceWithoutParameters(MapsActivity.this, url12, "Case2class", "Case2method");
                                ServiceWithoutParameters postmethod = new ServiceWithoutParameters(MapsActivity.this, url2, "Case3class", "Case3method");
                                postmethod.execute();
                                postmethods1.execute();
                                layout.setVisibility(View.GONE);
                            }
                        } else {
                            alertDialog.showOkDialog("Please select an alias location.");
                        }
                    }
                });
                break;
            case 4:
                alertDialog.showOkDialog("Please enter radius and fake locations count to proceed.");
                numberLayout.setVisibility(View.VISIBLE);
                numberSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Random random = new Random();
                        Log.d("Latti & Long", String.valueOf(curLat)+","+ String.valueOf(curLon));
                        Double r = 30.0;//Double.parseDouble(edRadiusNumber.getText().toString());
                        int num = Integer.parseInt(edNumber.getText().toString());
                        if (num < 0) {
                            alertDialog.showOkDialog("Please enter number value greater than 0");
                        } else if (num > 100) {
                            alertDialog.showOkDialog("Please enter number value less than 100");
                        } else {


                            locationValues = locationGenerator.generateRandomLoc(curLon,curLat,num,30,mapsContext);
                            String temp = locationValues.substring(0,locationValues.length()-1);
                            Log.d("Locationvalues 3party",temp);
                            String url12 = getResources().getString(R.string.base_url1) + "multipleresponses.php?" + "username=" + username + "&reallat=" + curLat + "&reallon=" + curLon + "&searchTerm=" + searchTerm + "&locations=" + temp + "&radius=" + r*1609 + "";
                            locationValues = locationValues + String.valueOf(curLat) + "," + String.valueOf(curLon)+"";
                            Log.d("Location Values",locationValues);
                            String url2 = getResources().getString(R.string.base_url) + "multipleresponses.php?" + "username=" + username + "&locations=" + locationValues + "&radius=" + r*1609.34 + "&number=" + num + "&searchTerm=" + searchTerm + "";
                            Log.d("Detailed Analysis",url2);
                            Log.d("Detailed Analysis",url12);
                            ServiceWithoutParameters postmethods1 = new ServiceWithoutParameters(MapsActivity.this, url12, "Case2class", "Case2method");
                            ServiceWithoutParameters postmethod = new ServiceWithoutParameters(MapsActivity.this, url2, "Case5class", "Case5method");
                            postmethod.execute();
                            postmethods1.execute();

                            numberLayout.setVisibility(View.GONE);
                        }

                    }
                });
                break;
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom((currentLocation), 10.0f));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override

            public void onInfoWindowClick(final Marker marker) {

                resLat1 = marker.getPosition().latitude;
                resLon1 = marker.getPosition().longitude;
                alertDialogBuilder
                        .setMessage("Do you want to get the Directions from your current location to " + placeName + "?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //String url3 = getResources().getString(R.string.base_url) + "directions1.php?" + "&lat=" + resLat1 + "&lon=" + resLon1 + "&curlat=" + curLat + "&curlon=" + curLon + "";
                                String url3 = "https://maps.googleapis.com/maps/api/directions/json?origin="+curLat+","+curLon+"&destination="+resLat1+","+resLon1+"&key=AIzaSyCFLGY58ZN5ESpLddviG9o_9TMjkLbWiis";
                                ServiceWithoutParameters postmethods1 = new ServiceWithoutParameters(MapsActivity.this, url3, "Case4class", "Case4method");
                                for(int i = 0 ; i < markerArrayList.size(); i++){
                                    markerArrayList.get(i).remove();
                                }
                                Marker destination =  mMap.addMarker(new MarkerOptions().position(new LatLng((resLat1), (resLon1))).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                                destination.showInfoWindow();
                                postmethods1.execute();
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
        });


    }
    @Override
    public void getResponse(String data, String classname, String methodname) {
        if (classname.equalsIgnoreCase("Case1class")) {
            if (methodname.equalsIgnoreCase("Case1method")) {
                try {
                    JSONObject job = new JSONObject(data);
                    String notes = job.getString("record");
                    JSONArray ja = new JSONArray(notes);
                    int len = ja.length();
                    list1 = new String[len];
                    String status = job.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        for (int i = 0; i < len; i++) {
                            JSONObject jb1 = ja.getJSONObject(i);
                            placeName = jb1.getString("name");
                            resLat = jb1.getString("lat");
                            resLon = jb1.getString("lon");
                            Marker currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(resLat), Double.parseDouble(resLon))).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                            currentMarker.showInfoWindow();

                        }
                        LatLngBounds x = new LatLngBounds(new LatLng(curLat,curLon), new LatLng(Double.parseDouble(resLat),Double.parseDouble(resLon)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(curLat, curLon)), 10.0f));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(x.getCenter(), 0));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(x.getCenter(), 0));
                    } else {
                        alertDialog.showOkDialog("Something went wrong, please try again");
                    }
                } catch (Exception e) {

                }

            }
        }
        if (classname.equalsIgnoreCase("Case3class")) {
            if (methodname.equalsIgnoreCase("Case3method")) {
                try {
                    JSONObject job = new JSONObject(data);
                    Log.d("In case5",data);
                    String notes = job.getString("record");
                    Log.d("In case5",notes);
                    JSONArray ja = new JSONArray(notes);
                    int len = ja.length();
                    list1 = new String[len];
                    String status = job.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        for (int i = 0; i < len; i++) {
                            JSONObject jb1 = ja.getJSONObject(i);
                            placeName = jb1.getString("name");
                            resLat = jb1.getString("lat");
                            resLon = jb1.getString("lon");
                            Marker currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(resLat), Double.parseDouble(resLon))).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                            currentMarker.showInfoWindow();
                            markerArrayList.add(currentMarker);


                        }
                        //if(len < 1){
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(resLat),Double.parseDouble(resLon)), 8));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(lat3, lon3)), 10.0f));
                        //}
                        //else {
                        //LatLngBounds x = new LatLngBounds(new LatLng(curLat, curLon), new LatLng(Double.parseDouble(resLat), Double.parseDouble(resLon)));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(x.getCenter(), 0));
                        //}
                    } else {
                        alertDialog.showOkDialog("Something went wrong, please try again");
                    }
                } catch (Exception e) {

                }

            }
        }
        if (classname.equalsIgnoreCase("Case5class")) {
            if (methodname.equalsIgnoreCase("Case5method")) {
                try {
                    Log.d("In case5 case, methodif","entered if");
                    JSONObject job = new JSONObject(data);
                    Log.d("In case5",data);
                    String notes = job.getString("record");
                    Log.d("In case5",notes);
                    JSONArray ja = new JSONArray(notes);
                    Log.d("In case5",ja.toString());
                    int len = ja.length();
                    // list1 = new String[len];
                    String status = job.getString("status");
                    if (status.equalsIgnoreCase("200")) {
                        Log.d("In case5 status if","entered if");
                        for (int i = 0; i < len; i++) {
                            Log.d("In case5 for", "Entered for");
                            JSONObject jb1 = ja.getJSONObject(i);
                            if(jb1.getString("reqlat").equals(String.valueOf(curLat)) && jb1.getString("reqlon").equals(String.valueOf(curLon))) {
                                Log.d("In case5 location if", "Entered if");
                                placeName = jb1.getString("name");
                                resLat = jb1.getString("lat");
                                resLon = jb1.getString("lon");
                                Marker currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(resLat), Double.parseDouble(resLon))).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                                currentMarker.showInfoWindow();
                                //markerArrayList.add(currentMarker);
                            }

                        }
                        LatLngBounds x = new LatLngBounds(new LatLng(curLat,curLon), new LatLng(Double.parseDouble(resLat),Double.parseDouble(resLon)));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(x.getCenter(), 0));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(curLat, curLon)), 10.0f));

                    } else {
                        alertDialog.showOkDialog("Something went wrong, please try again");
                    }
                } catch (Exception e) {

                }

            }
        }
        if (classname.equalsIgnoreCase("Case4class")) {
            if (methodname.equalsIgnoreCase("Case4method")) {
                try {


                    JSONObject job = new JSONObject(data);
                    String status = job.getString("status");
                    if (status.equalsIgnoreCase("ok")) {
                        JSONArray jsonArray1 = job.optJSONArray("routes");
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                        JSONArray jsonArray2 = jsonObject1.optJSONArray("legs");
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(0);
                        JSONArray jsonArray3 = jsonObject2.optJSONArray("steps");
                        for (int z = 0; z <= jsonArray3.length(); z++) {
                            JSONObject jsonObject4 = jsonArray3.getJSONObject(z);
                            JSONObject jsonObject5 = jsonObject4.getJSONObject("polyline");
                            encoded = jsonObject5.getString("points");
                            List<LatLng> list = decodePoly(encoded);
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat",
                                        Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng",
                                        Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                            routes.add(path);
                            ArrayList<LatLng> points = null;
                            PolylineOptions polyLineOptions = null;

                            // traversing through routes
                            for (int i1 = 0; i1 < routes.size(); i1++) {
                                points = new ArrayList<LatLng>();
                                polyLineOptions = new PolylineOptions();
                                List<HashMap<String, String>> path = routes.get(i1);

                                for (int j = 0; j < path.size(); j++) {
                                    HashMap<String, String> point = path.get(j);

                                    double lat = Double.parseDouble(point.get("lat"));
                                    double lng = Double.parseDouble(point.get("lng"));
                                    LatLng position = new LatLng(lat, lng);

                                    points.add(position);
                                }

                                polyLineOptions.addAll(points);
                                polyLineOptions.width(5);
                                polyLineOptions.color(Color.BLUE);
                            }

                            mMap.addPolyline(polyLineOptions);

                        }
                    }
                    //JSONArray jsonArray2 =
                                    /*JSONArray ja = new JSONArray(notes);
                                    int len = ja.length();
                                    list1 = new String[len];

                                    if (status.equalsIgnoreCase("200")) {
                                        for (int i = 0; i < len; i++) {
                                            JSONObject jb1 = ja.getJSONObject(i);
                                            encoded = jb1.getString("encoded");
                                            List<LatLng> list = decodePoly(encoded);
                                            for (int l = 0; l < list.size(); l++) {
                                                HashMap<String, String> hm = new HashMap<String, String>();
                                                hm.put("lat",
                                                        Double.toString(((LatLng) list.get(l)).latitude));
                                                hm.put("lng",
                                                        Double.toString(((LatLng) list.get(l)).longitude));
                                                path.add(hm);
                                            }
                                            routes.add(path);
                                            ArrayList<LatLng> points = null;
                                            PolylineOptions polyLineOptions = null;

                                            // traversing through routes
                                            for (int i1 = 0; i1 < routes.size(); i1++) {
                                                points = new ArrayList<LatLng>();
                                                polyLineOptions = new PolylineOptions();
                                                List<HashMap<String, String>> path = routes.get(i1);

                                                for (int j = 0; j < path.size(); j++) {
                                                    HashMap<String, String> point = path.get(j);

                                                    double lat = Double.parseDouble(point.get("lat"));
                                                    double lng = Double.parseDouble(point.get("lng"));
                                                    LatLng position = new LatLng(lat, lng);

                                                    points.add(position);
                                                }

                                                polyLineOptions.addAll(points);
                                                polyLineOptions.width(5);
                                                polyLineOptions.color(Color.BLUE);
                                            }

                                            mMap.addPolyline(polyLineOptions);
                                            /*originLat = jb1.getString("slat");
                                            originLon = jb1.getString("slon");
                                            destinationLat = jb1.getString("elat");
                                            destinationLon = jb1.getString("elon");
                                            Polyline polyline = mMap.addPolyline(new PolylineOptions()
                                                    .add(new LatLng(Double.parseDouble(originLat), Double.parseDouble(originLon)),new LatLng(Double.parseDouble(destinationLat), Double.parseDouble(destinationLon)))
                                                    .width(5)
                                                    .color(Color.BLUE));
                                            //Marker currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(resLat), Double.parseDouble(resLon))).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                                            //currentMarker.showInfoWindow();

                                        }
                                    } */
                    else {
                        alertDialog.showOkDialog("Something went wrong, please try again");
                    }
                } catch (Exception e) {

                }

            }
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public boolean cond(int i, double curLat, double curLon, double foundLat, double foundLon){
        boolean condResult = true;
        switch (i){
            case 0:
                condResult = curLon < foundLon;
                break;
            case 1:
                condResult = curLon > foundLon;
                break;
            case 2:
                condResult = curLat > foundLat;
                break;
            case 3:
                condResult = curLat < foundLat;
                break;
            case 4:
                condResult = false;
                break;
        }
        return condResult;
    }

    public boolean getLocationsFrom(double curLat, double curLon, Double num) {
        boolean locationsFromDB = true;
        Cursor rs = myDB.getData(curLat, curLon, num);
        if (rs.getCount() <= 0){
            System.out.println(rs.getCount());
            locationsFromDB = false;
            System.out.println("if data not found");
        }
        else{
            rs.moveToFirst();
            locationValues = rs.getString(rs.getColumnIndex(DBHelper.Locations_COLUMN_results));
            locationsFromDB = true;
            System.out.println("if data found");
        }
        return locationsFromDB;
    }
}
