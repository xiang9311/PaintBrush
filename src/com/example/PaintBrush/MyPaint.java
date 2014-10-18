package com.example.PaintBrush;

import android.content.Context;
import android.graphics.Paint;

public class MyPaint extends Paint {
	
	//����ģʽ
	private static MyPaint thePaint;
	private static Context context;
	public static float range = 30;          //������֮��ļ������
	
	//���ʵ�״̬����
	public enum Entity{
		LINE, OVAL, RECT, BRUSH, ERASER, CIRCLE , FILL , BROKEN
	}
	public Entity entity;
	
	//���߻�ͼ��һЩ����
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
		//��ʼ�����ʵĲ���
		this.entity = Entity.BRUSH;
		this.setColor(context.getResources().getColor(R.color.black));
		this.setStrokeWidth(1);
		preX = -range - 1; //СԲ��ĵ���Ч��������Ϊ5
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
