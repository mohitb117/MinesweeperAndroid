package com.example.minesweeper;

import java.util.LinkedList;
import java.util.List;

import android.R.bool;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class minesweeperView extends View 
{
    private GestureDetector gestureDetector;
    private float screenW,screenH;
    private Paint p =new Paint();
    private int size;
    private float dw,dh;    
    private int i,j;
    private minesweeperModel myModel;
	
    private boolean isMine;
	private boolean isFlagged,wasFlagged;
	private int adjCount;
	
	// Images used -- set in constructor
	private Bitmap adjImage;
	private Bitmap mineImage;
	private Bitmap flagImage;
	private int resID;
	private boolean hasStarted,doubleTap;
	private Rect r1;
	
	
    public float getScreenW()
    {
    	return screenW;
    }
    
    public float getScreenH()
    {
    	return screenH;    	
    }
    
    public minesweeperView(Context context) 
    {
        super(context); // creating new gesture detector
        gestureDetector = new GestureDetector(context, new customGestureListener());
        p.setColor(Color.GRAY);
        myModel=new minesweeperModel();
        size=myModel.getSize();
    }

    /** Utility makes a scaled version of the given bitmap to the given width,
    but keeping the original proportions. */
    
   private Bitmap makeScaled(Bitmap base) 
   {
   	return Bitmap.createScaledBitmap(base,(int)Math.floor(dw),(int)Math.floor(dh), false);
   }    
   
    
    public int convertAdjCount2Image(int adj)
    {
    	String res=new String("img").concat(Integer.toString(adj));
    	
    	return getContext().getResources().getIdentifier(res, "drawable", "com.example.minesweeper");
    }
  
    public void generateAlertDialogBox(String title,String message)
    {
        // prepare the alert box                   
        AlertDialog.Builder alertbox = new AlertDialog.Builder(getContext());
       
        alertbox.setTitle(title);
        
        // set the message to display
        alertbox.setMessage(message);
        
        alertbox.setCancelable(false);
        
        // add a neutral button to the alert box and assign a click listener
       
        alertbox.setNegativeButton("Quit Game", new DialogInterface.OnClickListener() 
        {
	        public void onClick(DialogInterface arg0, int arg1) 
	        {
	        	System.exit(0);
	        }
        });
        
       
        alertbox.setNeutralButton("Restart!", new DialogInterface.OnClickListener() 
        {
            
            // click listener on the alert box
            public void onClick(DialogInterface arg0, int arg1) 
            {
            	myModel=new minesweeperModel();
            	hasStarted=false;
        		
        		myModel=new minesweeperModel();//attaching a new Model to the minesweeper
        	
        		invalidate();
            }
        });
        
        
        // show it
        alertbox.show();
    }


    public void generateInGameAlertDialogBox(String title,String message)
    {
        // prepare the alert box                   
        AlertDialog.Builder alertbox = new AlertDialog.Builder(getContext());
       
        alertbox.setTitle(title+"\n"+"Press Outside the Screen to Resume Game!");
        
        // set the message to display
        alertbox.setMessage(message);
         
        // add a neutral button to the alert box and assign a click listener
       
        alertbox.setNegativeButton("Quit Game", new DialogInterface.OnClickListener() 
        {
	        public void onClick(DialogInterface arg0, int arg1) 
	        {
	        	System.exit(0);
	        }
        });
        
       
        alertbox.setNeutralButton("Restart!", new DialogInterface.OnClickListener() 
        {
            
            // click listener on the alert box
            public void onClick(DialogInterface arg0, int arg1) 
            {
            	myModel=new minesweeperModel();
            	hasStarted=false;
        		
        		myModel=new minesweeperModel();//attaching a new Model to the minesweeper
        	
        		invalidate();
            }
        });
        
        
        // show it
        alertbox.show();
    }

    public void renderCurrentMinesweeperState(Canvas canvas)
    {
    	for(int x=1;x<=15;x++)
    	{
    		canvas.drawLine(x*dw,0,x*dw,getScreenH(),p);
    		canvas.drawLine(0,x*dh,getScreenW(),x*dh,p);
    	}
 
    	if(hasStarted==true)
    	{
    			for(int x=0;x<size;x++)
    			{
    				for(int y=0;y<size;y++)
    				{
    					//iterate over the grid and render the corresponding images!
    				
    					indexLocations temp=new indexLocations(x,y);
    					
    					if(myModel.isFlag(temp)==true)//draw a flag
    					{
    						canvas.drawBitmap(flagImage,x*dw,y*dh,null );			
    					}
    					
    					if(myModel.isFlag(temp)==false && myModel.isMarked(temp)==true)
    					{	
	    				
    						if(myModel.whatsAtLocation(temp)==true)//draw one mine per tap
	    					{
	    		        		canvas.drawBitmap(mineImage,x*dw,y*dh,null );			
	    					}
	    						
	    					if(myModel.whatsAtLocation(temp)==false)//draw a number
	    					{
	    						int adj=myModel.getAdjMineCount(new indexLocations(x,y));
	    						
	    						Bitmap img=makeScaled(BitmapFactory.decodeResource(getContext().getResources(),convertAdjCount2Image(adj)));
	    		        		
	    		        		canvas.drawBitmap(img,x*dw,y*dh,null );			
	    					}
	    								
    					}
    					
    				}
    			}

    			
    			for(int x=0;x<size;x++)
    			{
    				for(int y=0;y<size;y++)
    				{
    					indexLocations temp=new indexLocations(x, y);
    					//rendering on a special case of losing!
    					if(isFlagged==false && isMine==true && doubleTap==true && myModel.isFlag(temp)==false && myModel.whatsAtLocation(temp)==true)//draw all mines 
    					{
    		        		canvas.drawBitmap(mineImage,x*dw,y*dh,null );			
    					}
    				}
    			}
    			
//    	  		if(doubleTap==false)//single tap on a location 
//            	{
//            		if(myModel.isFlag(new indexLocations(i, j))==true)//mark a cell 
//            		{
//            			if(myModel.isGameWon()==true)
//                			generateAlertDialogBox("Game Won", "You Won The Game!!");			        	
//            		}
//            	}
//            	
//            	else if(doubleTap==true )
//            	{
//
//        			//double tapped on a location which is neither flagged nor explored!!
//
//        	      	if(myModel.isFlag(new indexLocations(i, j))==false )
//        			{
//        				if(isMine==true)//double tapped on a mine
//        				{
//        					generateAlertDialogBox("Game Over", "You Lost The Game!!");
//        				}
//        			
//        					
//        				if(isMine==false )//double tapped on a unmined cell
//        				{	        		
//        						    if(myModel.isGameWon()==true)
//        	        			       generateAlertDialogBox("Game Won", "You Won The Game!!");	
//        						    
//        					
//        						    else if(adjCount==0 && myModel.isMarked(new indexLocations(i,j))==false )
//        			        		{
//        				        		List<indexLocations> BFSscan=myModel.performBFS(new indexLocations(i, j));
//        				        			
//        				        		Toast.makeText(getContext(),""+BFSscan.size()+":"+myModel.getUncoveredCellCount(), Toast.LENGTH_SHORT).show();
//        			        		}
//        			
//        			        		else
//        			        			myModel.incrementUncoveredCellCount(new indexLocations(i, j));    			        		
//        			        
//        				}
//        			}
//            	    	
//            	}    		

    			
      	}    	

    }

    			

    	    
    @Override
    public void onDraw(Canvas canvas) 
    {
    	//rendering the grid each time the screen gets invalidated!!
    	
    	renderCurrentMinesweeperState(canvas);
    	
    }
   
    // skipping measure calculation and drawing
    // delegate the event to the gesture detector
    
    @Override
    public boolean onTouchEvent(MotionEvent e) 
    {
        return gestureDetector.onTouchEvent(e);
    }

    public void onSizeChanged (int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        screenW = w;
        screenH = h;    
        
        dw=getScreenW()/size;
    	dh=getScreenH()/size;
    	
    	mineImage=makeScaled(BitmapFactory.decodeResource(getContext().getResources(),R.drawable.mine));
        
        flagImage=makeScaled(BitmapFactory.decodeResource(getContext().getResources(),R.drawable.flag));
      
        //Toast.makeText(getContext(),"ht:"+getScreenH()+" width:"+getScreenW(), Toast.LENGTH_LONG).show();	
    }
    
    private class customGestureListener extends GestureDetector.SimpleOnGestureListener 
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            return true;
        }
        
        
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
     		generateInGameAlertDialogBox("You triggered a Game Menu!!","Restart Game");			        	          
            return true;
        }
        
        public boolean onSingleTapConfirmed(MotionEvent e) 
        {
      		
        	  float x = e.getX(); 
        	  float y = e.getY();
        	 
        	  isFlagged=false;
        	  
        	  i=(int) (x/dw);
        	  j=(int) (y/dh);


         	  indexLocations curr=new indexLocations(i, j);
         	   
        	  if(myModel.isMarked(curr)==false)
        	  {
        		  myModel.toggleFlagLocation(curr);
        		  isFlagged=myModel.isFlag(curr);
        	  }
        	  
        	  isMine=myModel.whatsAtLocation(curr);
        	  adjCount=myModel.getAdjMineCount(curr);
        
        	  doubleTap=false;
              hasStarted=true;

         		if(myModel.isFlag(new indexLocations(i, j))==true)//mark a cell 
        		{
        			if(myModel.isGameWon()==true)
            			generateAlertDialogBox("Game Won", "You Won The Game!!");			        	
        		}
 
        invalidate();
       
        //Toast.makeText(getContext(), "Tapped at: (" + i + "," + j + ") isFlagged "+isFlagged+" isMine "+isMine+" "+adjCount+" isMarked "+myModel.isMarked(curr), Toast.LENGTH_SHORT).show();
        
        return true;
        
        }        
        
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) 
        {
        	float x = e.getX();
            float y = e.getY();

            i=(int) (x/dw);
      	  	j=(int) (y/dh);
      	  
      	   indexLocations curr=new indexLocations(i, j);
      	  	//mark only that location which is neither marked nor flagged!

      	    
     		isMine=myModel.whatsAtLocation(curr);
      	  	isFlagged=myModel.isFlag(curr);
      	  	adjCount=myModel.getAdjMineCount(curr);
      	  	
      	  	if(!(isFlagged==true) && adjCount!=0)                           
      	  	{
      	  		myModel.setMarked(curr);
      	  	}
        
      	doubleTap=true;
        	
        hasStarted=true;

		//double tapped on a location which is neither flagged nor explored!!
      	if(myModel.isFlag(new indexLocations(i, j))==false )
		{
			if(isMine==true)//double tapped on a mine
			{
				generateAlertDialogBox("Game Over", "You Lost The Game!!");
			}
		
			if(isMine==false )//double tapped on a unmined cell
			{	        		
					    if(myModel.isGameWon()==true)
        			       generateAlertDialogBox("Game Won", "You Won The Game!!");	
					    		
					    else if(adjCount==0 && myModel.isMarked(new indexLocations(i,j))==false )
		        		{
			        		List<indexLocations> BFSscan=myModel.performBFS(new indexLocations(i, j));		
			        //		Toast.makeText(getContext(),""+BFSscan.size()+":"+myModel.getUncoveredCellCount(), Toast.LENGTH_SHORT).show();
		        		}
		        		else
		        			myModel.incrementUncoveredCellCount(new indexLocations(i, j));    			        		
			}
		}

      	invalidate();
        
      	  	
         //Toast.makeText(getContext(), 
      	 // "Double Tapped at: (" + i + "," + j + ") isFlag "+isFlagged+" isMine "+isMine+" "+adjCount+"isMarked"+myModel.isMarked(curr), Toast.LENGTH_SHORT).show();
       	
           return true;
        }
       
    }
}
