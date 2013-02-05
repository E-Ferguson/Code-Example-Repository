package maze; 



/*class will provide the functionality for a cell in the MazeBuilderPrim class. 
 * We need to store 3 things in each cell: 1)whether we consider it as part of 
 * the maze  2) information regarding its walls 3) a container for storing the 
 * walls
 */




public class PrimCell {
	
	
	
	boolean inMaze;
	
	int topWall = 1;
	int botWall = 2;
	int leftWall = 4;
	int rightWall = 8;
	
	boolean hasNoUpperBound;
	boolean hasNoLowerBound;
	boolean hasNoRightBound;
	boolean hasNoLeftBound;
	
	int wallValue; //where the information 
	
	int wallCount = 0;
	
	//each cell should have a unique position
	final int xPosition;
	final int yPosition;
	
	//constructor
	public PrimCell(int x, int y, int width, int height){
		
		if (y == 0)
			hasNoUpperBound = false;
		else
			hasNoUpperBound = true;
		
		if (y == height - 1)
			hasNoLowerBound = false;
		else
			hasNoLowerBound = true;
		
		if (x == 0)
			hasNoLeftBound = false;
		else
			hasNoLeftBound = true;
		
		if (x == width - 1)
			hasNoRightBound = false;
		else
			hasNoRightBound = true;
		
		
		
		//wallContainer = new boolean[4];
		
		xPosition = x;
		yPosition = y; 
		
	}
	
	
	
	/*we will use the following representation for walls:
	 * 	wallContainer[0] = upper wall 
	 *  wallContainer[1] = right wall
	 *  wallContainer[2] = bottom wall present
	 *  wallContainer[3] = left wall present 
	 */
	
	public void storeWall(int i){
		
		
		//we want to ensure that if we store a wall that the opposite side can be visited 
		
		wallValue = i; 
		
		
	}
		
	
	
	
	/* 
	 * method that will return whether the instance
	 * is storing any walls
	 */
	
	public boolean hasWalls(){
		
		
		return wallValue != 0;
		
	}
	
	/*
	 * method returns whether the instance is storing a certain wall
	 */
	
	public boolean holdsWall(int mask){
		
		return (wallValue & mask) != 0; 
		
	}
	
	
	
	
	
	
	
}//end class