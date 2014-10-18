package com.example.PaintBrush;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

public class SaveManager {
	
	private String filename;
	
	@SuppressLint("SdCardPath")
	public SaveManager(){

	}
	
	public void saveBitmap(Bitmap bitmap) throws IOException{
		filename = String.valueOf(System.currentTimeMillis());
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/mypaintbrush/");
		if(!file.exists())
			file.mkdirs();
		file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/mypaintbrush/" + filename + ".png");
		if(!file.exists())
			file.createNewFile();
		FileOutputStream FOS = new FileOutputStream(file);
		if(bitmap == null){
			Log.d("baocun", "’‚¿Ô");
		}
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, FOS);
		FOS.flush();
		FOS.close();
	}
}
