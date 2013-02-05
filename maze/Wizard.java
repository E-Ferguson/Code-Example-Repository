package maze;

import java.util.ArrayList;

import java.util.Random;


public class Wizard implements RobotDriver{
	
	
	BasicRobot robot;
	int totalLength = 0;
	
	
	Maze maze;
	
	public Wizard (Maze mz){
		
		maze = mz;
		
		robot = new BasicRobot();
		
		
		robot.provideMaze(mz);
		
		try{
			setRobot( robot );
		}
		catch (UnsuitableRobotException badRobot){}
	
	}
	
	/**
	 * In set robot we ensure that we have a valid robot
	 */
	public void setRobot(Robot r) throws UnsuitableRobotException{
		
		if ( robot.numberOfSensors == 0 )
			throw new UnsuitableRobotException();
		
//		try{
//			drive2Exit();
//		}
//		catch(Exception e){}
	}
	
	/**
	 * The solving algorithm: we use the mazes distance grid to find the adjacent 
	 * cell that is closest to the exit (as there always is one)
	 */
	public boolean drive2Exit() throws Exception {
		
		int[] xDirections = {0, 0, -1, 1};
		int[] yDirections = {-1, 1, 0, 0};
		
		int smallestNum = 0;
		int lowestXpos = 0;
		int lowestYpos = 0;
		
		int lowestXdir = 0;
		int lowestYdir = 0;

		boolean searchStarted = false;
		
		
		
		
		while ( true ){
			
			int curXposition = robot.getCurrentPosition()[0];
			int curYposition = robot.getCurrentPosition()[1];
			
			int curXdirection = robot.getCurrentDirection()[0];
			int curYdirection = robot.getCurrentDirection()[1];
			
			
			if (robot.theMaze.mazedists[curXposition][curYposition] == 1)
				return true;
			else if (robot.hasStopped())
				return false;
			
			System.out.println("gets here");
			
			
			
			
			//first we must find the adjacent cell with the smallest distance from
			//the exit
			searchStarted = true;
			for( int i = 0; i < 4; i++){
				
				if (maze.mazecells.hasMaskedBitsFalse(curXposition, curYposition, (int)Math.pow(2, i))){
					
					if (searchStarted){
						searchStarted = false;
						
						lowestXdir = xDirections[i];
						lowestYdir = yDirections[i];
						
						lowestXpos = curXposition + lowestXdir;
						lowestYpos = curYposition + lowestYdir;
						smallestNum = maze.mazedists[lowestXpos][ lowestYpos];
						continue;
					}
					
					
					int candidate = maze.mazedists[ curXposition + xDirections[i] ][ curYposition + yDirections[i] ];
					
					if (candidate < smallestNum){
						lowestXdir = xDirections[i];
						lowestYdir = yDirections[i];
						
						lowestXpos = curXposition + lowestXdir;
						lowestYpos = curYposition + lowestXdir;
						smallestNum = candidate;
					}
				
				
				}
			}
			
			/* We now travel in the direction of the cell with the lowest distance.  We 
			 * know which the cell in which we want to move, but we may have to orient
			 * the robot in according to its current direction
			 * */  
			
				
			
			if ( curXdirection != 0 ){ //then we are facing right or left
				
				
				int tester = Math.abs(curXdirection + lowestXdir);
				
				switch(tester){
				
					case 0: //then we need to move backwards 
						
						try{
						robot.move(1, false);
						totalLength++;
						break;
						}
						catch(HitObstacleException contact){}
						
					case 2: //then we need to move in the same direction
						
						try{
						robot.move(1, true);
						totalLength++;
						break;
						}
						catch(HitObstacleException contact){}
					case 1: 
						
						if (curXdirection == -1){//then we are facing left and need to rotate either left or right
						
							try{
							robot.rotate(-lowestYdir * 90);
							
							if (robot.theMaze.mazedists[curXposition][curYposition] == 1)
								return true;
							else if (robot.hasStopped())
								return false;
							
							robot.move(1, true);
							totalLength++;
							break;
							}
							catch(HitObstacleException contact){}
							catch(UnsupportedArgumentException invalidArgument){}
						}
						else{ //then we are facing right and need to rotate
							try{
							robot.rotate(lowestYdir * 90);
							
							if (robot.theMaze.mazedists[curXposition][curYposition] == 1)
								return true;
							else if (robot.hasStopped())
								return false;
							
							robot.move(1, true);
							totalLength++;
							break;
							}
							catch(HitObstacleException contact){}
							catch(UnsupportedArgumentException invalidArgument){}
						}
				
				}
			}
			
			
			else{//we are facing up or down
				
				
				int tester = Math.abs(curYdirection + lowestYdir);
				
				switch(tester){
				
					case 0: //then we must move backwards
						try{
						robot.move(1, false);
						totalLength++;
						break;
						}
						catch(HitObstacleException contact){};
						
					case 2: //move in the same direction
						try{
						robot.move(1, true);
						totalLength++;
						break;
						}
						catch(HitObstacleException contact){};		
						
					case 1:
						if (curYdirection == -1){ //then we are facing up and we need to rotate
							
							try{
							robot.rotate(lowestXdir * 90);
							
							if (robot.theMaze.mazedists[curXposition][curYposition] == 1)
								return true;
							else if (robot.hasStopped())
								return false;
							
							robot.move(1, true);
							totalLength++;
							break;
							}
							catch(HitObstacleException contact){}
							catch(UnsupportedArgumentException invalidArgument){}
						}
						else{//then we are facing down
							
							try{
							robot.rotate(-lowestXdir * 90);
							
							if (robot.theMaze.mazedists[curXposition][curYposition] == 1)
								return true;
							else if (robot.hasStopped())
								return false;
							
							robot.move(1, true);
							totalLength++;
							break;
							}
							catch(HitObstacleException contact){}
							catch(UnsupportedArgumentException invalidArgument){}
						}
				
				
				}
				
			}
			
				
			if (robot.theMaze.mazedists[curXposition][curYposition] == 1)
				return true;
			else if (robot.hasStopped())
				return false;
				
		
		}
		
		
		

	}
	
	
	
	/**
	 * Returns the total energy consumption of the journey
	 */
	public float getEnergyConsumption(){
		return ( 1000 - robot.batteryLevel); //1000 is the initial energy level
	}
	
	
	/**
	 * Returns the total length of the journey in number of cells traversed. The initial position counts as 0. 
	 */
	public int getPathLength() {
		return totalLength;
	}
		
		
	
	
	
	
}//end class


