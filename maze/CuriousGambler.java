package maze;

import java.util.ArrayList;

import java.util.Random;


public class CuriousGambler implements RobotDriver{
	
	
	
	BasicRobot robot;
	Random aRandom = new Random();
	int totalLength = 0;
	
	int[][]visitedCells;
	
	
	public CuriousGambler(Maze mz){
		
		robot = new BasicRobot();
		
		
		visitedCells = new int[mz.mazew][mz.mazeh];
		
		robot.provideMaze(mz);
		
		try{
		setRobot( robot );
		}
		catch (UnsuitableRobotException badRobot){}
	}
	
	
	
	public void setRobot(Robot r) throws UnsuitableRobotException{
		
		if ( robot.numberOfSensors == 0 )
			throw new UnsuitableRobotException();
		
//		try{
//			drive2Exit();
//		}
//		catch(Exception e){}
		
		
	}
	
	
	
	
	public boolean drive2Exit() throws Exception {
		
		//a container for the available moves
		ArrayList <Integer> availableMoves = new ArrayList<Integer>();
		
		
		int[] upCandidate    = new int[2];
		int[] downCandidate  = new int[2];
		int[] leftCandidate  = new int[2];
		int[] rightCandidate = new int[2];
		
		int curx;
		int cury;
		
		boolean hasBeenRotated;
		
		while ( true ){
			
			curx = robot.getCurrentPosition()[0];
			cury = robot.getCurrentPosition()[1];
			
			
			
			//we have visited the starting position
			visitedCells[curx][cury] += 1;
			
			System.out.println("in this loop");
			
			/////
			hasBeenRotated = false;

			

			//part 1 will be similar to that of Gambler: we must find all available 
			//moves given our sensors
			try{
				System.out.println("the distance to the obstacle ahead: " + robot.distanceToObstacleAhead());
				if ( robot.distanceToObstacleAhead() > 0 ){
					availableMoves.add(1); 
					upCandidate[0] = curx + robot.frontSensor.xSensingDirection;
					upCandidate[1] = cury + robot.frontSensor.ySensingDirection;

				}
			}
			
			catch( UnsupportedMethodException unavailableSensor ){
				System.out.println("line 69");
			}

			try{
				System.out.println("the distance to the obstacle on left: " + robot.distanceToObstacleOnLeft());
				if ( robot.distanceToObstacleOnLeft() > 0 ){
					availableMoves.add(2);
					leftCandidate[0] = curx + robot.leftSensor.xSensingDirection;
					leftCandidate[1] = cury + robot.leftSensor.ySensingDirection;
				}
			}
			catch( UnsupportedMethodException unavailableSensor ){
				System.out.println("line 77");
			}

			//if we did not encounter a valid move we will rotate the robot 
			//and attempt to find one 
			if ( availableMoves.size() == 0 ){

				//we will now attempt to rotate the robot and determine its other available moves
				if ( robot.hasEnergy( (int)robot.getEnergyForFullRotation() ) ){


					try{
						robot.rotate(180); //the robot has now rotated 180 degrees from its original orientation
						if (robot.isAtGoal())
							return true;
						else if (robot.hasStopped())
							return false;
						hasBeenRotated = true;
					}
					catch(UnsupportedArgumentException unsupportedDegree){
						System.out.println("line 91");
					}


					try{
						if ( robot.distanceToObstacleAhead() > 0 ){
							availableMoves.add(3);
							downCandidate[0] = curx + robot.frontSensor.xSensingDirection;
							downCandidate[1] = cury + robot.frontSensor.ySensingDirection;
							
						}
					}
					catch(UnsupportedMethodException unavailableSensor){
						System.out.println("line 101");
					}

					try{
						if ( robot.distanceToObstacleOnLeft() > 0 ){
							availableMoves.add(4);
							rightCandidate[0] = curx + robot.leftSensor.xSensingDirection;
							rightCandidate[1] = cury + robot.leftSensor.ySensingDirection;
						}
					}
					catch(UnsupportedMethodException unavailableSensor){
						System.out.println("line 109");
					}
				}
			}

			
		   /*Now there are three potential possibilities:
			* 1)There is only 1 value in the list of available moves
			* 2)There are two moves in the list with the same value in our grid of visited cells--we must randomly pick one
			* 3)There are two moves with a different value in the grid 
			*/
			
			//set to 3 for debuggin purposes: our list can have a maximum length of 2, so if the variable ndx is not 
			//changed in the following loop, something is done incorrectly 
			int ndx = 3;
			
			
			if (hasBeenRotated){
				
				if (availableMoves.size() > 1){
					
					if ( ( visitedCells[downCandidate[0]][downCandidate[1]] ) ==  ( visitedCells[rightCandidate[0]][rightCandidate[1]] ) ) //pick a random value
						ndx = aRandom.nextInt( availableMoves.size() );
					
					else//pick the cell with the lowest value in our grid
						ndx = (visitedCells[downCandidate[0]][downCandidate[1]]) < (visitedCells[rightCandidate[0]][rightCandidate[1]]) ? 0 : 1;
					
				}	
				else //there is only one value
					ndx = 0;
			}
			
			else{
				
				if (availableMoves.size() > 1){
					
					if ( ( visitedCells[upCandidate[0]][upCandidate[1]] ) ==  ( visitedCells[leftCandidate[0]][leftCandidate[1]] ) ) //pick a random value
						ndx = aRandom.nextInt( availableMoves.size() );
					
					else//pick the cell with the lowest value in our grid
						ndx = (visitedCells[upCandidate[0]][upCandidate[1]]) < (visitedCells[leftCandidate[0]][leftCandidate[1]]) ? 0 : 1;
					
				}	
				else //there is only one value
					ndx = 0;
			}
			
			
			
			
			int direction = availableMoves.get(ndx);

			//depending upon which direction has been chosen, we may need to 
			//rotate the robot ( to move left or right )

			if (hasBeenRotated){

				if (direction == 3){
					try{
						robot.move(1, true);
						totalLength++;
					}
					catch(HitObstacleException wallContact){}
				}
				else{
					try{
						robot.rotate(90);
					}
					catch(UnsupportedArgumentException unsupportedDegree){}
					
					try{
						if (robot.isAtGoal())
							return true;
						else if (robot.hasStopped())
							return false;
					}
					catch(Exception e){}
					
					try{
						robot.move(1, true);
						totalLength++;
					}
					catch(HitObstacleException wallContact){}	
				}
			}

			//if the robot has not been rotated then we only have two values added to the list of 
			//directions
			else{

				if (direction == 1){
					try{
						robot.move( 1, true ); 
						totalLength++;
						System.out.println("179 the angle: " + robot.theMaze.ang);
					}
					catch( HitObstacleException wallContact ){
						System.out.println("line 190");
					} 
				}

				else{ //we move left
					try{
						robot.rotate(90);
						
						
						
						
					}
					catch(UnsupportedArgumentException unsupportedDegree){
						System.out.println("line 199");
					}
					
					try{
						if (robot.isAtGoal())
							return true;
						else if (robot.hasStopped())
							return false;
					}
					catch(Exception e){}
					

					try{
						robot.move( 1, true ); 
						totalLength++;
						System.out.println("198 the angle: " + robot.theMaze.ang);
					}
					catch( HitObstacleException wallContact ){
						System.out.println("line 207");
					} 

				}

			}

			//System.out.println("The battery level: " + robot.batteryLevel);
			
			try{
				if ( robot.isAtGoal() ){
					System.out.println("\nisAtGoal has been entered\n");
					return true;
				}
				
			}
			catch(Exception e){}
			
			try{
			if ( robot.hasStopped() )
				return false;
			}
			catch(Exception e){}


			availableMoves.clear(); //clear the list of moves and repeat the process
			
			
			System.out.println("The battery level" + robot.batteryLevel);
		}
		
		
		
		
		
		
	}
	
	
	
	
	/**
	 * Returns the total energy consumption of the journey
	 */
	public float getEnergyConsumption() {
		return ( 1000 - robot.batteryLevel); //1000 is the initial energy level
	}
	
	
	
	/**
	 * Returns the total length of the journey in number of cells traversed. The initial position counts as 0. 
	 */
	public int getPathLength() {
		return totalLength;
	}
	
	
	
	
	
}










