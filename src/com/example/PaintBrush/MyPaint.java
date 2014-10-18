package com.example.PaintBrush;

import android.content.Context;
import android.graphics.Paint;

public class MyPaint extends Paint {
	
	//单件模式
	private static MyPaint thePaint;
	private static Context context;
	public static float range = 30;          //画折线之后的检测区域
	
	//画笔的状态参数
	public enum Entity{
		LINE, OVAL, RECT, BRUSH, ERASER, CIRCLE , FILL , BROKEN
	}
	public Entity entity;
	
	//折线画图的一些参数
	public float preX , preY;
	public float startX , startY;
	
	private MyPaint(){
		super();
		Init();
	}
	
	private MyPaint(int arg0){
		super(arg0);
		Init();
	}
	
	private MyPaint(Paint p){
		super(p);
		Init();
	}
	
	private void Init(){
		//初始化画笔的参数
		this.entity = Entity.BRUSH;
		this.setColor(context.getResources().getColor(R.color.black));
		this.setStrokeWidth(1);
		preX = -range - 1; //小圆点的的有效操作区域为5
		preY = -range - 1;
		startX = -range;
		startY = -range;
	}

	public static MyPaint Instance(Context baseContext) {
		if(null == context)
			context = baseContext;
		
		if(null == thePaint){
			thePaint = new MyPaint();
		}
		return thePaint;
	}
	
}
