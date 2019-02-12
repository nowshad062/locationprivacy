package com.example.chava.locationprivacy;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.Random;

import static java.lang.Math.*;

/**
 * Created by chava on 6/3/2017.
 */

/*This class generates random locations and returns them to the MapsActivity class.*/
public class LocationGenerator
{
    private DBHelper myDB;
    String locationValues="";
    Random random = new Random();
    int condChoice = random.nextInt(5);
    double direction;
    double number;

    //This method was called from MapsActivity class.
    //This method checks weather there was a preious record of random locations associated with the requested lat and lng,
    //if the record was available then it checks weather the available record has more fake locations count or less and accordingly it returns the fake locations or generaates new fake locations
    public String generateRandomLoc(double curLon, double curLat, double num, double r, Context mapsContext) {
        myDB = new DBHelper(mapsContext);
        boolean locationsFromDB = false;
        Log.d("Detailed analysis","finding data available in db or not");
        //Method to find weather there was a previous record of fake locations.
        locationsFromDB = getLocationsFrom(curLat, curLon, num);
        r = r * 1609.34;
        //Condition if there was no previous record
        if(!locationsFromDB) {
            //Method to generate fake locations
            locationValues = findRandomLocations(curLat, curLon, num, r );
            Log.d("Detailed Analysis","Going to save location values in sqllite db");
            //calling insertLocations in DBHelper class to insert the generated fake locations into db for further use
            myDB.insertLocations(Math.round(curLat * 10000.0) / 10000.0,Math.round(curLon * 10000.0) / 10000.0,num,locationValues, condChoice ,false);
            Cursor rs = myDB.getData(Math.round(curLat * 10000.0) / 10000.0,Math.round(curLon * 10000.0) / 10000.0, num);
            rs.moveToFirst();
            number = Double.parseDouble(rs.getString(rs.getColumnIndex(DBHelper.Locations_COLUMN_num)));
            Log.d("Detailed Analysis","Recent number value from !locationsFromDB: "+ number);
            Log.d("Detailed analysis","Location Values "+locationValues);
        }
        else{
            locationValues = locationValues+"";
            Log.d("Detailed Analysis","Going to save updated location values in sqllite db");
            double num1;
            String values =  locationValues;
            String valuesArray[] = values.split(":");
            num1 = valuesArray.length;
            myDB.insertLocations(Math.round(curLat * 10000.0) / 10000.0,Math.round(curLon * 10000.0) / 10000.0,num1,locationValues, condChoice, false);
            Log.d("Detailed analysis","Location Values "+locationValues);
        }
        //Condition to check if the requested fake locations count was greater than the previous request
        if(num > number){
            Log.d("Detailed Analysis: ","num: "+num +" number: "+ number);
            Log.d("Detailed Analysis","Number of fake locations requested this time were higher than previous request");
            Log.d("Detailed Analysis","Generating new random locations");
            String temp = locationValues;
            locationValues = "";
            //generating new fake locations for the extra count
            temp = temp + findRandomLocations(curLat, curLon, num - number, r);
            Log.d("Detailed Analysis","Going to save updated location values in sqllite db after adding new random locations");
            //updating the previous record with new fake locations count
            myDB.insertLocations(Math.round(curLat * 10000.0) / 10000.0,Math.round(curLon * 10000.0) / 10000.0,num,temp, condChoice, true);
            locationValues = temp;
        }
        //Condition to check if the requested fake locations count was less than the previous request
        if(num < number){
            Log.d("Detailed Analysis: ","num: "+num +" number: "+ number);
            Log.d("Detailed Analysis","Number of fake locations requested this time were lesser than previous request");
            String values =  locationValues;
            String valuesArray[] = values.split(":");
            String temp = "";
            //trimmig the fake locations count to the requested number
            for(int i = 0; i < num; i++) {
                String pointsArray[] = valuesArray[i].split(",");
                temp = temp + Double.parseDouble(pointsArray[0]) + "," + Double.parseDouble(pointsArray[1]) + ":" + "" ;
            }
            locationValues = temp;
            Log.d("Detailed Analysis","Location values when reduced fake locations count to "+num + ": "+ locationValues);
        }
        Log.d("Detailed Analysis: ", "number before returning locations: "+ number);
        return locationValues;
    }

