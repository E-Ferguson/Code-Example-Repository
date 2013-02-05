/*
 * project name: MazeBuilderPrim.java

 * 
 * author: Eddie Ferguson.
 * 
 * The program uses Prim's randomized algorithm to create a perfect maze. It has an 
 * accompanyint walls.java class that allows us to store the walls in a container and 
 * handle them as necessary. 
 */



package maze; 

import java.util.Random;


import java.util.ArrayList; 

public class MazeBuilderPrim extends MazeBuilder{
	
	
	
	
	ArrayList<walls> container; //a container for our accompanying walls class
	Random numberGenerator;
	
	//use random number in this context
	int[] xDirection = {0, 0, -1, 1};
	int[] yDirection = {-1, 1, 0, 0};
	
	
	
	
	boolean cellInList;
	
	public MazeBuilderPrim(){//(int w, int h ){ 
		
		numberGenerator = new Random();
		cellInList = false;
		container = new ArrayList<walls>();
		
	}
	
	//the deterministic constructor
	public MazeBuilderPrim(boolean deterministic){
		
		
		numberGenerator = new Random();
		numberGenerator.setSeed(5);
		
		cellInList = false;
		container = new ArrayList<walls>();
		
		
		
	}
	
	
	/*class will generate a solution for a maze based upon the 
	 * randomized version Prim's algorithm 
	 */
	
