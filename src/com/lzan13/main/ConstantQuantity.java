package com.lzan13.main;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class ConstantQuantity {
	
	private static Context mContext;
	
	public static final int MENU_STATE_CLOSE = 0;
	public static final int MENU_STATE_OPEN = 1;
	
	public static final int DURATION_TIME = 500; 	//��������ʱ��
	
	public static int screenWidth; // ��Ļ���
	public static int screenHeight; // ��Ļ�߶ȣ�������״̬����
	public static float screenDensity;	//��Ļ�ܶ�
	
	
	
	public static void setScreenSize(int width, int height, float density){
		screenWidth = width;
		screenHeight = height;
		screenDensity = density;
	}
	
}
