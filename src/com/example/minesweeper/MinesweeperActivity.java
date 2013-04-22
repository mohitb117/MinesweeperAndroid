package com.example.minesweeper;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MinesweeperActivity extends Activity 
{

	float screenHeight,screenWidth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);     
		super.onCreate(savedInstanceState);
		final minesweeperView minesweeper = new minesweeperView(this);
		setContentView(minesweeper);
		
		minesweeper.setBackgroundColor(Color.WHITE);
		
	}
	
	
}


