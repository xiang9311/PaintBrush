package com.example.PaintBrush;

import java.util.ArrayList;

import org.apache.commons.logging.Log;

import com.example.PaintBrush.EntryOpera.Opera;
import com.example.PaintBrush.MyPaint.Entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.opengl.Visibility;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PaintManager {

	private int bitmapWidth, bitmapHeight;          
    private int operaIndex;                     //操作指针
    private Boolean uodoflag;
	private int activeEntryId;                  //当前图元位置
    private Entry activeEntry;                  //当前被选择的图元
    private int entryCount;
	
    // 图元列表 和 操作列表
	private ArrayList<Entry> entryList = new ArrayList<Entry>();
    private ArrayList<EntryOpera> entryOperaArrayList = new ArrayList<EntryOpera>();
	private Bitmap cacheBitmap;					//所有的图元都绘制在这上面
	private Canvas cacheCanvas;
	private Paint paint;

	
	private LinearLayout ll_list;
	private ArrayList<View> tempLl_list = new ArrayList<View>();
	private LinearLayout ll_action;
	private Context context;

	
	private Bitmap selectBitmap;				//选择的图元
	private Bitmap otherBitmap;					//除了被选择之外的图元
	
	private PaintState state;					//当前绘图状态
	
	// 绘图的状态，绘图、编辑
	public enum PaintState{
		DRAW, EDIT
	}


	public PaintManager(int bitmapWidth, int bitmapHeight) {
		this.bitmapWidth = bitmapWidth;
		this.bitmapHeight = bitmapHeight;
		cacheBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
		cacheCanvas = new Canvas();
		cacheCanvas.setBitmap(cacheBitmap);
		paint = new Paint();
		
		activeEntryId = -1;
		operaIndex = -1;
		entryCount = 0;
		uodoflag = false;
		
		//设置画笔风格
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);		
		paint.setAntiAlias(true);
		paint.setDither(true);
		
		state = PaintState.DRAW;
	}
	
	
	public ArrayList<Entry> getEntryList(){
		return entryList;
	}
	
	public Bitmap getCacheBitmap() {
		return cacheBitmap;
	}

	public PaintState getState() {
		return state;
	}

	public void setState(PaintState state) {
		this.state = state;
	}
	
	// 更新cacheBitmap
    public void updateCacheBitmap(){
        activeEntryId = -1;
//        cacheBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
//        cacheCanvas.setBitmap(cacheBitmap);
        cacheCanvas.drawColor(Color.WHITE);

        for(int i=0; i<entryList.size(); i++){
            Path path = new Path();
            path.reset();
            Entry entry = entryList.get(i);
            paint.setColor(entry.color);
            paint.setStrokeWidth(entry.stroke);
            
            if(entry.rotate != 0){
            	drawRotate(entry);
            	continue;
            }
            
            if(entry.getType() == MyPaint.Entity.BRUSH || entry.getType() == MyPaint.Entity.ERASER){
            	cacheCanvas.drawPath(entry.path, paint);
            }
            else if(entry.getType() == MyPaint.Entity.LINE){
            	path.moveTo(entry.x1, entry.y1);
            	path.lineTo(entry.x2, entry.y2);
            	cacheCanvas.drawPath(path, paint);
            }
            else if(entry.getType() == MyPaint.Entity.RECT){
            	path.addRect(entry.x1, entry.y1, entry.x2, entry.y2, Direction.CW);
            	cacheCanvas.drawPath(path, paint);
            }
            else if(entry.getType() == MyPaint.Entity.CIRCLE){
            	path.addCircle(entry.x1, entry.y1, entry.x2, Direction.CW);
            	cacheCanvas.drawPath(path, paint);
            }
            else if(entry.getType() == MyPaint.Entity.OVAL){
            	RectF oval = new RectF(entry.x1, entry.y1, entry.x2, entry.y2);
            	path.addOval(oval, Direction.CW);
            	cacheCanvas.drawPath(path, paint);
            }
            else if(entry.getType() == MyPaint.Entity.FILL){
            	int[] pixels = new int[bitmapWidth*bitmapHeight];
            	cacheBitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
            	int color = entry.color;
            	for(int j=0; j<entry.points.size(); j++){
            		pixels[entry.points.get(j).intValue()] = color;
            	}
            	cacheBitmap.setPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
            }
        }
    }
    // 把所选图元画到selectBitmap上, 并画上被选上的标志
    private Bitmap drawSelectEntry(Entry entry){
    	Path path = new Path();
        paint.setColor(entry.color);
        paint.setStrokeWidth(entry.stroke);
        selectBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(selectBitmap);
        
        // 画出所选图元
    	if(entry.getType() == MyPaint.Entity.BRUSH){
    		canvas.drawPath(entry.path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.LINE){
        	path.moveTo(entry.x1, entry.y1);
        	path.lineTo(entry.x2, entry.y2);
        	canvas.drawPath(path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.RECT){
        	path.addRect(entry.x1, entry.y1, entry.x2, entry.y2, Direction.CW);
        	canvas.drawPath(path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.CIRCLE){
        	path.addCircle(entry.x1, entry.y1, entry.x2, Direction.CW);
        	canvas.drawPath(path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.OVAL){
        	RectF oval = new RectF(entry.x1, entry.y1, entry.x2, entry.y2);
        	path.addOval(oval, Direction.CW);
        	canvas.drawPath(path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.FILL){
        	int[] pixels = new int[bitmapWidth*bitmapHeight];
        	selectBitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
        	int color = entry.color;
        	for(int j=0; j<entry.points.size(); j++){
        		pixels[entry.points.get(j).intValue()] = color;
        	}
        	selectBitmap.setPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
        }
    	
    	state = PaintState.EDIT;
    	// 画被选中的标志
    	Paint selectPaint = new Paint();
    	selectPaint.setColor(Color.BLUE);
    	if(entry.getType() == MyPaint.Entity.LINE || entry.getType() == MyPaint.Entity.BRUSH){
    		canvas.drawCircle(entry.x1, entry.y1, 5, selectPaint);
        	canvas.drawCircle(entry.x2, entry.y2, 5, selectPaint);
    	}
    	else if(entry.getType() == MyPaint.Entity.CIRCLE){
    		canvas.drawCircle(entry.x1, entry.y1, 5, selectPaint);
    		canvas.drawCircle(entry.x1-entry.x2, entry.y1-entry.x2, 5, selectPaint);
    		canvas.drawCircle(entry.x1-entry.x2, entry.y1+entry.x2, 5, selectPaint);
    		canvas.drawCircle(entry.x1+entry.x2, entry.y1-entry.x2, 5, selectPaint);
    		canvas.drawCircle(entry.x1+entry.x2, entry.y1+entry.x2, 5, selectPaint);
    	}
    	else{
	    	canvas.drawCircle(entry.x1, entry.y1, 5, selectPaint);
	    	canvas.drawCircle(entry.x2, entry.y2, 5, selectPaint);
	    	canvas.drawCircle(entry.x1, entry.y2, 5, selectPaint);
	    	canvas.drawCircle(entry.x2, entry.y1, 5, selectPaint);
    	}
    	
    	if(entry.rotate != 0){
    		Bitmap tempBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
    		Canvas tempCanvas = new Canvas(tempBitmap); 
	    	float px = (entry.x1 + entry.x2)/2;
	    	float py = (entry.y1 + entry.y2)/2;
	    	Matrix m = new Matrix();
	    	m.setRotate(entry.rotate, px, py);
	
	    	tempCanvas.drawBitmap(selectBitmap, m, paint);
	    	selectBitmap = tempBitmap;
    	}
    	
    	
    	return selectBitmap;
    }
    // 绘制新添加的图元
    private void drawAddEntry(Entry entry){
    	Path path = new Path();
        paint.setColor(entry.color);
        paint.setStrokeWidth(entry.stroke);
        
        if(entry.rotate != 0){
        	drawRotate(entry);
        	return;
        }
        
    	if(entry.getType() == MyPaint.Entity.BRUSH || entry.getType() == MyPaint.Entity.ERASER){
    		cacheCanvas.drawPath(entry.path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.LINE){
        	path.moveTo(entry.x1, entry.y1);
        	path.lineTo(entry.x2, entry.y2);
        	cacheCanvas.drawPath(path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.RECT){
        	path.addRect(entry.x1, entry.y1, entry.x2, entry.y2, Direction.CW);
        	cacheCanvas.drawPath(path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.CIRCLE){
        	path.addCircle(entry.x1, entry.y1, entry.x2, Direction.CW);
        	cacheCanvas.drawPath(path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.OVAL){
        	RectF oval = new RectF(entry.x1, entry.y1, entry.x2, entry.y2);
        	path.addOval(oval, Direction.CW);
        	cacheCanvas.drawPath(path, paint);
        }
    }
    
    // 绘制旋转了的图元到cacheBitmap
    private void drawRotate(Entry entry){
    	
    	Path path = new Path();
        paint.setColor(entry.color);
        paint.setStrokeWidth(entry.stroke);
    	
    	Canvas canvas = new Canvas();
    	Bitmap tempBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
    	canvas.setBitmap(tempBitmap);
    	
    	if(entry.getType() == MyPaint.Entity.BRUSH || entry.getType() == MyPaint.Entity.ERASER){
    		canvas.drawPath(entry.path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.LINE){
        	path.moveTo(entry.x1, entry.y1);
        	path.lineTo(entry.x2, entry.y2);
        	canvas.drawPath(path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.RECT){
        	path.addRect(entry.x1, entry.y1, entry.x2, entry.y2, Direction.CW);
        	canvas.drawPath(path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.CIRCLE){
        	path.addCircle(entry.x1, entry.y1, entry.x2, Direction.CW);
        	canvas.drawPath(path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.OVAL){
        	RectF oval = new RectF(entry.x1, entry.y1, entry.x2, entry.y2);
        	path.addOval(oval, Direction.CW);
        	canvas.drawPath(path, paint);
        }
    	
    	float px = (entry.x1 + entry.x2)/2;
    	float py = (entry.y1 + entry.y2)/2;
    	Matrix m = new Matrix();
    	m.setRotate(entry.rotate, px, py);

    	cacheCanvas.drawBitmap(tempBitmap, m, paint);
    	
    }
    
    // 绘制旋转了的图元到cacheBitmap
    private void drawRotateToBitmap(Entry entry, Canvas drawCanvas){
    	
    	Path path = new Path();
        paint.setColor(entry.color);
        paint.setStrokeWidth(entry.stroke);
    	
    	Canvas canvas = new Canvas();
    	Bitmap tempBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
    	canvas.setBitmap(tempBitmap);
    	
    	if(entry.getType() == MyPaint.Entity.BRUSH || entry.getType() == MyPaint.Entity.ERASER){
    		canvas.drawPath(entry.path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.LINE){
        	path.moveTo(entry.x1, entry.y1);
        	path.lineTo(entry.x2, entry.y2);
        	canvas.drawPath(path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.RECT){
        	path.addRect(entry.x1, entry.y1, entry.x2, entry.y2, Direction.CW);
        	canvas.drawPath(path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.CIRCLE){
        	path.addCircle(entry.x1, entry.y1, entry.x2, Direction.CW);
        	canvas.drawPath(path, paint);
        }
        else if(entry.getType() == MyPaint.Entity.OVAL){
        	RectF oval = new RectF(entry.x1, entry.y1, entry.x2, entry.y2);
        	path.addOval(oval, Direction.CW);
        	canvas.drawPath(path, paint);
        }
    	
    	float px = (entry.x1 + entry.x2)/2;
    	float py = (entry.y1 + entry.y2)/2;
    	Matrix m = new Matrix();
    	m.setRotate(entry.rotate, px, py);

    	drawCanvas.drawBitmap(tempBitmap, m, paint);
    	
    }
    
    // 选择图元， 把除了选择的图元画到otherbitmap上
	public Bitmap selectEntry(int entryID){
		state = PaintState.EDIT;
		ll_action.setVisibility(View.VISIBLE);
		
		activeEntryId = entryID;
        for(int i=0; i<entryList.size(); i++){
        	if(entryList.get(i).getId() == activeEntryId){
        		activeEntry = entryList.get(i);
        	}
        }
        
        otherBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas();
		canvas.setBitmap(otherBitmap);
		
		for(int i=0; i<entryList.size(); i++){
			Entry entry = entryList.get(i);
			if(entry.getId() != entryID){

				Path path = new Path();
	            paint.setColor(entry.color);
	            paint.setStrokeWidth(entry.stroke);
	            
	            if(entry.rotate != 0){
	            	drawRotateToBitmap(entry, canvas);
	            	continue;
	            }
	            
	            if(entry.getType() == MyPaint.Entity.BRUSH || entry.getType() == MyPaint.Entity.ERASER){
	            	canvas.drawPath(entry.path, paint);
	            }
	            else if(entry.getType() == MyPaint.Entity.LINE){
	            	path.moveTo(entry.x1, entry.y1);
	            	path.lineTo(entry.x2, entry.y2);
	            	canvas.drawPath(path, paint);
	            }
	            else if(entry.getType() == MyPaint.Entity.RECT){
	            	path.addRect(entry.x1, entry.y1, entry.x2, entry.y2, Direction.CW);
	            	canvas.drawPath(path, paint);
	            }
	            else if(entry.getType() == MyPaint.Entity.CIRCLE){
	            	path.addCircle(entry.x1, entry.y1, entry.x2, Direction.CW);
	            	canvas.drawPath(path, paint);
	            }
	            else if(entry.getType() == MyPaint.Entity.OVAL){
	            	RectF oval = new RectF(entry.x1, entry.y1, entry.x2, entry.y2);
	            	path.addOval(oval, Direction.CW);
	            	canvas.drawPath(path, paint);
	            }
	            else if(entry.getType() == MyPaint.Entity.FILL){
	            	int[] pixels = new int[bitmapWidth*bitmapHeight];
	            	otherBitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
	            	int color = entry.color;
	            	for(int j=0; j<entry.points.size(); j++){
	            		pixels[entry.points.get(j).intValue()] = color;
	            	}
	            	otherBitmap.setPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
	            }
			}
		}
        
		// 绘制到cacheBitmap上
		cacheCanvas.drawColor(Color.WHITE);
        cacheCanvas.drawBitmap(otherBitmap, 0, 0, null);
        cacheCanvas.drawBitmap(drawSelectEntry(activeEntry), 0, 0, null);
        return cacheBitmap;
	}
	// 选择默认图元
	public Bitmap selectDefaultEntry(){
		if(entryList.size()>0){
			for(int i=entryList.size()-1; i>=0; i--){
				if(entryList.get(i).getType() != MyPaint.Entity.ERASER
						&& entryList.get(i).getType() != MyPaint.Entity.FILL){
					Bitmap bitmap = selectEntry(entryList.get(i).getId());
					MainActivity.bv.invalidate();
					return bitmap;
				}
			}
		}
		return null;
	}
	// 取消选择
	public void unSelectEntry(){
		ll_action.setVisibility(View.INVISIBLE);
		cacheCanvas.drawColor(Color.WHITE);
        //cacheCanvas.drawBitmap(otherBitmap, 0, 0, null);
        //drawAddEntry(activeEntry);
		updateCacheBitmap();
		state = PaintState.DRAW;
		MainActivity.bv.invalidate();
	}
	
	// 删除图元
	public void deleteEntry(){
		Toast.makeText(context, "别着急亲，稍后添加！", Toast.LENGTH_SHORT).show();
	}

    public void addLine(int color, float stroke, float preX, float preY, float x, float y){
        if(uodoflag){
        	updateOperaList();
        }
    	Entry entry = new Entry(MyPaint.Entity.LINE, entryCount++);
        entry.color = color;
    	entry.stroke = stroke;
    	entry.x1 = preX;
    	entry.y1 = preY;
    	entry.x2 = x;
    	entry.y2 = y;
    	entryList.add(entry);
    	
    	EntryOpera eo = new EntryOpera(entry.getId(), Opera.ADD, entry);
    	entryOperaArrayList.add(eo);
    	operaIndex = entryOperaArrayList.size()-1;
    	//updateCacheBitmap();
    	drawAddEntry(entry);
    	addImageButton();
    }

    public void addRect(int color, float stroke, float preX, float preY, float x, float y){
    	if(uodoflag){
        	updateOperaList();
        }
    	Entry entry = new Entry(MyPaint.Entity.RECT, entryCount++);
    	entry.color = color;
    	entry.stroke = stroke;
    	entry.x1 = preX;
    	entry.y1 = preY;
    	entry.x2 = x;
    	entry.y2 = y;
    	entryList.add(entry);
    	
    	EntryOpera eo = new EntryOpera(entry.getId(), Opera.ADD, entry);
    	entryOperaArrayList.add(eo);
    	operaIndex = entryOperaArrayList.size()-1;
    	//updateCacheBitmap();
    	drawAddEntry(entry);
    	addImageButton();
    }

    public void addBrush(Path path, int color, float stroke, float x1, float y1, float x2, float y2){
    	if(uodoflag){
        	updateOperaList();
        }
    	Entry entry = new Entry(MyPaint.Entity.BRUSH, entryCount++);
    	entry.path = path;
    	entry.color = color;
    	entry.stroke = stroke;
    	entry.x1 = x1;
    	entry.y1 = y1;
    	entry.x2 = x2;
    	entry.y2 = y2;
    	entryList.add(entry);
    	
    	EntryOpera eo = new EntryOpera(entry.getId(), Opera.ADD, entry);
    	entryOperaArrayList.add(eo);
    	operaIndex = entryOperaArrayList.size()-1;
    	//updateCacheBitmap();
    	drawAddEntry(entry);
    	addImageButton();
    }

    public void addOval(int color, float stroke, float x1, float y1, float x2, float y2){
    	if(uodoflag){
        	updateOperaList();
        }
    	Entry entry = new Entry(MyPaint.Entity.OVAL, entryCount++);
    	entry.color = color;
    	entry.stroke = stroke;
    	entry.x1 = x1;
    	entry.y1 = y1;
    	entry.x2 = x2;
    	entry.y2 = y2;
    	entryList.add(entry);
    	
    	EntryOpera eo = new EntryOpera(entry.getId(), Opera.ADD, entry);
    	entryOperaArrayList.add(eo);
    	operaIndex = entryOperaArrayList.size()-1;
    	//updateCacheBitmap();
    	drawAddEntry(entry);
    	addImageButton();
    }
    
    public void addCircle(int color, float stroke, float x1, float y1, float x2){
    	if(uodoflag){
        	updateOperaList();
        }
    	Entry entry = new Entry(MyPaint.Entity.CIRCLE, entryCount++);
    	entry.color = color;
    	entry.stroke = stroke;
    	entry.x1 = x1;
    	entry.y1 = y1;
    	entry.x2 = x2;
    	entryList.add(entry);
    	
    	EntryOpera eo = new EntryOpera(entry.getId(), Opera.ADD, entry);
    	entryOperaArrayList.add(eo);
    	operaIndex = entryOperaArrayList.size()-1;
    	//updateCacheBitmap();
    	drawAddEntry(entry);
    	addImageButton();
    }
    
    public void addFill(int color, ArrayList<Integer> points) {
    	if(uodoflag){
        	updateOperaList();
        }
    	Entry entry = new Entry(MyPaint.Entity.FILL, entryCount++);
    	entry.color = color;
    	entry.points = points;
    	entryList.add(entry);
    	
    	EntryOpera eo = new EntryOpera(entry.getId(), Opera.ADD, entry);
    	entryOperaArrayList.add(eo);
    	operaIndex = entryOperaArrayList.size()-1;
    	//updateCacheBitmap();
    	drawAddEntry(entry);
	}
    // 橡皮檫
    public void addEraser(Path path, int color, float stroke, float x1, float y1, float x2, float y2){
    	if(uodoflag){
        	updateOperaList();
        }
    	Entry entry = new Entry(MyPaint.Entity.ERASER, entryCount++);
    	entry.path = path;
    	entry.color = color;
    	entry.stroke = stroke;
    	entry.x1 = x1;
    	entry.y1 = y1;
    	entry.x2 = x2;
    	entry.y2 = y2;
    	entryList.add(entry);
    	
    	EntryOpera eo = new EntryOpera(entry.getId(), Opera.ADD, entry);
    	entryOperaArrayList.add(eo);
    	operaIndex = entryOperaArrayList.size()-1;
    	//updateCacheBitmap();
    	drawAddEntry(entry);
    }
    
    //设置ll_list
    public void setLl_list(LinearLayout ll_list, Context context){
    	this.ll_list = ll_list;
    	this.context = context;
    	
    	if(ll_list.getChildCount() == 1){
    		ll_list.getChildAt(0).setTag(-1);
    		System.out.println("----  -1 ----");
    	}
    }
    //设置ll_action
    public void setLl_action(LinearLayout ll_action, Context context){
    	this.ll_action = ll_action;
    	this.context = context;
    }
    
    //生成图元缩略图
    private Bitmap generateThumb(Entry entry){
    	//绘制出path对应的bitmap
		Bitmap littlebitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
		
		//设置白色的bitmap
		int[] px= new int[bitmapWidth * bitmapHeight];
		int white = Color.WHITE;
		for(int i = 0 ; i < bitmapWidth * bitmapHeight ; i ++)
			px[i] = white;
		littlebitmap.setPixels(px, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
		Canvas littlecanvas = new Canvas();
		littlecanvas.setBitmap(littlebitmap);
		Paint p = new Paint();
		//设置画笔风格
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeJoin(Paint.Join.ROUND);
		p.setStrokeCap(Paint.Cap.ROUND);		
		p.setAntiAlias(true);
		p.setDither(true);
		
		p.setColor(entry.color);
		p.setStrokeWidth(entry.stroke * 10);
		littlecanvas.drawPath(entry.path, p);
		
		int newHeight = 82;
		int newWidth = 82;
		
		// 计算缩放因子 
		float heightScale = ((float) newHeight) / bitmapWidth; 
		float widthScale = ((float) newWidth) / bitmapHeight; 
		Matrix matrix = new Matrix(); 
		matrix.postScale(heightScale, widthScale); 
		Bitmap newBitmap = Bitmap.createBitmap(littlebitmap, 0, 0, bitmapWidth,bitmapHeight, matrix, true);
    
		return newBitmap;
    }
    
    //动态添加ImageButton到ll_list
    public void addImageButton(){
//    	ImageButton imagebutton = new ImageButton(getBaseContext());
//    	imagebutton.setImageDrawable(getResources().getDrawable(R.drawable.black));
//    	imagebutton.setBackground(getResources().getDrawable(R.drawable.roundcorner));
//    	//imagebutton.setPadding(7, 8, 7, 8);
//    	ll_list.addView(imagebutton);
    	
    	Entry entry = entryList.get(entryList.size() - 1);
    	//绘制出path对应的bitmap
		Bitmap littlebitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
		//设置白色的bitmap
		Canvas littlecanvas = new Canvas();
		littlecanvas.setBitmap(littlebitmap);
		littlecanvas.drawColor(Color.WHITE);
		
		Paint p = new Paint();
		//设置画笔风格
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeJoin(Paint.Join.ROUND);
		p.setStrokeCap(Paint.Cap.ROUND);		
		p.setAntiAlias(true);
		p.setDither(true);
		p.setColor(entry.color);
		p.setStrokeWidth(entry.stroke * 10);
		
		// 构建不同的图元path
    	if(entry.getType() == MyPaint.Entity.BRUSH){
    		
    		littlecanvas.drawPath(entry.path, p);

    	}
    	else if(entry.getType() == MyPaint.Entity.LINE){
    		
    		Path path = new Path();
    		path.moveTo(entry.x1, entry.y1);
    		path.lineTo(entry.x2, entry.y2);
    		littlecanvas.drawPath(path, p);
    	}
    	else if(entry.getType() == MyPaint.Entity.BROKEN){
    		
    	}
    	else if(entry.getType() == MyPaint.Entity.RECT){
    		
    		Path path = new Path();
    		path.addRect(entry.x1, entry.y1, entry.x2, entry.y2, Direction.CW);
    		littlecanvas.drawPath(path, p);
    		
    	}
    	else if(entry.getType() == MyPaint.Entity.CIRCLE){
    		Path path = new Path();
    		path.addCircle(entry.x1, entry.y1, entry.x2, Direction.CW);
    		littlecanvas.drawPath(path, p);
    	}
    	else if(entry.getType() == MyPaint.Entity.OVAL){
    		Path path = new Path();
    		RectF rectf = new RectF(entry.x1, entry.y1, entry.x2, entry.y2);
			path.addOval(rectf, Path.Direction.CW);
			littlecanvas.drawPath(path, p);
    	}
    	
    	int newHeight = 82;
		int newWidth = 82;
		// 计算缩放因子 
		float heightScale = ((float) newHeight) / bitmapWidth; 
		float widthScale = ((float) newWidth) / bitmapHeight; 
		Matrix matrix = new Matrix(); 
		matrix.postScale(heightScale, widthScale); 
		Bitmap newBitmap = Bitmap.createBitmap(littlebitmap, 0, 0, bitmapWidth,bitmapHeight, matrix, true); 
		
		ImageButton imagebutton = new ImageButton(context);
    	imagebutton.setImageBitmap(newBitmap);
    	imagebutton.setBackground(context.getResources().getDrawable(R.drawable.roundcorner));
    	//imagebutton.setTop(7);
    	imagebutton.setPadding(10, 0, 10, 0);
    	
    	imagebutton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//Toast.makeText(context,  arg0.getTag().toString(), Toast.LENGTH_SHORT).show();
				try{
					Integer i = (Integer)arg0.getTag();
					BrushView.paintManager.selectEntry(i.intValue());
					MainActivity.bv.invalidate();
				}
				catch (Exception e){
					
				}
			}
    		
    	});
    	imagebutton.setTag(entry.getId());
    	ll_list.addView(imagebutton);
    }
    
    private void exchangeEntry(Entry newEntry){
    	for(int i=0; i<entryList.size(); i++){
    		if(newEntry.getId() == entryList.get(i).getId()){
    			entryList.remove(i);
    			entryList.add(i, newEntry);
    			break;
    		}
    	}
    }
    
    // 平移图元
    public void moveEntry(float xOffset, float yOffset, boolean add){
         if(activeEntry != null){
        	 if(activeEntry.getType() == MyPaint.Entity.RECT || activeEntry.getType() == MyPaint.Entity.OVAL){
      		 
        		 Entry entry = activeEntry.copy();
	        	 entry.x1 = activeEntry.x1 + xOffset;
	        	 entry.y1 = activeEntry.y1 + yOffset;
	        	 entry.x2 = activeEntry.x2 + xOffset;
	        	 entry.y2 = activeEntry.y2 + yOffset;
	        	 if(add){
	        		 // 
		        	 exchangeEntry(entry);
	        		 
		        	 EntryOpera eo = new EntryOpera(activeEntryId, Opera.CHANGE, entry);
		        	 entryOperaArrayList.add(eo);
		        	 operaIndex = entryOperaArrayList.size()-1;
	        	 }
	        	 
	        	 // 绘制到cacheBitmap上
	     		 cacheCanvas.drawColor(Color.WHITE);
	             cacheCanvas.drawBitmap(otherBitmap, 0, 0, null);
	             cacheCanvas.drawBitmap(drawSelectEntry(entry), 0, 0, null);
	        	 
        	 }
        	 else if(activeEntry.getType() == MyPaint.Entity.LINE){
        		 Entry entry = activeEntry.copy();
	        	 entry.x1 = activeEntry.x1 + xOffset;
	        	 entry.y1 = activeEntry.y1 + yOffset;
	        	 entry.x2 = activeEntry.x2 + xOffset;
	        	 entry.y2 = activeEntry.y2 + yOffset;
	        	 if(add){
	        		 // 
	        		 exchangeEntry(entry);
		        	 
		        	 EntryOpera eo = new EntryOpera(activeEntryId, Opera.CHANGE, entry);
		        	 entryOperaArrayList.add(eo);
		        	 operaIndex = entryOperaArrayList.size()-1;
	        	 }
	        	 
	        	 // 绘制到cacheBitmap上
	     		 cacheCanvas.drawColor(Color.WHITE);
	             cacheCanvas.drawBitmap(otherBitmap, 0, 0, null);
	             cacheCanvas.drawBitmap(drawSelectEntry(entry), 0, 0, null);
	        	 
        	 }
        	 else if(activeEntry.getType() == MyPaint.Entity.BRUSH){
        		 Canvas canvas = new Canvas();
        	     Bitmap tempBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
        	     canvas.setBitmap(tempBitmap);
        	     Matrix m = new Matrix();
        	     m.postTranslate(xOffset, yOffset);
        	     canvas.drawBitmap(selectBitmap, m, null);
        	     
        	     if(add){
        	    	 Entry entry = new Entry(MyPaint.Entity.BRUSH, activeEntry.getId());
        	    	 activeEntry.x1 = activeEntry.x1 + xOffset;
            	     activeEntry.y1 = activeEntry.y1 + yOffset;
            	     activeEntry.x2 = activeEntry.x2 + xOffset;
            	     activeEntry.y2 = activeEntry.y2 + yOffset;
            	     activeEntry.path.transform(m);
        	    	 entry = activeEntry.copy();
//        	    	 Path path = new Path();
//        	    	 activeEntry.path.transform(m, path);
//        	    	 entry.path = path;
        	    	 EntryOpera eo = new EntryOpera(activeEntryId, Opera.CHANGE, entry);
		        	 entryOperaArrayList.add(eo);
		        	 operaIndex = entryOperaArrayList.size()-1;
        	     }
        	     
        	     // 绘制到cacheBitmap上
    		 	 cacheCanvas.drawColor(Color.WHITE);
    		 	 cacheCanvas.drawBitmap(otherBitmap, 0, 0, null);
    		 	 cacheCanvas.drawBitmap(tempBitmap, 0, 0, null);
        	 }
        	 else if(activeEntry.getType() == MyPaint.Entity.CIRCLE){
        		 Entry entry = activeEntry.copy();
	        	 entry.x1 = activeEntry.x1 + xOffset;
	        	 entry.y1 = activeEntry.y1 + yOffset;
	        	 
	        	 if(add){ 
	        		 exchangeEntry(entry);
		        	 
		        	 EntryOpera eo = new EntryOpera(activeEntryId, Opera.CHANGE, entry);
		        	 entryOperaArrayList.add(eo);
		        	 operaIndex = entryOperaArrayList.size()-1;
	        	 }
	        	 
	        	 // 绘制到cacheBitmap上
	     		 cacheCanvas.drawColor(Color.WHITE);
	             cacheCanvas.drawBitmap(otherBitmap, 0, 0, null);
	             cacheCanvas.drawBitmap(drawSelectEntry(entry), 0, 0, null);
        	 }
        	 
         }
    }
    // 旋转图元
    public void rotateEntry(float angle){
    	Matrix m = new Matrix();
    	activeEntry.rotate += angle;
    	Canvas canvas = new Canvas();
    	Bitmap tempBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
    	canvas.setBitmap(tempBitmap);
    	
    	float px = (activeEntry.x1 + activeEntry.x2)/2;
    	float py = (activeEntry.y1 + activeEntry.y2)/2;
    	//canvas.rotate(angle, px, py);
    	m.setRotate(activeEntry.rotate, px, py);
    	Toast.makeText(context, "rotate", Toast.LENGTH_SHORT).show();
    	canvas.drawBitmap(selectBitmap, m, new Paint());
    	
    	// 绘制到cacheBitmap上
	 	cacheCanvas.drawColor(Color.WHITE);
	 	cacheCanvas.drawBitmap(otherBitmap, 0, 0, null);
	 	cacheCanvas.drawBitmap(tempBitmap, 0, 0, null);
    }
    // 缩放图元
    public void scaleEntry(float multiple, boolean add){
    	if(activeEntry != null){
    		if(activeEntry.getType() == MyPaint.Entity.CIRCLE){
    			activeEntry.x2 *= multiple;
    			
//    			Entry entry = new Entry(activeEntry.getType());
//    			entry = activeEntry.copy();
//    			entry.x2 = activeEntry.x2*multiple;
    			
//    			if(add){
//		        	 activeEntry = entry;
//		        	 EntryOpera eo = new EntryOpera(activeEntryPos, Opera.CHANGE, entry);
//		        	 entryOperaArrayList.add(eo);
//		        	 operaIndex = entryOperaArrayList.size()-1;
//	       	 	}
				
				// 绘制到cacheBitmap上
	 		 	cacheCanvas.drawColor(Color.WHITE);
	 		 	cacheCanvas.drawBitmap(otherBitmap, 0, 0, null);
	 		 	cacheCanvas.drawBitmap(drawSelectEntry(activeEntry), 0, 0, null);
    		}
    		else{
	    		float w = activeEntry.x2 - activeEntry.x1;
	    		float h = activeEntry.y2 - activeEntry.y1;
	    		
	    		float xOffet, yOffset;
	    		xOffet = w*(multiple - 1)/2;
	    		yOffset = h*(multiple - 1)/2;
	
				activeEntry.x1 -= xOffet;
				activeEntry.x2 += xOffet;
				activeEntry.y1 -= yOffset;
				activeEntry.y2 += yOffset;
//				Entry entry = activeEntry.copy();
//				entry.x1 = activeEntry.x1 - xOffet;
//				entry.x2 = activeEntry.x2 + xOffet;
//				entry.y1 = activeEntry.y1 - yOffset;
//				entry.y2 = activeEntry.y2 + yOffset;
//				if(add){
//		        	 activeEntry = entry;
//		        	 EntryOpera eo = new EntryOpera(activeEntryPos, Opera.CHANGE, entry);
//		        	 entryOperaArrayList.add(eo);
//		        	 operaIndex = entryOperaArrayList.size()-1;
//	       	 	}
				
				// 绘制到cacheBitmap上
	 		 	cacheCanvas.drawColor(Color.WHITE);
	 		 	cacheCanvas.drawBitmap(otherBitmap, 0, 0, null);
	 		 	cacheCanvas.drawBitmap(drawSelectEntry(activeEntry), 0, 0, null);
 		 	
    		}
        }
    }

    public void copyEntry(){
    	Entry newEntry = activeEntry.copy();
    	
    	// TODO 等待补充
    	if(newEntry.getType() == MyPaint.Entity.BRUSH)
    		return;
    	if(newEntry.getType() == MyPaint.Entity.CIRCLE){
    		newEntry.x1 += 10;
    		newEntry.y1 += 10;
    	}
    	else{
	    	newEntry.x1 += 10;
	    	newEntry.y1 += 10;
	    	newEntry.x2 += 10;
	    	newEntry.y2 += 10;
    	}
    	newEntry.setId(entryCount++);
    	
    	entryList.add(newEntry);
    	
    	EntryOpera eo = new EntryOpera(newEntry.getId(), Opera.ADD, newEntry);
    	entryOperaArrayList.add(eo);
    	operaIndex = entryOperaArrayList.size()-1;

    	drawAddEntry(newEntry);
    	addImageButton();
    	
    	selectEntry(newEntry.getId());
    }
    
	public boolean undo(){
		if(operaIndex >= 0){
			EntryOpera eo = entryOperaArrayList.get(operaIndex);
			if(eo.opera == Opera.ADD){
				for(int i = entryList.size()-1; i>=0; i--){
					if(entryList.get(i).getId() == eo.id){
						entryList.remove(i);
						break;
					}
				}
				
				for(int i=ll_list.getChildCount()-1; i>=0; i--){
					Integer id = (Integer)ll_list.getChildAt(i).getTag();
					if(id.intValue() == eo.id){
						tempLl_list.add(ll_list.getChildAt(i));
						ll_list.removeViewAt(i);
						break;
					}
				}
			}
			else if(eo.opera == Opera.CHANGE){
				for(int i = entryList.size()-1; i>=0; i--){
					if(entryList.get(i).getId() == eo.id){
						entryList.remove(i);
						
						for(int j=operaIndex-1; j>=0; j--){
							if(entryOperaArrayList.get(j).id == eo.id){
								entryList.add(i, entryOperaArrayList.get(j).entry);
								break;
							}
						}
						break;
					}
				}
				
			}
			else if(eo.opera == Opera.DELETE){
				//entryList.add(eo.pos, eo.entry);
			}
			operaIndex--;
			updateCacheBitmap();
			uodoflag = true;
			return true;
		}
        return false;
	}
    
    public boolean redo(){
        if(operaIndex+1 < entryOperaArrayList.size()){
        	++operaIndex;
        	EntryOpera eo = entryOperaArrayList.get(operaIndex);
        	if(eo.opera == Opera.ADD){
        		boolean flag = false;
				//找到原来的相对位置加入
				for(int i = entryList.size()-1; i>=0; i--){
					if(eo.id < entryList.get(i).getId()){
						entryList.add(i, eo.entry);
						flag = true;
						break;
					}
				}
				if(!flag){
					entryList.add(eo.entry);
				}
				
				for(int i=tempLl_list.size()-1; i>=0; i--){
					ImageButton imgBut = (ImageButton)tempLl_list.get(i);
					Integer id = (Integer)imgBut.getTag();
					
					if(id.intValue() == eo.id){
						ll_list.addView(imgBut);
						tempLl_list.remove(i);
						break;
					}
				}
			}
			else if(eo.opera == Opera.CHANGE){
				exchangeEntry(eo.entry);
			}
			else if(eo.opera == Opera.DELETE){
				//entryList.remove(eo.pos);
			}
			updateCacheBitmap();
			return true;
        }
        uodoflag = false;
        return false;
    }
    
    public void updateOperaList() {
    	ArrayList<Integer> integers = new ArrayList<Integer>();
		while(operaIndex < entryOperaArrayList.size()-1){
			if(entryOperaArrayList.get(operaIndex+1).opera == Opera.DELETE){
				integers.add(entryOperaArrayList.get(operaIndex+1).id);
			}
			entryOperaArrayList.remove(operaIndex+1);
		}
		
		for(int i=0; i<integers.size()-1; i++){
			for(int j=0; j<entryOperaArrayList.size()-1; ){
				if(entryOperaArrayList.get(j).id == integers.get(i)){
					entryOperaArrayList.remove(j);
				}
				else{
					j++;
				}
			}
		}
		
		tempLl_list.clear();
	}

    public void clear(){
    	tempLl_list.clear();
    	entryList.clear();
    	entryOperaArrayList.clear();
    	cacheCanvas.drawColor(Color.WHITE);
    	
    	View v = ll_list.getChildAt(0);
    	ll_list.removeAllViews();
    	ll_list.addView(v);
    	
    	operaIndex = -1;
    	activeEntryId = -1;
    	entryCount = 0;
    }
}

class Entry{
	
	private MyPaint.Entity type;
	private int id;
	public Path path;
    public ArrayList<Integer> points;
    public int color;
    public float stroke;
	public float x1, y1, x2, y2;
	public float rotate;
	
	public Entry(MyPaint.Entity type, int id){
		this.type = type;
		this.id = id;
	}
    
    public MyPaint.Entity getType() {
		return type;
	}
    
    public int getId(){
    	return id;
    }
    
    public void setId(int id){
    	this.id = id;
    }
    
    public Entry copy(){
    	Entry en = new Entry(type, id);
    	en.path = path;
    	en.points = points;
    	en.color = color;
    	en.stroke = stroke;
    	en.x1 = x1;
    	en.y1 = y1;
    	en.x2 = x2;
    	en.y2 = y2;
    	en.rotate = rotate;
    	return en;
    }
}

class EntryOpera{
    public enum Opera{
        DELETE,
        ADD,
        CHANGE,
    }
    public int id;             // 图元id
    public Entry entry;
    public Opera opera;

    public EntryOpera(int id, Opera opera, Entry entry){
        this.id = id;
        this.opera = opera;
        this.entry = entry;
    }
}