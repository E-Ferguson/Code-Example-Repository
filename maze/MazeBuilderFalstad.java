/*
 * project name: MazeBuilderFalstadTest.java
 * 
 * author: Eddie Ferguson
 * 
 * The test for the generate method using the algorithm found in Falstad's 
 * original MazeBuilder class. 
 * 
 */





package maze;

import java.util.Random;
import java.util.Vector;



public class MazeBuilderFalstad extends MazeBuilder{
	
	
	public MazeBuilderFalstad(){
		
	}
	
	
	public MazeBuilderFalstad(boolean deterministic){
		
		if ( deterministic )
			
			random = new Random();
		
			random.setSeed(5);
		
	}
	
	  
	
	public void generate(){
		
		// pick position (x,y) with x being random, y being 0: so our starting position is a random spot along the top border
		int x = randNo(0, width-1) ;
		int firstx = x ; // constant to memorize initial x coordinate
		int y = 0;       // no need to memorize initial y coordinate
		int dir = 0;
		int origdir = dir;
		cells.setVirginToZero(x, y);//the cell has been visited
		
	
		while (true) { 
			int dx = dirsx[dir], dy = dirsy[dir]; //starts at 0
			if (!cells.canGo(x, y, dx, dy)) { //if we can't go in the direction
				dir = (dir+1) & 3; //simply gives us the next number not greater than 3
				
				if (origdir == dir) { //then there are no available directions 
					// if back at origin (firstx,0) stop.
					if (x == firstx && y == 0)
						break; // exit loop at this point
					int odr = origdirs[x][y];
					dx = dirsx[odr];
					dy = dirsy[odr];
					
					x -= dx; //we move to the previous position
					y -= dy;
					origdir = dir = randNo(0, 3); 
				}
			} else { //if we can go in that direction
				cells.deleteWall(x, y, dx, dy);
				
				x += dx;//both x and y become that position
				y += dy;
				cells.setVirginToZero(x, y);//the new cell has been visited
				origdirs[x][y] = dir; 
				origdir = dir = randNo(0, 3);  //we proceed in a random direction
			}
		} 
		
		
		//following block initializes each cell in the distance array so that it contains
		//the distance from the exit
		
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
				remotedist = dists[x][height-1];  //here: remotedist = dists[x][height-1];
			}
		}
		for (y = 0; y != height; y++) { 
			if (dists[0][y] > remotedist) {
				remotex = 0;
				remotey = y;
				remotedist = dists[0][y];//here remotedist = dists [0][y]
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
		
	
}
	
	
