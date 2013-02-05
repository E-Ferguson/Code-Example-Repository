package maze; 




/*
 *Class will provide the functionality for a wall in the MazeBuilderPrim class. 
 *The walls have two necessary characteristics: 1) the position of the cell that
 *contains the cell  2) the particular wall that we are dealing with. In other words, 
 *we store a top wall, bottom wall, etc.
 */



public class walls {
	
	//the wall's corresponding cell position
	final int xPosition;
	final int yPosition;
	
	//variable stores whether we have a top, bottom, left, or right wall
	final int wallPosition;
	
	
	public walls(int x, int y, int position){
		
		xPosition = x;
		yPosition = y;
		wallPosition = position;
		
	}
	
}