package com.example.minesweeper;

public class indexLocations 
{
	private int row, column;

	public indexLocations(int row,int col) 
	{
		this.row=row;
		this.column=col;
	}
	
	public int getRow()
	{
		return row;
	}
	
	public int getColumn()
	{
		return column;
	}
}
