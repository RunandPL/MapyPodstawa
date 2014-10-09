package com.example.maps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;

import android.content.Context;
import android.hardware.Camera.Size;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;


public class FileManager {
	private final String FILE_NAME = "SavedTracks";
	private final String TAG = "RouteToFileSaver";
	private File directory;
	private Context activityContext;
	
	public FileManager(File directory, Context activityContext) {
		this.directory = directory;
		this.activityContext = activityContext;
	}
	
	public void saveRoute(double[] track) {
		File file = new File(directory.getAbsolutePath() + "\\" + FILE_NAME);
		if(!file.exists())
			try {
				file.createNewFile();
			} catch(IOException e) {
				Toast.makeText(activityContext, "Nie uda³o siê utworzyæ pliku", Toast.LENGTH_LONG).show();
				return;
			}
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file, true);
		} catch (FileNotFoundException e) {
			Toast.makeText(activityContext, "Nie znaleziono pliku do zapisu", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(fileOutputStream != null) {
			try {
				fileOutputStream.write(getRouteAsByteTable(track));
				fileOutputStream.flush();
			} catch(IOException e) {
				Toast.makeText(activityContext, "B³¹d podczas zapisu", Toast.LENGTH_SHORT).show();
				Log.e(TAG, e.getMessage());
			} finally {
				try {
					fileOutputStream.close();
				} catch(IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
	}
	
	private void readFromFile() {
		File file = new File(directory.getAbsolutePath() + "\\" + FILE_NAME);
		if(!file.exists())
			return;
		
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch(IOException e) {
			Toast.makeText(activityContext, "B³¹d podczas odczytu", Toast.LENGTH_SHORT).show();
			Log.e(TAG, e.getMessage());
		}
		
		if(fileInputStream != null) {
		}
	}
	
	private byte[] getRouteAsByteTable(double[] track) {
		//Zak³adam ¿e double ma 8 bajtów rozmiaru
		int byteTableSize = track.length * 8 + 4;
		ByteBuffer byteBuffer = ByteBuffer.allocate(byteTableSize);
		//Najpierw idzie iloœæ punktów na trasie
		byteBuffer.putInt(track.length);
		//Teraz wszystkie punkty  z trasy, mam nadziejê ¿e nie ma ich jakoœ bardzo du¿o
		for(int i = 0; i < track.length; i++) {
			byteBuffer.putDouble(track[i]);
		}
		return byteBuffer.array();
	}
}
