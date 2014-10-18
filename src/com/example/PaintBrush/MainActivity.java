package com.example.PaintBrush;

import java.io.IOException;

import com.example.PaintBrush.PaintManager.PaintState;

import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	static public MainActivity that = null;
	
	private long exitTime;
//	private List<String> list = new ArrayList<String>();
//	private ArrayAdapter adapter;
	
	private boolean isslided;    //判断是否已经滑出工具栏
	//private FrameLayout fl;
	private LinearLayout ll;
	private LinearLayout ll2;
	private LinearLayout ll_select;
	private LinearLayout ll_color;
	private LinearLayout ll_stroke;
	private LinearLayout ll_list;
	private LinearLayout ll_tool;
	private HorizontalScrollView HSV_color;
	private HorizontalScrollView HSV_stroke;
	private HorizontalScrollView HSV_select;
	private HorizontalScrollView HSV_list;
	private LinearLayout ll_action;
	//private Button btn_ceshi;

	//功能按钮
	private ImageButton btn_select;
	private ImageButton btn_color;
	private ImageButton btn_stroke;
	private ImageButton btn_tool;
	private ImageButton btn_eraser;
	private ImageButton btn_fill;
	private ImageButton btn_list;
	public static BrushView bv;
	
	//隐藏的按钮
	
	//图形选择
	private ImageButton btn_brush;
	private ImageButton btn_line;
	private ImageButton btn_broken;
	private ImageButton btn_rect;
	private ImageButton btn_circle;
	private ImageButton btn_oval;
	//颜色选择
	private ImageButton btn_black;
	private ImageButton btn_red;
	private ImageButton btn_orange;
	private ImageButton btn_yellow;
	private ImageButton btn_green;
	private ImageButton btn_blue;
	private ImageButton btn_blues;
	private ImageButton btn_purple;
	//笔宽选择
	private ImageButton btn_stroke1;
	private ImageButton btn_stroke2;
	private ImageButton btn_stroke3;
	private ImageButton btn_stroke4;
	private ImageButton btn_stroke5;
	private ImageButton btn_stroke6;
	private ImageButton btn_stroke7;
	//功能
	private ImageButton btn_undo;
	private ImageButton btn_redo;
	private ImageButton btn_save;
	private ImageButton btn_clean;
	
	//单件的画笔
	private MyPaint paint;
	
	BrushView brushView;
	//PaintManager paintManager;
	
	//传感器
	private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        that = this;//在其他类可以调用that参数
        
        brushView = (BrushView)findViewById(R.id.brushView);
        //paintManager = brushView.getPaintManager();

        //获取传感器管理服务  
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);  
        
        ll = (LinearLayout)findViewById(R.id.LinearLayout1);
        ll2 = (LinearLayout)findViewById(R.id.LinearLayout2);
        ll_select = (LinearLayout)findViewById(R.id.linearlayout_select);
        ll_color = (LinearLayout)findViewById(R.id.linearlayout_color);
        ll_stroke = (LinearLayout)findViewById(R.id.linearlayout_stroke);
        ll_list = (LinearLayout)findViewById(R.id.linearlayout_list);
        ll_tool = (LinearLayout)findViewById(R.id.linearlayout_tool);
        HSV_color = (HorizontalScrollView)findViewById(R.id.HSV_color);
        HSV_stroke = (HorizontalScrollView)findViewById(R.id.HSV_stroke);
        HSV_select = (HorizontalScrollView)findViewById(R.id.HSV_select);
        HSV_list = (HorizontalScrollView)findViewById(R.id.HSV_list);
        isslided = true;
        
        //TODO 底部的按钮(选中图元，把ll_aciton的visibility设置为visible)实现以下按钮的功能
        ll_action = (LinearLayout)findViewById(R.id.ll_action);
        ((ImageButton)findViewById(R.id.ib_big)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				brushView.paintManager.scaleEntry((float) 1.5, true);
				brushView.invalidate();
			}
        	
        });
        ((ImageButton)findViewById(R.id.ib_small)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				brushView.paintManager.scaleEntry((float) 0.5, true);
				brushView.invalidate();
			}
        	
        });
        ((ImageButton)findViewById(R.id.ib_rotationl)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				brushView.paintManager.rotateEntry(-5);
				brushView.invalidate();
			}
        	
        });
        ((ImageButton)findViewById(R.id.ib_rotationr)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				brushView.paintManager.rotateEntry(5);
				brushView.invalidate();
			}
        	
        });
        ((ImageButton)findViewById(R.id.ib_copy)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				brushView.paintManager.copyEntry();
				brushView.invalidate();
			}
        	
        });
        ((ImageButton)findViewById(R.id.ib_deletety)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				brushView.paintManager.deleteEntry();
				brushView.invalidate();
			}
        	
        });
        
        
        
        //初始化功能按钮
        btn_eraser = (ImageButton)findViewById(R.id.button_eraser);
		btn_fill = (ImageButton)findViewById(R.id.button_fill);
		btn_select = (ImageButton)findViewById(R.id.button_select);
		btn_stroke = (ImageButton)findViewById(R.id.button_stroke);
		btn_list = (ImageButton)findViewById(R.id.button_list);
		btn_color = (ImageButton)findViewById(R.id.button_color);
		btn_tool = (ImageButton)findViewById(R.id.button_tool);
		bv = (BrushView)findViewById(R.id.brushView);
		
		//初始化隐藏的选项按钮
		InitButtons();
		//设置这些按钮的点击事件
		InitButtonsClickListener();
		
		paint = MyPaint.Instance(this.getBaseContext());
		
		btn_select.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(brushView.paintManager.getState() == PaintState.EDIT)
					brushView.paintManager.unSelectEntry();
				
				clearFocus();
				v.setBackground(getResources().getDrawable(R.drawable.roundcornerclick));
				
				if(ll_select.getVisibility() == View.VISIBLE){
					ll2.setVisibility(View.GONE);
					ll_select.setVisibility(View.INVISIBLE);
					HSV_select.setVisibility(View.INVISIBLE);
				}
				else{
					hideLinearLayouts();
					ll2.setVisibility(View.VISIBLE);
					ll_select.setVisibility(View.VISIBLE);
					HSV_select.setVisibility(View.VISIBLE);
					bv.paintManager.setState(PaintState.DRAW);
				}
				
			}
			
		});
		
		btn_color.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clearFocus();
				v.setBackground(getResources().getDrawable(R.drawable.roundcornerclick));
				if(ll_color.getVisibility() == View.VISIBLE){
					ll2.setVisibility(View.GONE);
					ll_color.setVisibility(View.INVISIBLE);
					HSV_color.setVisibility(View.INVISIBLE);
				}
				else{
					hideLinearLayouts();
					ll2.setVisibility(View.VISIBLE);
					ll_color.setVisibility(View.VISIBLE);
					HSV_color.setVisibility(View.VISIBLE);
				}
				
				if(brushView.paintManager.getState() == PaintState.EDIT)
					brushView.paintManager.unSelectEntry();
			}
			
		});
		
		btn_stroke.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clearFocus();
				v.setBackground(getResources().getDrawable(R.drawable.roundcornerclick));
				if(ll_stroke.getVisibility() == View.VISIBLE){
					ll2.setVisibility(View.GONE);
					ll_stroke.setVisibility(View.INVISIBLE);
					HSV_stroke.setVisibility(View.INVISIBLE);
				}
				else{
					hideLinearLayouts();
					ll2.setVisibility(View.VISIBLE);
					ll_stroke.setVisibility(View.VISIBLE);
					HSV_stroke.setVisibility(View.VISIBLE);
				}
				
				if(brushView.paintManager.getState() == PaintState.EDIT)
					brushView.paintManager.unSelectEntry();
			}
			
		});
		
		btn_tool.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clearFocus();
				v.setBackground(getResources().getDrawable(R.drawable.roundcornerclick));
				if(ll_tool.getVisibility() == View.VISIBLE){
					ll2.setVisibility(View.GONE);
					ll_tool.setVisibility(View.INVISIBLE);
				}
				else{
					hideLinearLayouts();
					ll2.setVisibility(View.VISIBLE);
					ll_tool.setVisibility(View.VISIBLE);
				}
				
				if(brushView.paintManager.getState() == PaintState.EDIT)
					brushView.paintManager.unSelectEntry();
			}
			
		});
		
		btn_list.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clearFocus();
				
				v.setBackground(getResources().getDrawable(R.drawable.roundcornerclick));
				if(ll_list.getVisibility() == View.VISIBLE){
					ll2.setVisibility(View.GONE);
					ll_list.setVisibility(View.INVISIBLE);
					HSV_list.setVisibility(View.INVISIBLE);
					
					
				}
				else{
					hideLinearLayouts();
					ll2.setVisibility(View.VISIBLE);
//					ll_list.removeAllViewsInLayout();
//					ll_list.addView(new ImageButton(getBaseContext()));
					ll_list.setVisibility(View.VISIBLE);
					HSV_list.setVisibility(View.VISIBLE);
					
				}
			}
			
		});

		btn_eraser.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clearFocusForPaint();
				v.setBackground(getResources().getDrawable(R.drawable.roundcornerclick));
				paint.entity = MyPaint.Entity.ERASER;
				hideLinearLayouts();
				
				if(brushView.paintManager.getState() == PaintState.EDIT)
					brushView.paintManager.unSelectEntry();
			}
			
		});
		
		btn_fill.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				clearFocusForPaint();
				v.setBackground(getResources().getDrawable(R.drawable.roundcornerclick));
				paint.entity = MyPaint.Entity.FILL;
				hideLinearLayouts();
				
				if(brushView.paintManager.getState() == PaintState.EDIT)
					brushView.paintManager.unSelectEntry();
			}
			
		});
		
		//把ll_list传给PaintManager
        bv.paintManager.setLl_list(ll_list,getBaseContext());
        bv.paintManager.setLl_action(ll_action,getBaseContext());
    }
    
    //selectbutton的共同事件
    private void selectButtonsCommen(View v){	
    	btn_brush.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_line.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_broken.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_rect.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_circle.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_oval.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	
    	btn_eraser.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_fill.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	
    	v.setBackground(getResources().getDrawable(R.drawable.roundcornerclick));
    }
    
    //colorbutton的共同事件
    private void colorButtonsCommen(View v){
    	btn_black.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_red.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_orange.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_yellow.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_green.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_blue.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_blues.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_purple.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	
    	v.setBackground(getResources().getDrawable(R.drawable.roundcornerclick));
    }
    
    //strokebutton的共同事件
    private void strokeButtonsCommen(View v){  	
    	btn_stroke1.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_stroke2.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_stroke3.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_stroke4.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_stroke5.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_stroke6.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_stroke7.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	
    	v.setBackground(getResources().getDrawable(R.drawable.roundcornerclick));
    }
		

    //设置各个按钮的监听事件
    private void InitButtonsClickListener() {
		btn_brush.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				paint.entity = MyPaint.Entity.BRUSH;
				selectButtonsCommen(arg0);
				hideLinearLayouts();
			}
			
		});
		btn_line.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				paint.entity = MyPaint.Entity.LINE;
				selectButtonsCommen(arg0);
				hideLinearLayouts();
			}
			
		});
		btn_broken.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				paint.entity = MyPaint.Entity.BROKEN;
				selectButtonsCommen(arg0);
				hideLinearLayouts();
			}
			
		});
		btn_rect.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				paint.entity = MyPaint.Entity.RECT;
				selectButtonsCommen(arg0);
				hideLinearLayouts();
			}
			
		});
		btn_circle.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				paint.entity = MyPaint.Entity.CIRCLE;
				selectButtonsCommen(arg0);
				hideLinearLayouts();
			}
			
		});
		btn_oval.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				paint.entity = MyPaint.Entity.OVAL;
				selectButtonsCommen(arg0);
				hideLinearLayouts();
			}
			
		});
		
		//颜色绑定事件
		btn_black.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				paint.setColor(getResources().getColor(R.color.black));
				colorButtonsCommen(v);
				hideLinearLayouts();
			}
			
		});
		btn_red.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				paint.setColor(getResources().getColor(R.color.red));
				colorButtonsCommen(v);
				hideLinearLayouts();
			}
			
		});
		btn_orange.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				paint.setColor(getResources().getColor(R.color.orange));
				colorButtonsCommen(v);
				hideLinearLayouts();
			}
			
		});
		btn_yellow.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				paint.setColor(getResources().getColor(R.color.yellow));
				colorButtonsCommen(v);
				hideLinearLayouts();
			}
			
		});
		btn_green.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				paint.setColor(getResources().getColor(R.color.green));
				colorButtonsCommen(v);
				hideLinearLayouts();
			}
			
		});
		btn_blue.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				paint.setColor(getResources().getColor(R.color.blue));
				colorButtonsCommen(v);
				hideLinearLayouts();
			}
			
		});
		btn_blues.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				paint.setColor(getResources().getColor(R.color.darkblue));
				colorButtonsCommen(v);
				hideLinearLayouts();
			}
			
		});
		btn_purple.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				paint.setColor(getResources().getColor(R.color.purple));
				colorButtonsCommen(v);
				hideLinearLayouts();
			}
			
		});
		
		//笔宽选择
		btn_stroke1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				paint.setStrokeWidth(1);
				strokeButtonsCommen(v);
				hideLinearLayouts();
			}
			
		});
		btn_stroke2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				paint.setStrokeWidth(2);
				strokeButtonsCommen(v);
				hideLinearLayouts();
			}
			
		});
		btn_stroke3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				paint.setStrokeWidth(3);
				strokeButtonsCommen(v);
				hideLinearLayouts();
			}
			
		});
		btn_stroke4.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				paint.setStrokeWidth(4);
				strokeButtonsCommen(v);
				hideLinearLayouts();
			}
			
		});
		btn_stroke5.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				paint.setStrokeWidth(5);
				strokeButtonsCommen(v);
				hideLinearLayouts();
			}
			
		});
		btn_stroke6.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				paint.setStrokeWidth(6);
				strokeButtonsCommen(v);
				hideLinearLayouts();
			}
			
		});
		btn_stroke7.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				paint.setStrokeWidth(7);
				strokeButtonsCommen(v);
				hideLinearLayouts();
			}
			
		});
		
		btn_undo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				brushView.undo();
			}
			
		});
		btn_redo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				brushView.redo();
			}
			
		});
		btn_save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				SaveManager sm = new SaveManager();
				try {
					sm.saveBitmap(bv.paintManager.getCacheBitmap());
					Toast.makeText(getBaseContext(), "保存成功", Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				hideLinearLayouts();
			}
			
		});
		btn_clean.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
				alert.setTitle("清空");
				alert.setMessage("清空后无法恢复，确认清空内容？");
				alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				alert.setButton(DialogInterface.BUTTON_POSITIVE, "清空", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						bv.clearCanvas();
					}});
				alert.show();
				hideLinearLayouts();
			}
			
		});
	}


    //绑定各个按钮对应的控件
	private void InitButtons() {
		btn_brush = (ImageButton)findViewById(R.id.btn_brush);
		btn_line = (ImageButton)findViewById(R.id.btn_line);
		btn_broken = (ImageButton)findViewById(R.id.btn_broken);
		btn_rect = (ImageButton)findViewById(R.id.btn_rect);
		btn_circle = (ImageButton)findViewById(R.id.btn_circle);
		btn_oval = (ImageButton)findViewById(R.id.btn_oval);
		
		btn_black = (ImageButton)findViewById(R.id.btn_black);
		btn_red = (ImageButton)findViewById(R.id.btn_red);
		btn_orange = (ImageButton)findViewById(R.id.btn_orange);
		btn_yellow = (ImageButton)findViewById(R.id.btn_yellow);
		btn_green = (ImageButton)findViewById(R.id.btn_green);
		btn_blue = (ImageButton)findViewById(R.id.btn_blue);
		btn_blues = (ImageButton)findViewById(R.id.btn_blues);
		btn_purple = (ImageButton)findViewById(R.id.btn_purple);
		
		btn_stroke1 = (ImageButton)findViewById(R.id.btn_stroke1);
		btn_stroke2 = (ImageButton)findViewById(R.id.btn_stroke2);
		btn_stroke3 = (ImageButton)findViewById(R.id.btn_stroke3);
		btn_stroke4 = (ImageButton)findViewById(R.id.btn_stroke4);
		btn_stroke5 = (ImageButton)findViewById(R.id.btn_stroke5);
		btn_stroke6 = (ImageButton)findViewById(R.id.btn_stroke6);
		btn_stroke7 = (ImageButton)findViewById(R.id.btn_stroke7);
		
		btn_undo = (ImageButton)findViewById(R.id.btn_undo);
		btn_redo = (ImageButton)findViewById(R.id.btn_redo);
		btn_save = (ImageButton)findViewById(R.id.btn_save);
		btn_clean = (ImageButton)findViewById(R.id.btn_clean);

	}



	protected void hideLinearLayouts() {
		ll2.setVisibility(View.GONE);
		ll_select.setVisibility(View.INVISIBLE);
		ll_color.setVisibility(View.INVISIBLE);
		ll_stroke.setVisibility(View.INVISIBLE);
		ll_list.setVisibility(View.INVISIBLE);
		ll_tool.setVisibility(View.INVISIBLE);
		HSV_color.setVisibility(View.INVISIBLE);
		HSV_stroke.setVisibility(View.INVISIBLE);
		HSV_select.setVisibility(View.INVISIBLE);
		HSV_list.setVisibility(View.INVISIBLE);
	}



	protected void clearFocus() {
		btn_select.setBackgroundColor(getResources().getColor(R.color.snow));
		btn_color.setBackgroundColor(getResources().getColor(R.color.snow));
		btn_stroke.setBackgroundColor(getResources().getColor(R.color.snow));
		btn_tool.setBackgroundColor(getResources().getColor(R.color.snow));
		//btn_eraser.setBackgroundColor(getResources().getColor(R.color.snow));
		//btn_fill.setBackgroundColor(getResources().getColor(R.color.snow));
		btn_list.setBackgroundColor(getResources().getColor(R.color.snow));
	}
	
	protected void clearFocusForPaint(){
		btn_select.setBackgroundColor(getResources().getColor(R.color.snow));
		btn_color.setBackgroundColor(getResources().getColor(R.color.snow));
		btn_stroke.setBackgroundColor(getResources().getColor(R.color.snow));
		btn_tool.setBackgroundColor(getResources().getColor(R.color.snow));
		btn_eraser.setBackgroundColor(getResources().getColor(R.color.snow));
		btn_fill.setBackgroundColor(getResources().getColor(R.color.snow));
		btn_list.setBackgroundColor(getResources().getColor(R.color.snow));
		
		btn_brush.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_line.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_broken.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_rect.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_circle.setBackground(getResources().getDrawable(R.drawable.roundcorner));
    	btn_oval.setBackground(getResources().getDrawable(R.drawable.roundcorner));
	}



	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(!isslided){
			
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				slideOut();
				break;
			}
		}
		else if(isslided){
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				hideLinearLayouts();
				slideIn();
				break;
			}
		}
		
		return super.onTouchEvent(event);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
   
	@Override
	protected void onResume() {
		super.onResume();
		if(mSensorManager != null)
			mSensorManager.registerListener(sensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
    protected void onPause() {
        super.onPause();
//        if (mSensorManager != null) {// 取消监听器  
//            mSensorManager.unregisterListener(sensorEventListener);  
//        } 
    }


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(System.currentTimeMillis() - exitTime > 2000){
				Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
				return false;
			}
			else{
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//滑出动画
	private void slideOut(){
		final float p1 = 0;
		final float p2 = - btn_fill.getWidth();
		long durationMillis = 800;
		long startOffset = 100;
		
		//ll.setLeft(btn_ceshi.getWidth() - 200);
		
		//Toast.makeText(v.getContext(), ll.getLeft()+"", Toast.LENGTH_SHORT).show();
		
		//滑动动画
		TranslateAnimation animation = new TranslateAnimation(p1,p2,0,0);
		animation.setInterpolator(new OvershootInterpolator());
		animation.setDuration(durationMillis);
		animation.setStartOffset(startOffset);	
		animation.setAnimationListener(new Animation.AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				int left = ll.getLeft()+(int)(p2-p1);
				int top = ll.getTop();
				int width = ll.getWidth();
				int height = ll.getHeight();
				ll.clearAnimation();
				ll.layout(left, top, left+width, top+height);
				//Toast.makeText(getBaseContext(), ll.getRight()+".", Toast.LENGTH_SHORT).show();
				//ll.setRight(ll.getRight()+btn_brush.getWidth());
				
				
				//
				isslided = true;
				//Toast.makeText(getBaseContext(), ll.getRight()+"chuchu"+isslided, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub

			}
			
		});
		animation.setFillAfter(true);              //阻止动画完成后的其他动作，解决了跳回原来位置的问题
		ll.clearAnimation();
		ll.startAnimation(animation);
		//Toast.makeText(getBaseContext(), ll.getLeft()+":", Toast.LENGTH_SHORT).show();
	}
	
	//滑入动画
	private void slideIn(){
		final float p1 = 0;
		final float p2 = btn_fill.getWidth();
		long durationMillis = 800;
		long startOffset = 100;
		
		//ll.setLeft(btn_ceshi.getWidth() - 200);
		
		//Toast.makeText(v.getContext(), ll.getLeft()+"", Toast.LENGTH_SHORT).show();
		
		//滑动动画
		TranslateAnimation animation = new TranslateAnimation(p1,p2,0,0);
		animation.setInterpolator(new OvershootInterpolator());
		animation.setDuration(durationMillis);
		animation.setStartOffset(startOffset);	
		animation.setAnimationListener(new Animation.AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				int left = ll.getLeft()+(int)(p2-p1);
				int top = ll.getTop();
				int width = ll.getWidth();
				int height = ll.getHeight();
				ll.clearAnimation();
				ll.layout(left, top, left+width, top+height);
				//hideLinearLayouts();
				//Toast.makeText(getBaseContext(), ll.getRight()+".", Toast.LENGTH_SHORT).show();
				//ll.setRight(ll.getRight()+btn_brush.getWidth());
				
				
				//
				isslided = false;
				//Toast.makeText(getBaseContext(), ll.getRight()+"划入"+isslided, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub

			}
			
		});
		animation.setFillAfter(true);              //阻止动画完成后的其他动作，解决了跳回原来位置的问题
		ll.clearAnimation();
		ll.startAnimation(animation);
		//Toast.makeText(getBaseContext(), ll.getLeft()+":", Toast.LENGTH_SHORT).show();
	}
	
    
	//重力感应  摇一摇
	private SensorEventListener sensorEventListener = new SensorEventListener() {
		
		//boolean isSelect = false;
		long chageTime;
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			float x = event.values[0];	//左右
			float y = event.values[1];	//前后
			float z = event.values[2];	//上下
			 
			int value = 13;
			if(Math.abs(x) > value || Math.abs(y) > value || Math.abs(z) > value){
				//Log.i("sensor", "x轴方向的重力加速度" + x +  "；y轴方向的重力加速度" + y +  "；z轴方向的重力加速度" + z);
				if(brushView.paintManager.getState() == PaintState.DRAW 
						&& System.currentTimeMillis()-chageTime > 1000){
					brushView.paintManager.selectDefaultEntry();
					chageTime = System.currentTimeMillis();
					//isSelect = true;
				}
				else if(System.currentTimeMillis()-chageTime > 1000){
					brushView.paintManager.unSelectEntry();
					chageTime = System.currentTimeMillis();
					//isSelect = false;
				}
			}
		}
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
}