	public void generate(){
		
		//System.out.println("generate started");
		
		//first, we start by selecting a random cell
		int startingx = numberGenerator.nextInt(width); //give us a random number not exceeding the width
		int startingy = numberGenerator.nextInt(height);//same for height
		
		//variables that will allow us to move from 
		//one x, y coordinate to another
		int x;
		int y;
		int newx = 0; 
		int newy = 0;
		
		
		if (startingy != 0)
			container.add(new walls(startingx, startingy, 1));
		if (startingy != height - 1)
			container.add(new walls(startingx, startingy, 2));
		if (startingx != 0)
			container.add(new walls(startingx, startingy, 4));
		if (startingx != width - 1)
			container.add(new walls(startingx, startingy, 8));
		
		
		
		cells.setVirginToZero(startingx, startingy);
		
		
		
		
		//int value = cells.returnValueAt(startingx, startingy); //we are storing the value at position x, y in 
															   //a PrimCell instance
		//pCell.storeWall(value);
		
		//container.add(pCell); //add the recently created value to our list
		
		
		while ( !container.isEmpty() ){
			
			//System.out.println("line"+container.size());
			
			//now we must select a random wall from the list. to do this, we must first select a random cell, 
			//then select a random wall from that cell
			
			int randomIndex = numberGenerator.nextInt(container.size());//we select a random cell (this gives us index positions)
			
			
			walls randomWall = container.get(randomIndex);
			
		
			int wallValue = randomWall.wallPosition;
			
			
			
			
			x = randomWall.xPosition;
			y = randomWall.yPosition;
			
			int positionIndex = 0;
			
			
			
			
			//now we must select the cell that is adjacent to the recently selected wall
			switch (wallValue){
			
			//we will first find the next direction based upon the wall that was selected. For example, if the top wall 
			//was selected we want to examine the above cell. 
			
			case 1: //analyze the above cell (which is on the opposite side of the above wall)
				
				if (cells.hasMaskedBitsFalse(x, y, 32)){ //if we don't have a top bound
					newx = x + xDirection[0];
					newy = y + yDirection[0];
					positionIndex = 0;
					break;
				}
				
				else
					continue; //we cannot use the randomly selected wall because there is nothing on the other side
				
				
				
			case 2://analyze the bottom cell 
				
				if (cells.hasMaskedBitsFalse(x, y, 64)){ //if we don't have a bottom bound
					newx = x + xDirection[1];
					newy = y + yDirection[1];
					positionIndex = 1;
					break;
				}
				else
					continue;
				
			case 4: //analyze the cell to the left
				
				if (cells.hasMaskedBitsFalse(x, y, 128)){ //if we don't have a left bound
					newx = x + xDirection[2];
					newy = y + yDirection[2];
					positionIndex = 2;
					break;
				}
				else
					continue;
			
			case 8: //analyze the cell to the right
				
				if (cells.hasMaskedBitsFalse(x, y, 256)){ //if we don't have a right bound
					newx = x + xDirection[3];
					newy = y + yDirection[3];
					positionIndex = 3;
					break;
				}
				else
					continue;
				
			}
			
			//now we determine if the newly selected position is in our list.
			//we do this by iterating over our collection of cells and determining if
			//the most recently selected x, y coordinate is present in our list
			
			/*for (int i = 0; i < container.size(); i++){
				walls theCell = container.get(i);
				
				if ( theCell.xPosition == newx && theCell.yPosition == newy ){
					//if the item is already present, then we want to remove it
					//container.remove(i);
					cellInList = true;
					break;
					
					
				}
			}
			*/
			/*if ( cellInList ){//|| cells.hasMaskedBitsFalse(newx, newy, 16) ){
				cellInList = false;
				container.remove(randomIndex);
				continue;
			}*/
			
			if (cells.hasMaskedBitsFalse(newx, newy, 16) ){
				container.remove(randomIndex);
				
				continue;
			}
			
			//since we have encountered a 
			cells.deleteWall(x, y, xDirection[positionIndex], yDirection[positionIndex]);
			
			
			
			
			//cells.deleteWall(x, y, xDirection[positionIndex], yDirection[positionIndex]);
			//cells.setWallToZero(newx, newy, -xDirection[positionIndex], -yDirection[positionIndex]);
			
			
			//we need a way of not adding the wall that we have just deleted to the new cell. For example
			//if we have just deleted the upper wall from a cell, then we do not want to add the
			//bottom cell for the new cell
			//!int[] switchList = {2, 1, 8, 4};
			
			
			//!int doNotAddValue = switchList[positionIndex];
			
			//we need to add all of the appropriate walls to our list of walls except for 
			//the one that correlates to the wall that we've just deleted 
			for (int i = 0; i < 4; i++){
				
				int multiple = (int)java.lang.Math.pow(2, i);
				
				//!if ( multiple != doNotAddValue){
					
					if (i == 0 && cells.hasMaskedBitsFalse(newx, newy, 32) && cells.hasMaskedBitsTrue(newx, newy, 1))
							container.add(new walls(newx, newy, multiple));
							
					
					if (i ==1 && cells.hasMaskedBitsFalse(newx, newy, 64) && cells.hasMaskedBitsTrue(newx, newy, 2))
							container.add(new walls(newx, newy, multiple));
					
					if (i == 2 && cells.hasMaskedBitsFalse(newx, newy, 128) && cells.hasMaskedBitsTrue(newx, newy, 4))
						container.add(new walls(newx, newy, multiple));
					
					if (i == 3 && cells.hasMaskedBitsFalse(newx, newy, 256) && cells.hasMaskedBitsTrue(newx, newy, 8))
						container.add(new walls(newx, newy, multiple));
				 
				}
					
			//!}
			
			//container.remove(randomIndex);//remove the wall for the direction
			
			cells.setVirginToZero(newx, newy);
			
		} 
		
		
		
		
		
		
		
		
		///here///
		// compute temporary distances for an (exit) point (x,y) = (width/2,height/2) 
		// which is located in the center of the maze.
		computeDists(width/2, height/2);

		// find most remote point in maze somewhere on the border. Here remoteness
		// is measured as distance from the center, as shown by the fact that we have
		// sent the middle point of the grid to computeDists
		int remotex = -1, remotey = -1, remotedist = 0;
		for (x = 0; x != width; x++) {
			if (dists[x][0] > remotedist) {
				remotex = x;
				remotey = 0;
				remotedist = dists[x][0];
			}
			if (dists[x][height-1] > remotedist) {
				remotex = x;
				remotey = height-1;
				remotedist = dists[x][height-1];
			}
		}
		for (y = 0; y != height; y++) {
			if (dists[0][y] > remotedist) {
				remotex = 0;
				remotey = y;
				remotedist = dists[0][y];
			}
			if (dists[width-1][y] > remotedist) {
				remotex = width-1;
				remotey = y;
				remotedist = dists[width-1][y];
			}
		}

		// recompute distances for an exit point (x,y) = (remotex,remotey)
		computeDists(remotex, remotey);

		// identify cell with the greatest distance. Since we have just sent the most remote
		// point to the compute Dists method and we are to set this as the exit point, we are setting
		// the start position to the cell with the greatest distance.
		setStartPositionToCellWithMaxDistance();

		// make exit position at true exit 
		setExitPosition(remotex, remotey);  //this position is the longest distance from the starting position
	}
	
		
		
		
		
		
		
		
		
	//System.out.println(cells.toString());
		
		
		
		
		
}
	
	
	
	
	
	
	
	
	

	
	
	
	
//end class

