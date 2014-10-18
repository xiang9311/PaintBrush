package com.example.PaintBrush;

import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

public class MyGestureDetector implements OnScaleGestureListener{

	@Override
	public boolean onScale(ScaleGestureDetector arg0) {
		// TODO Auto-generated method stub
		float scale = arg0.getScaleFactor();
		BrushView.paintManager.scaleEntry(scale, false);
		MainActivity.bv.invalidate();
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector arg0) {
		// TODO Auto-generated method stub
		float scale = arg0.getScaleFactor();
		BrushView.paintManager.scaleEntry(scale, true);
		MainActivity.bv.invalidate();
	}
	
}
