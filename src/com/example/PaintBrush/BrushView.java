package com.example.PaintBrush;

import java.util.ArrayList;

import com.example.PaintBrush.PaintManager.PaintState;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


public class BrushView extends View{
	
	//滑动的layout块
	//public LinearLayout ll;
	
	public int view_width = 0;
	public int view_height = 0;
	private float preX;
	private float preY;
	private float x1, y1, x2, y2;
	private Path path;
	public MyPaint paint;
	Bitmap cacheBitmap = null;
	Canvas cacheCanvas = null;
	
	Bitmap newBitmap = null;
	Canvas newCanvas = null;
	public static PaintManager paintManager;
	
	//填充
	private int[] pixels;
	private int[][] point;
	private int head , tail;

	//双指缩放手势
	ScaleGestureDetector mGestureDetector;
	
	public BrushView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//获取屏幕的高度和宽度
		view_height = context.getResources().getDisplayMetrics().heightPixels;
		view_width = context.getResources().getDisplayMetrics().widthPixels;
		
		paintManager = new PaintManager(view_width, view_height);
		
		Init();
		mGestureDetector = new ScaleGestureDetector(context, new MyGestureDetector());
	}
	
	protected void Init(){
		//cacheBitmap = Bitmap.createBitmap(view_width, view_height, Config.ARGB_8888);
		cacheBitmap = paintManager.getCacheBitmap();
		
		//设置白色的bitmap
		int[] p= new int[view_width * view_height];
		int white = Color.WHITE;
		for(int i = 0 ; i < view_width * view_height ; i ++)
			p[i] = white;
		cacheBitmap.setPixels(p, 0, view_width, 0, 0, view_width, view_height);
		
		cacheCanvas = new Canvas();
		newCanvas = new Canvas();
		path = new Path();

		cacheCanvas.setBitmap(cacheBitmap);
		paint = MyPaint.Instance(this.getContext());
		//设置画笔风格
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);		
		paint.setAntiAlias(true);
		paint.setDither(true);
		
	}
	
	public PaintManager getPaintManager(){
		return paintManager;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float strokewidth = paint.getStrokeWidth();
		int color = paint.getColor();
		
		if(paint.entity == MyPaint.Entity.ERASER){
			paint.setColor(getContext().getResources().getColor(R.color.white));
			paint.setStrokeWidth(30);
		}
		
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(cacheBitmap, 0, 0, paint);		//把它画到画板上
		canvas.drawPath(path, paint);						//实时显示绘制的路径
		canvas.save(Canvas.ALL_SAVE_FLAG);
		//
		canvas.restore();
		paint.setStrokeWidth(strokewidth);
		paint.setColor(color);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//滑出菜单的变量
		boolean slide = false;
		//获取坐标
		float x = event.getX();
		float y = event.getY();
		//Toast.makeText(getContext(), x+" :: "+y, Toast.LENGTH_SHORT).show();
		//从右侧滑出菜单,可检测宽度为20px
		if(x > view_width - 15){
			
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				slide = true;
				break;
			}
			//Toast.makeText(getContext(), x+" 滑出 "+y, Toast.LENGTH_SHORT).show();
		}
		if(paintManager.getState() == PaintState.EDIT){
			
			// 缩放
			mGestureDetector.onTouchEvent(event);
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				preX = x;
				preY = y;

				//paintManager.scaleEntry(1.1f);
				//paintManager.rotateEntry(10f);
				break;
	
			case MotionEvent.ACTION_MOVE:
				float dx = x - preX;
				float dy = y - preY;
				paintManager.moveEntry(dx, dy, false);
				break;
			case MotionEvent.ACTION_UP:
				dx = x - preX;
				dy = y - preY;
				paintManager.moveEntry(dx, dy, true);
				break;
			}
			invalidate();
			return true;
		}
		if(slide){
			//ll.scrollTo(500, 100);
			//ll.setAlpha((float) 8.0);
			//Toast.makeText(getContext(), x+" 滑出 "+y, Toast.LENGTH_SHORT).show();
		}
		//画笔
		else if(paint.entity == MyPaint.Entity.BRUSH ){
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				path.moveTo(x, y);
				preX = x;
				preY = y;
				x1 = x;
				y1 = y;
				break;
	
			case MotionEvent.ACTION_MOVE:
				float dx = Math.abs(x - preX);
				float dy = Math.abs(y - preY);
				if (dx >= 5 || dy >= 5) {
					path.quadTo(preX, preY, (x+preX)/2, (y+preY)/2);
					preX = x;
					preY = y;
				}
				break;
			case MotionEvent.ACTION_UP:
				//cacheCanvas.drawPath(path, paint);
				x2 = x;
				y2 = y;
				paintManager.addBrush(path, paint.getColor(), paint.getStrokeWidth(), x1, y1, x2, y2);
				cacheBitmap = paintManager.getCacheBitmap();
				
				path = new Path();
				break;
			}
			invalidate();
			return true;
		}
		//直线
		else if(paint.entity == MyPaint.Entity.LINE){
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				preX = x;
				preY = y;
				path.moveTo(preX, preY);
				break;
	
			case MotionEvent.ACTION_MOVE:
				float dx = Math.abs(x - preX);
				float dy = Math.abs(y - preY);
				if (dx >= 5 || dy >= 5) {
					path.reset();
					path.moveTo(preX, preY);
					path.lineTo(x, y);
					x1 = preX;
					y1 = preY;
					x2 = x;
					y2= y;
				}
				break;
			case MotionEvent.ACTION_UP:
				//cacheCanvas.drawPath(path, paint);
			
				paintManager.addLine(paint.getColor(), paint.getStrokeWidth(), x1, y1, x2, y2);
				cacheBitmap = paintManager.getCacheBitmap();
				
				path.reset();
				break;
			}
			invalidate();
			return true;
		}
		//折线
		else if(paint.entity == MyPaint.Entity.BROKEN){
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if(Math.abs(x - paint.preX) <= MyPaint.range && Math.abs(y - paint.preY) <= MyPaint.range){
					x = paint.preX;
					y = paint.preY;
				}
				else{
					paint.preX = -MyPaint.range;
					paint.preY = -MyPaint.range;
					paint.startX = x;
					paint.startY = y;
				}
				paint.preX = x;
				paint.preY = y;
				path.moveTo(paint.preX, paint.preY);
				break;
	
			case MotionEvent.ACTION_MOVE:
				float dx = Math.abs(x - paint.preX);
				float dy = Math.abs(y - paint.preY);
				if (dx >= 5 || dy >= 5) {
					path.reset();
					path.moveTo(paint.preX, paint.preY);
					//回到原点
					if(Math.abs(x - paint.startX) <= MyPaint.range && Math.abs(y - paint.startY) <= MyPaint.range){
						x = paint.startX;
						y = paint.startY;
					}
					path.lineTo(x, y);
					x1 = paint.preX;
					y1 = paint.preY;
					x2 = x;
					y2= y;
				}
				break;
			case MotionEvent.ACTION_UP:
				//cacheCanvas.drawPath(path, paint);
				paint.preX = x;
				paint.preY = y;
			
				paintManager.addLine(paint.getColor(), paint.getStrokeWidth(), x1, y1, x2, y2);
				cacheBitmap = paintManager.getCacheBitmap();
				
				path.reset();
				break;
			}
			invalidate();
			return true;
		}
		//矩形
		else if(paint.entity == MyPaint.Entity.RECT){
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				preX = x;
				preY = y;
				break;
	
			case MotionEvent.ACTION_MOVE:
				float dx = Math.abs(x - preX);
				float dy = Math.abs(y - preY);
				if (dx >= 5 || dy >= 5) {
					path.reset();
					//float x1, y1, x2, y2;
					if(preX > x){
						x1 = x;
						x2 = preX;
					}
					else{
						x1 = preX;
						x2 = x;
					}
					if(preY > y){
						y1 = y;
						y2 = preY;
					}
					else{
						y1 = preY;
						y2 = y;
					}
					path.addRect(x1, y1, x2, y2, Path.Direction.CW);
					//this.x1 = x1;
					//this.y1 = y1;
					//this.x2 = x2;
					//this.y2 = y2;
				}
				break;
			case MotionEvent.ACTION_UP:
				//cacheCanvas.drawPath(path, paint);
				
				paintManager.addRect(paint.getColor(), paint.getStrokeWidth(), x1, y1, x2, y2);
				cacheBitmap = paintManager.getCacheBitmap();
				
				path.reset();
				break;
			}
			invalidate();
			return true;
		}
		//椭圆
		else if(paint.entity == MyPaint.Entity.OVAL){
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				preX = x;
				preY = y;
				break;
	
			case MotionEvent.ACTION_MOVE:
				float dx = Math.abs(x - preX);
				float dy = Math.abs(y - preY);
				if (dx >= 5 || dy >= 5) {
					path.reset();
					float x1, y1, x2, y2;
					if(preX > x){
						x1 = x;
						x2 = preX;
					}
					else{
						x1 = preX;
						x2 = x;
					}
					if(preY > y){
						y1 = y;
						y2 = preY;
					}
					else{
						y1 = preY;
						y2 = y;
					}
//					setBrush();
					RectF rectf = new RectF(x1,y1,x2,y2);
					path.addOval(rectf, Path.Direction.CW);
					//path.addRect(x1, y1, x2, y2, Path.Direction.CW);
					this.x1 = x1;
					this.y1 = y1;
					this.x2 = x2;
					this.y2 = y2;
				}
				break;
			case MotionEvent.ACTION_UP:
				
				//cacheCanvas.drawPath(path, paint);
				
				paintManager.addOval(paint.getColor(), paint.getStrokeWidth(), x1, y1, x2, y2);
				cacheBitmap = paintManager.getCacheBitmap();
				
				path.reset();
				break;
			}
			invalidate();
			return true;
		}
		//圆形
		else if(paint.entity == MyPaint.Entity.CIRCLE){
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				preX = x;
				preY = y;
				break;
	
			case MotionEvent.ACTION_MOVE:
				float dx = Math.abs(x - preX);
				float dy = Math.abs(y - preY);
				if (dx >= 5 || dy >= 5) {
					path.reset();
					float x1, y1, x2, y2;
					if(preX > x){
						x1 = x;
						x2 = preX;
					}
					else{
						x1 = preX;
						x2 = x;
					}
					if(preY > y){
						y1 = y;
						y2 = preY;
					}
					else{
						y1 = preY;
						y2 = y;
					}
//					setBrush();
					float r = (float) (Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1))/2);
					path.addCircle((x1+x2)/2, (y1+y2)/2, r, Path.Direction.CW);
					//path.addRect(x1, y1, x2, y2, Path.Direction.CW);
					this.x1 = (x1+x2)/2;
					this.y1 = (y1+y2)/2;
					this.x2 = r;
				}
				break;
			case MotionEvent.ACTION_UP:
				//cacheCanvas.drawPath(path, paint);
				
				paintManager.addCircle(paint.getColor(), paint.getStrokeWidth(), x1, y1, x2);
				cacheBitmap = paintManager.getCacheBitmap();
				
				path.reset();
				break;
			}
			invalidate();
			return true;
		}		
		//橡皮擦
		else if(paint.entity == MyPaint.Entity.ERASER){
			float strokewidth = paint.getStrokeWidth();
			int color = paint.getColor();
			paint.setColor(getContext().getResources().getColor(R.color.white));
			paint.setStrokeWidth(30);
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				path.moveTo(x, y);
				preX = x;
				preY = y;
				break;
	
			case MotionEvent.ACTION_MOVE:
				float dx = Math.abs(x - preX);
				float dy = Math.abs(y - preY);
				if (dx >= 5 || dy >= 5) {
					path.quadTo(preX, preY, (x+preX)/2, (y+preY)/2);
					preX = x;
					preY = y;
				}
				break;
			case MotionEvent.ACTION_UP:
				//cacheCanvas.drawPath(path, paint);
				paintManager.addEraser(path, paint.getColor(), paint.getStrokeWidth(), x1, y1, x2, y2);
				cacheBitmap = paintManager.getCacheBitmap();
				                                                                                                                                                    
				path = new Path();
				break;
			}
			
			invalidate();
			paint.setStrokeWidth(strokewidth);
			paint.setColor(color);
			return true;
		}
		//填充
		else if(paint.entity == MyPaint.Entity.FILL){
			//BFS
			pixels = new int[view_width*view_height];
			point = new int[2][view_width*view_height];
			head = 0;
			tail = 0;
			int pixel;
			int thecolor = paint.getColor();
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				preX = x;
				preY = y;
				//获得当前点的pixel值
				pixel = cacheBitmap.getPixel((int)preX, (int)preY);

				//获取像素点
				cacheBitmap.getPixels(pixels, 0, view_width, 0, 0, view_width, view_height);
				
				//第一个点入队列
				point[0][head] = (int) x;
				point[1][head++] = (int) y;
				pixels[(int)(y*view_width + x)] = thecolor;
				cacheBitmap.setPixel((int)x, (int)y, thecolor);
				cacheCanvas.drawPoint(x, y, paint);
				
				//Toast.makeText(getContext(), x+","+y, Toast.LENGTH_LONG).show();
				if(pixel != thecolor)
					fill(thecolor,pixel);
				
				break;
			}
			invalidate();
			return true;
		}
		return false;
	}

	private void fill(int thecolor,int pixel){
		//填充
		ArrayList<Integer> points = new ArrayList<Integer>();
		int x , y , prex , prey;
		while(head != tail){
			x = point[0][tail];
			y = point[1][tail++];
			//Log.d("debug","出队列");
			
			prex = x + 1;prey = y;
			if(prex >= 0 && prex < view_width && prey >= 0 && prey < view_height && pixels[prey*view_width+prex] == pixel){
				point[0][head] = prex;
				point[1][head++] = prey;
				int i = prey*view_width + prex;
				pixels[i] = thecolor;
				// 添加图元点
				points.add(i);
			}
			
			prex = x - 1;prey = y;
			if(prex >= 0 && prex < view_width && prey >= 0 && prey < view_height && pixels[prey*view_width+prex] == pixel){
				point[0][head] = prex;
				point[1][head++] = prey;
				int i = prey*view_width + prex;
				pixels[i] = thecolor;
				
				points.add(i);
			}
			
			prex = x;prey = y - 1;
			if(prex >= 0 && prex < view_width && prey >= 0 && prey < view_height && pixels[prey*view_width+prex] == pixel){
				point[0][head] = prex;
				point[1][head++] = prey;
				int i = prey*view_width + prex;
				pixels[i] = thecolor;
				
				points.add(i);
			}
			
			prex = x;prey = y + 1;
			if(prex >= 0 && prex < view_width && prey >= 0 && prey < view_height && pixels[prey*view_width+prex] == pixel){
				point[0][head] = prex;
				point[1][head++] = prey;
				int i = prey*view_width + prex;
				pixels[i] = thecolor;
				
				points.add(i);
			}
		}
		
		paintManager.addFill(thecolor, points);
		cacheBitmap.setPixels(pixels, 0, view_width, 0, 0, view_width, view_height);
	}
	
	public void undo(){
		if(paintManager.undo()){
			cacheBitmap = paintManager.getCacheBitmap();
			invalidate();
		}
	}
	
	public void redo() {
		if(paintManager.redo()){
			cacheBitmap = paintManager.getCacheBitmap();
			invalidate();
		}
	}
	
//	public void clear(){
//		//设置图形重叠时的处理方式
//		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//		paint.setStrokeWidth(50);
//		paint.setColor(Color.WHITE);
//	}
	
//	public void setBrush(){
//		paint.setXfermode(null);
//		paint.setColor(Color.BLACK);
//	}
//	
//	public void setBrush(int i){
//		paint.setXfermode(null);
//		paint.setStrokeWidth(i);
//		paint.setColor(Color.BLACK);
//	}
	
	public void clearCanvas(){
		Init();
		paintManager.clear();
		
		invalidate();
	}
}