    //This method is to check weather the locations were being generated according to the selected direction
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
    //This method finds weather there was a previous record of fake locations,
    // here if data found we have to check weather the user is in the same location or moved.
    // If the user has moved a certain distance then all the fake locations has to be moved in the same direction and same distance
    public boolean getLocationsFrom(double curLat, double curLon, Double num) {
        boolean locationsFromDB = true;
        String previousCurLat, previousCurLon;
        Log.d("Detailed analysis","getting data from db, calling getdata function");
        Cursor rs1 = myDB.getData(curLat, curLon, num);
        //In this condition if data not found locationsFromDB is set to false and then returned
        if (rs1.getCount() <= 0){
            System.out.println(rs1.getCount());
            locationsFromDB = false;
            Log.d("Detailed analysis","Data not found");
        }
        //In this condition if data is found need to do the further action of finding the user has moved or not and calculating the direction and distance if user has moved
        else {
            Log.d("Detailed analysis", "data found");
            Double[] latArray = new Double[rs1.getCount()];
            Double[] lonArray = new Double[rs1.getCount()];
            int z = 0;
            double distanceBtwLocations = 0;
            double finalLat = 0.0;
            double finalLon = 0.0;
            while (rs1.moveToNext()) {
                latArray[z] = Double.parseDouble(rs1.getString(rs1.getColumnIndex(DBHelper.Locations_COLUMN_lat)));
                lonArray[z] = Double.parseDouble(rs1.getString(rs1.getColumnIndex(DBHelper.Locations_COLUMN_lon)));
                Log.d("Detailed Analysis", latArray[z] +", " + lonArray[z] );
                double tempDistance = findDistanceBetweenPoints(latArray[z], lonArray[z], Math.round(curLat * 10000.0) / 10000.0, Math.round(curLon * 10000.0) / 10000.0);
                System.out.println(tempDistance + " : tempDistance");
                if (distanceBtwLocations < tempDistance) {
                    finalLat = latArray[z];
                    finalLon = lonArray[z];
                    distanceBtwLocations = tempDistance;
                    System.out.println(distanceBtwLocations + " : distanceBtwLocations");
                }
                z++;
            }
            if (distanceBtwLocations < 100 * 1606.34) {
                Log.d("Detailed Analysis", "Distance is not greater than 100 miles");
                previousCurLat = String.valueOf(finalLat);//rs.getString(rs.getColumnIndex(DBHelper.Locations_COLUMN_lat));
                previousCurLon = String.valueOf(finalLon);//rs.getString(rs.getColumnIndex(DBHelper.Locations_COLUMN_lon));
                Cursor rs = myDB.getData(finalLat, finalLon, num);
                rs.moveToFirst();
//                condChoice = Integer.parseInt(rs.getString(rs.getColumnIndex(DBHelper.Locations_COLUMN_condChoice)));
                number = Double.parseDouble(rs.getString(rs.getColumnIndex(DBHelper.Locations_COLUMN_num)));
                Log.d("Detailed Analysis", "number while data found: " + number);
                Log.d("Detailed Analysis", "Recent number value from getLocationsFrom: " + number);
                Log.d("Detailed analysis", "Lattittude and Longitude points" + " " + Double.parseDouble(previousCurLat) + "," + Double.parseDouble(previousCurLon) + ":" + Math.round(curLat * 10000.0) / 10000.0 + "," + Math.round(curLon * 10000.0) / 10000.0);
                //if user has not moved then locationsFromDB is set to true and then returned
                if (Double.parseDouble(previousCurLat) == Math.round(curLat * 10000.0) / 10000.0 && Double.parseDouble(previousCurLon) == Math.round(curLon * 10000.0) / 10000.0) {
                    Log.d("Detailed analysis", "lattitudes and longitudes are same");
                    rs.moveToFirst();
                    locationValues = rs.getString(rs.getColumnIndex(DBHelper.Locations_COLUMN_results));
                    locationsFromDB = true;
                } else {
                    Log.d("Detailed analysis", "lattitudes and longitudes are not same");
                    float distance = 0;
                    LatLng movedPoints;
                    Log.d("Detailed analysis", "finding distance between lattitudes and longitudes");
                    distance = findDistanceBetweenPoints(Double.parseDouble(previousCurLat), Double.parseDouble(previousCurLon), curLat, curLon);
                    Log.d("Detailed Analysis", "distance in meters" + " " + distance);
                    direction = SphericalUtil.computeHeading(new LatLng(Double.parseDouble(previousCurLat), Double.parseDouble(previousCurLon)), new LatLng(curLat, curLon));
                    Log.d("Detailed Analysis", "direction in degrees" + " " + direction);
                    String values = rs.getString(rs.getColumnIndex(DBHelper.Locations_COLUMN_results));
                    String valuesArray[] = values.split(":");
                    Log.d("Detailed analysis", "finding random locations after moving a bit");
                    for (int i = 0; i < valuesArray.length; i++) {
                        String pointsArray[] = valuesArray[i].split(",");
                        Log.d("Detailed analysis", "lattitudes and longitudes points" + pointsArray[0] + " " + pointsArray[1]);
                        movedPoints = findMovedPoints(Double.parseDouble(pointsArray[0]), Double.parseDouble(pointsArray[1]), distance, direction);
                        Log.d("Detailed Analysis", "latitude and longitude points after moving" + " " + movedPoints);
                        locationValues = findRandomLocations(movedPoints.latitude, movedPoints.longitude, 1, distance + 1);
                    }
                }
            }
            else{
                locationValues = findRandomLocations(curLat, curLon, num, 30);
            }
            locationsFromDB = true;
        }
        return locationsFromDB;
    }

