package com.example.minesweeper;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.content.Context;
import android.widget.Toast;

public class minesweeperModel 
{
	 private final int size=16;
	 private final int maxMineCount;
	 private int currMineCount;
	 private boolean minesweeperGrid[][];
	 private boolean flagLocations[][];
	 
	 private boolean markedLocations[][];
	 
	 private int adjMineCount[][];
	 
	 private Queue <indexLocations> breadthFirstSearchQ;
	 
	 private List<indexLocations> noAdjMineList;
	 
	 private boolean isAdjInit,GameWon;
	 
	 private int uncoveredCells;
	 private int flagCount;
	 
	 minesweeperModel()
	 {
		 //intialize the minesweeper grid with random mine locations	 
		 minesweeperGrid=new boolean[size][size];
		 flagLocations=new boolean[size][size];
		 adjMineCount=new int[size][size];
		 markedLocations=new boolean[size][size];
		 
		 maxMineCount=(16*16)/6;		 
		 currMineCount=0;
		 
		 
		 boolean flag=true;
		 
		 while(flag)
		 {
			 	for(int i=0;i<size;i++)
			 	{
			 		for (int j=0;j<size;j++)
			 		{
			 			if(minesweeperGrid[i][j]==false && currMineCount<maxMineCount)
			 			{
			 					if(Math.random()>0.8)
			 					{
			 						minesweeperGrid[i][j]=true;
			 						currMineCount+=1;
			 					}
			 			}
			 			
			 			if(currMineCount==maxMineCount)
			 			{
			 				flag=false;
			 				break;
			 			}
			 		}
			 		
			 	}
		 }
		 	//initiaizing the adjacency matrix 
			for(int i=0;i<size;i++)
		 	{
		 		for (int j=0;j<size;j++)
		 		{
		 			getAdjMineCount(new indexLocations(i, j));
		 		}
		 	}
			isAdjInit=true;

	 }
	 
	 
	 public int getAdjMineCount(indexLocations x)
	 {
		 int adjCount=0;
		 
		 if(isAdjInit==false)
		 {
			 for(int i=x.getRow()-1;i<=x.getRow()+1;i++)
			 {
				for(int j=x.getColumn()-1;j<=x.getColumn()+1;j++)
				{
					if(i>=0 && j>=0 && i<size && j<size && minesweeperGrid[i][j]==true)
						adjCount++;
				}
			 }		 
			 if(minesweeperGrid[x.getRow()][x.getColumn()]==false )
			 {
				 adjMineCount[x.getRow()][x.getColumn()]=adjCount;
			 }
			 else
				 adjMineCount[x.getRow()][x.getColumn()]=adjCount-1;
		 }
		 return adjMineCount[x.getRow()][x.getColumn()];
	 }
	 
	 public int getSize()
	 {
		 	return size;
	 }

	public boolean whatsAtLocation(indexLocations x) 
	{
		return minesweeperGrid[x.getRow()][x.getColumn()];
	}

	
	public void toggleFlagLocation(indexLocations x)
	{
		boolean oldval=flagLocations[x.getRow()][x.getColumn()];
		
		flagLocations[x.getRow()][x.getColumn()]=!oldval;
			       
		if(oldval==false && isFlag(x)==true)
		{
			flagCount+=1;
			
			if((size*size)==flagCount+uncoveredCells)
				GameWon=true;
			else
				GameWon=false;
		}
		
		if(oldval==true && isFlag(x)==false)
		{
			flagCount-=1;	
		}
		
	}
	
	
	public int getFlagCount()
	{
		return flagCount;
	}
	
	public boolean isFlag(indexLocations x)
	{
		return flagLocations[x.getRow()][x.getColumn()];
	}
	
	
	public boolean isMarked(indexLocations x)
	{
		return markedLocations[x.getRow()][x.getColumn()];
	}

	public void setMarked(indexLocations x)
	{
		markedLocations[x.getRow()][x.getColumn()]=true;
	}
	
	
	public void unMark(indexLocations x)
	{
		markedLocations[x.getRow()][x.getColumn()]=false;
	}
	
	
	public void incrementUncoveredCellCount( indexLocations x)
	{
		if(uncoveredCells<(size*size)-maxMineCount)
		{
			uncoveredCells++;
		}
		
		if(uncoveredCells==(size*size)-maxMineCount)
			GameWon=true;
		
		markedLocations[x.getRow()][x.getColumn()]=true;
		
	}
	
	
	public void decrementUncoveredCellCount()
	{
		if(uncoveredCells>0)
		{
			uncoveredCells--;
			GameWon=false;
		}
	}
	
	public int getUncoveredCellCount()
	{
		return uncoveredCells;
	}
	
	public boolean isGameWon()
	{
		return GameWon;
	}
	
	
	/*
	 * perform BFS on the matrix
	 * 
	 * starting point i,j
	 * 
	 * push i,j in the queue
	 * 
	 * while(queue.notempty)
	 *    dequeue element
	 *    	enqueue all adjacent elements which have not been exposed
	 *    mark the element enqueued as visited 
	 */
	
	
	public void enQAdjIndexLocations(indexLocations x)
	{
		for(int i=x.getRow()-1;i<x.getRow()+1;i++)
		{
			for(int j=x.getColumn()-1;j<x.getColumn()+1;j++)
			{
				if(i>=0  && j>=0 && i<size && j<size)
				{
					if(!markedLocations[i][j] && !flagLocations[i][j] && !minesweeperGrid[i][j] && adjMineCount[i][j]==0)
					{
						try
						{
							breadthFirstSearchQ.add(new indexLocations(i, j));
						}
						catch(Exception e)
						{
							
						}		
					}
					
				}
			}
		}
		
		
	}
		
	public List<indexLocations> performBFS(indexLocations x)
	{	
		noAdjMineList=new LinkedList<indexLocations>();
		
		breadthFirstSearchQ=new LinkedList<indexLocations>();
		
		breadthFirstSearchQ.add(x);
		
		int i,j;
		
		indexLocations temp;
		try
		{
			while(!breadthFirstSearchQ.isEmpty())
			{
			    temp=breadthFirstSearchQ.remove();
				
			     i=temp.getRow();
				 j=temp.getColumn();	
				
				if(!markedLocations[i][j] && !flagLocations[i][j] && adjMineCount[i][j]==0)
				{
					enQAdjIndexLocations(temp);
					markedLocations[i][j]=true;
					uncoveredCells+=1;
				}

				noAdjMineList.add(temp);
			}
			
		}
		
		catch(Exception e)
		{

		}
		
  		return noAdjMineList;
	}
	
	
	
	
	
}
