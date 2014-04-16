package com.example.bluetoothapp;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class MapActivity extends FragmentActivity {
	GoogleMap mMap;
	double lat;
	double lng;
	Marker marker;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 Bundle extras = getIntent().getExtras();
	        if (extras != null)
	            {
	        	lat  = extras.getDouble("lat");
	        	lng  = extras.getDouble("lng");
	            }
		if (servicesOK()) {
			setContentView(R.layout.activity_map);

			if (initMap()) {
				Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT).show();
				if (extras != null)
				gotoLocation(lat, lng, 15);
			} else {
				Toast.makeText(this, "Map not available!", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			setContentView(R.layout.activity_main);
		}

	}

	private void gotoLocation(double lat, double lng, float zoom) {
		LatLng ll = new LatLng(lat, lng);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
		mMap.moveCamera(update);
	}

	public void geoLocate(View v) throws IOException {
		hideSoftKeyboard(v);//hide keyboard after enter input
		
		EditText et = (EditText) findViewById(R.id.editText1);
		String location = et.getText().toString();
		
		Geocoder gc = new Geocoder(this);
		if(Geocoder.isPresent()){
		List<Address> list = gc.getFromLocationName(location, 1);
		Address add = list.get(0);
		String locality = add.getLocality();
		
		
		double lat = add.getLatitude();
		double lng = add.getLongitude();
		
		gotoLocation(lat, lng, 15);
		if (marker != null) {
			marker.remove();
		}

		MarkerOptions options = new MarkerOptions()
			.title(locality)
			.position(new LatLng(lat, lng));
		marker = mMap.addMarker(options);
	
		}

	}

	private void hideSoftKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	public boolean servicesOK() {
		int isAvailable = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		if (isAvailable == ConnectionResult.SUCCESS) {
			return true;
		} else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable,
					this, 9001);
			dialog.show();
		} else {
			Toast.makeText(this, "Can't connect to Google Play services",
					Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	private boolean initMap() {
		if (mMap == null) {
			SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
			mMap = mapFrag.getMap();
		}
if(mMap!=null){
	mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
		
		@Override
		public void onMapLongClick(LatLng ll) {
			// TODO Auto-generated method stub
			Geocoder gc = new Geocoder(MapActivity.this);
			List<Address> list = null;
			
			try {
				list = gc.getFromLocation(ll.latitude, ll.longitude, 1);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			Address add = list.get(0);
			MapActivity.this.setMarker(add.getLocality(), add.getCountryName(), 
					ll.latitude, ll.longitude);
			
			
		
		}
	});
	mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
		
		private AlertDialog show;

		@Override
		public boolean onMarkerClick(final Marker marker) {
			show = new AlertDialog.Builder(MapActivity.this)
            .setTitle("Location")
            .setMessage("Check za7ma here?")
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	Intent i = new Intent();
            	i.putExtra("lat", marker.getPosition().latitude);
            	i.putExtra("lng", marker.getPosition().longitude);
            	 setResult(0, i);
//            	Intent small = new Intent(MapActivity.this,UpdatesActivity.class);
//            	small.putExtra("lat", marker.getPosition().latitude);
//		    	small.putExtra("lng", marker.getPosition().longitude);
//            	startActivity(small);
                }
             })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { 
                	String msg = marker.getTitle() + " (" + marker.getPosition().latitude + 
        					"," + marker.getPosition().longitude + ")";
        			Toast.makeText(MapActivity.this, msg, Toast.LENGTH_LONG).show();
        			
                }
             }).show();
return false;
		}
	});
}
		return (mMap != null);
	}
	public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.mapTypeNone:
			mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
			break;
		case R.id.mapTypeNormal:
			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			break;
		case R.id.mapTypeSatellite:
			mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.mapTypeTerrain:
			mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			break;
		case R.id.mapTypeHybrid:
			mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}
private void setMarker(String locality, String country, double lat, double lng) {
		
		if (marker != null) {
			marker.remove();
		}

		MarkerOptions options = new MarkerOptions()	
			.title(locality)
			.position(new LatLng(lat, lng)).draggable(true);
		if (country.length() > 0) {
			options.snippet(country);
		}
		
		marker = mMap.addMarker(options);
		
	}
}