    public String findRandomLocations(double curLat, double curLon, double num, double r){
        double foundLatitude;
        double foundLongitude;
        double x0 = curLon;
        double y0 = curLat;
        double radiusInDegrees = r / 111000f;
        for (int i = 0; i < num; ) {
            do {
                double u = random.nextDouble();
                Log.d("random calculator", ":"+u);
                double v1 = random.nextDouble();
                Log.d("random calculator", ":"+v1);
                double w = radiusInDegrees * Math.sqrt(u);
                Log.d("random calculator", ":"+w);
                double t = 2 * Math.PI * v1;
                Log.d("random calculator", ":"+t);
                double x = w * cos(t);
                Log.d("random calculator", ":"+x);
                double y = w * Math.sin(t);
                Log.d("random calculator", ":"+y);
                foundLongitude = x + x0;
                Log.d("random calculator", ":"+foundLongitude);
                foundLatitude = y + y0;
                Log.d("random calculator", ":"+foundLatitude);
            }
            while (cond(condChoice, curLat, curLon, foundLatitude, foundLongitude));
            if(onLand(foundLatitude, foundLongitude)){
                System.out.println(foundLatitude + " , " + foundLongitude);
                locationValues = locationValues + String.valueOf(foundLatitude) + "," + String.valueOf(foundLongitude) + ":"+"";
                i++;
            }
        }

        return locationValues;
    }

    public float findDistanceBetweenPoints(double previousCurLat, double preiousCurLon, double curLat, double curLon){
        float distance = 0;
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(curLat-previousCurLat);
        double dLng = Math.toRadians(curLon-preiousCurLon);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                cos(Math.toRadians(previousCurLat)) * cos(Math.toRadians(curLat)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        distance = (float) (earthRadius * c);
        return  distance;
    }

    public LatLng findMovedPoints(double lat, double lon, double distance, double direction){
        double dx, dy, delta_latitude, delta_longitude, final_lat, final_lon;
        dx = distance * sin(direction);
        Log.d("Detailed Analysis","dx value"+" "+dx);
        dy = distance * cos(direction);
        Log.d("Detailed Analysis","dy value"+" "+dy);
        delta_longitude = dx/(111320 * sin(lat));
        Log.d("Detailed Analysis","delta longitute value"+" "+delta_longitude);
        delta_latitude = dy/110540;
        Log.d("Detailed Analysis","delta latitude value"+" "+delta_latitude);
        final_lat = delta_latitude + lat;
        final_lon = delta_longitude + lon;
        LatLng latLng = new LatLng(final_lat, final_lon);
        return latLng;
    }



















































    public boolean onLand(double lat, double lng){
        boolean isLocOnLand = true;
        if(  (lat < 44.741525 && lng > -67.073412) ||
                (lat < 44.293846 && lng > -68.153266) ||
                (lat < 43.772551 && lng > -69.658393) ||
                (lat < 43.581853 && lng > -70.196723) ||
                (lat < 43.198639 && lng > -70.614203) ||
                (lat < 41.281247 && lng > -72.417143) ||
                (lat < 40.380116 && lng > -73.972052) ||
                (lat < 39.010737 && lng > -74.916876) ||
                (lat < 37.247912 && lng > -75.927619) ||
                (lat < 34.940079 && lng > -76.432990) ||
                (lat < 34.542857 && lng > -77.377814) ||
                (lat < 33.961681 && lng > -78.058966) ||
                (lat < 33.662735 && lng > -78.924140) ||
                (lat < 33.162942 && lng > -79.231757) ||
                (lat < 32.666932 && lng > -79.868621) ||
                (lat < 29.295781 && lng > -94.370573 && lat < 29.295781 && lng < -83.428191 && lat > 28.333194 && lng > -94.370573 && lat > 28.333294 && lng < -83.428191) ||
                (lat < 27.731958 && lng > -97.073210 && lat < 27.731958 && lng < -82.812957 && lat > 25.849131 && lng > -97.073210 && lat > 25.849131 && lng < -82.812957) ||
                (lat < 25.849131) ||
                (lat < 31.268970 && lng < -105.884250) ||
                (lat < 28.911819 && lng < -100.654758) ||
                (lat < 48.392589 && lng < -124.912571) ||
                (lat < 38.910367 && lng < -123.644200) ||
                (lat < 38.263976 && lng < -122.956791) ||
                (lat < 37.509751 && lng < -122.471825) ||
                (lat < 36.599105 && lng < -121.915541) ||
                (lat < 35.702289 && lng < -121.264115) ||
                (lat < 34.568544 && lng < -120.570920) ||
                (lat < 34.146885 && lng < -119.148046) ||
                (lat < 33.492815 && lng < -117.727319) ||
                (lat < 32.545841 && lng < -117.181154) ||
                (lat > 49.369609) ||
                (lat > 44.691568 && lng > -66.820257)  ){
            isLocOnLand = false;
        }
        return isLocOnLand;
    }

}