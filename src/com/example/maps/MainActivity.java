package com.example.maps;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	private GoogleMap map = null;
	private final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private LocationManager locationManager = null;
	private Location currentBestLocation = null;
	private Marker marker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        prepareMap();
        LocationListener locationListener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				markLocationOnMap(location, map);
			}
		};
		locationManager.requestLocationUpdates(LOCATION_PROVIDER, 0, 0, locationListener);
        
        if(map != null) {
        	drawLine();
        }
    }
    
    private void drawLine() {
    	//Rysowanie lini
    	LatLng[] listaPunktow = new LatLng[2];
    	listaPunktow[0] = new LatLng(54.115, 20.126);
    	listaPunktow[1] = new LatLng(54.1151, 20.126);
    	map.addPolyline(new PolylineOptions().add(listaPunktow).color(Color.RED));
    }
    
    private void prepareMap() {
    	map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
    	map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    	//Ustawiam ostatni¹ znan¹ pozycjê na mapie
    	Location location = locationManager.getLastKnownLocation(LOCATION_PROVIDER);
    	if(location != null) {
    		markLocationOnMap(location, map);
    		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
    		marker = map.addMarker(new MarkerOptions().position(latLng).title("Ja"));
    		currentBestLocation = location;
    	}
    }
    
    private void markLocationOnMap(Location location, GoogleMap map) {
    	if(map != null && isBetterLocation(location)) {
    		//Ususwam stary marker z mapy
    		if(marker != null)	{	
    			marker.remove();
    			marker = null;
    		}
    		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
    		marker = map.addMarker(new MarkerOptions().position(latLng).title("Ja"));
    		currentBestLocation = location;
    	}
    }
    
    private boolean isBetterLocation(Location location) {
    	if(currentBestLocation == null)
    		return true;
    	//Sprawdzam czy lokalizacja jest nowsza czy starsza
    	long timeDelta = location.getTime() - currentBestLocation.getTime();
    	boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
    	boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
    	boolean isNewer = timeDelta > 0;
    	
    	if(isSignificantlyNewer) 
    		return true;
    	else if(isSignificantlyOlder)
    		return false;
    	
    	//Sprawdzam czy lokalizacja jest dok³adniejsza
    	int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
    	boolean isLessAccurate = accuracyDelta > 0;
    	boolean isMoreAccurate = accuracyDelta < 0;
    	boolean isSignificantlyLessAcurate = accuracyDelta > 200;
    	
    	if(isMoreAccurate)
    		return true;
    	else if (isNewer && !isLessAccurate)
    		return true;
    	return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
