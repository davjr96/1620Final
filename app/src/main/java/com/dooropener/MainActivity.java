package com.dooropener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends Activity implements  GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    AmazonDynamoDB ddb = null;
    DynamoDBMapper mapper = null;
    public ArrayList<Door> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


      CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
              this, // get the context for the current activity
              Constants.ACCOUNT_ID, // your AWS Account id
              Constants.IDENTITY_POOL_ID, // your identity pool id
              Constants.UNAUTH_ROLE_ARN,// an authenticated role ARN
              Constants.AUTH_ROLE_ARN, // an unauthenticated role ARN
              Regions.US_EAST_1 //Region
      );
      ddb = new AmazonDynamoDBClient(credentialsProvider);
      mapper = new DynamoDBMapper(ddb);

      setUpMapIfNeeded();


  }
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                map();
            }
        }
    }
    public void map() {
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 20));
        }

        new loadMarkers().execute();
        mMap.setOnMarkerClickListener(this);
    }
    private void loadMarkers() {
        for (int x = 0; x < list.size(); x++) {

                mMap.addMarker(new MarkerOptions().position(new LatLng(list.get(x).getLatitude(), list.get(x).getLongitude())).title(list.get(x).getName()));

        }
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        String Name = "";
        int pin=24;
        Intent intent = new Intent(this, Display.class);
        for (int x = 0; x < list.size(); x++) {
            if (marker.getTitle().equals(list.get(x).getName())) {
                Name = list.get(x).getName();
                pin = list.get(x).getPin();
            }
        }
        intent.putExtra("NAME", Name);
        intent.putExtra("PIN", pin);

        startActivity(intent);
        return true;
    }
    private class loadMarkers extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<Door> result = mapper.scan(Door.class, scanExpression);
            list.addAll(result);
            return 0;        }

        protected void onPostExecute(Integer result) {
            loadMarkers();
        }

    }

    @DynamoDBTable(tableName = "Doors")
    public static class Door {
        private String name;
        private double latitude;
        private double longitude;
        private int pin;

        @DynamoDBHashKey(attributeName = "Name")
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        @DynamoDBAttribute(attributeName = "latitude")
        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        @DynamoDBAttribute(attributeName = "pin")
        public int getPin() {
            return pin;
        }

        public void setPin(int pin) {
            this.pin = pin;
        }

        @DynamoDBAttribute(attributeName = "longitude")
        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

    }
}
