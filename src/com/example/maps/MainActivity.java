package com.example.maps;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	private double[] trackedPositionsAsDouble;
	private Button startButton;
	private Button stopButton;
	private Button showMapButton;
	private Button saveButton;
	private boolean endOfTraining = false;
	private boolean serviceStarted = false;
	private MyReciver myReciver;
	private boolean positionsOK = false;
	private FileManager routeToFileSaver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	Toast.makeText(this, "On Create", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        showMapButton = (Button) findViewById(R.id.showMapButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        Toast.makeText(this, getExternalFilesDir(null).getAbsolutePath(), Toast.LENGTH_SHORT).show();
        
        startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startService(new Intent(getBaseContext(), GpsService.class));
				serviceStarted = true;
			}
		});
        
        stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(serviceStarted) {
					stopService(new Intent(getBaseContext(), GpsService.class));
					serviceStarted = false;
					endOfTraining = true;
				}
			}
		});
        
        showMapButton.setOnClickListener(new OnClickListener() {

			
			@Override
			public void onClick(View v) {
				if(endOfTraining && positionsOK) {
					Intent intent = new Intent(getBaseContext(), MapLook.class);
					intent.putExtra("POSITIONS", trackedPositionsAsDouble);
					startActivity(intent);
				}
			}
		});
        
        saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(endOfTraining && positionsOK) {
					routeToFileSaver.saveRoute(trackedPositionsAsDouble);
				}
			}
		});
        routeToFileSaver = new FileManager(getExternalFilesDir(null), getBaseContext());
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	savedInstanceState.putBoolean("SERVICE_STARTED", serviceStarted);
    	savedInstanceState.putBoolean("END_OF_TRAINING", endOfTraining);
    	savedInstanceState.putBoolean("POSITIONS_OK", positionsOK);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    	serviceStarted = savedInstanceState.getBoolean("SERVICE_STARTED");
    	endOfTraining = savedInstanceState.getBoolean("END_OF_TRAINING");
    	positionsOK = savedInstanceState.getBoolean("POSITIONS_OK");
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	myReciver = new MyReciver();
    	IntentFilter intentFilter = new IntentFilter();
    	intentFilter.addAction(GpsService.ACTION);
    	registerReceiver(myReciver, intentFilter);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	unregisterReceiver(myReciver);
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
    
    private void getTrackedPositions(Intent intent) {
    	trackedPositionsAsDouble = intent.getDoubleArrayExtra("POSITIONS");
    	if(trackedPositionsAsDouble != null) {
    		positionsOK = true;
    	}
    }
    
    private class MyReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String data = intent.getStringExtra("GPS_INFO");
			if(data != null) {
				Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
				return;
			}
			getTrackedPositions(intent);
		}
	}
}
