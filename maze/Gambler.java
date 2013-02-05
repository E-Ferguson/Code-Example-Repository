package maze;

import java.util.ArrayList;

import java.util.Random;


public class Gambler implements RobotDriver{
	
	
	
	BasicRobot robot;
	Random aRandom = new Random();
	int totalLength;
	
	
	public Gambler(Maze mz){
		
		robot = new BasicRobot();
		
		
		totalLength = 0;
		
		robot.provideMaze(mz);
		
		try{
		setRobot( robot );
		}
		catch (UnsuitableRobotException badRobot){}
	}
	
	
	/**
	 * We need to determine if we have a suitable robot.  It needs at least 1 sensor
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
	 * The solution-solving algorithm.  The gambler picks a random position to
	 * move to given its sensors.  If there are no available moves, it must rotate
	 * and find one.
	 */
	public boolean drive2Exit() throws Exception {
		
		//a container for the available moves
		ArrayList <Integer> availableMoves = new ArrayList<Integer>();
		
		
		
		
		while ( true ){
			
			
			
			System.out.println("TOTAL LENGTH"+totalLength);
			
			if (robot.isAtGoal())
				return true;
			else if (robot.hasStopped())
				return false;
			
			
			/////
			boolean hasBeenRotated = false;

			

			//part 1: we must find all available moves given our sensors
			try{
				
				if ( robot.distanceToObstacleAhead() > 0 ){
					availableMoves.add(1); 
				}
			}
			catch( UnsupportedMethodException unavailableSensor ){}

			try{
				if ( robot.distanceToObstacleOnLeft() > 0 )
					availableMoves.add(2);
			}
			catch( UnsupportedMethodException unavailableSensor ){}

			//if we did not have a valid move to enter 
			//we will rotate the robot and attempt to find other valid moves 
			if ( availableMoves.size() == 0 ){




				//we will now attempt to rotate the robot and determine its other available moves
				if ( robot.hasEnergy( (int)robot.getEnergyForFullRotation() ) ){


					try{
						robot.rotate(180); //the robot has now rotated 180 degrees from its original orientation
						hasBeenRotated = true;
					}
					catch(UnsupportedArgumentException unsupportedDegree){}


					try{
						if ( robot.distanceToObstacleAhead() > 0 )
							availableMoves.add(3);
					}
					catch(UnsupportedMethodException unavailableSensor){}

					try{
						if ( robot.distanceToObstacleOnLeft() > 0 )
							availableMoves.add(4);
					}
					catch(UnsupportedMethodException unavailableSensor){}
				}
			}

			
			System.out.println("130 the angle: " + robot.theMaze.ang);
			
			int ndx = aRandom.nextInt( availableMoves.size() );

			int direction = availableMoves.get(ndx);

			//depending upon which direction has been chosen, we may need to 
			//rotate the robot ( to move left or right )

			if (hasBeenRotated){

				switch( direction ){

				case 3: //original direction was moving backward, we are now facing this direction 

					try{
						robot.move(1, true);
						
						
						totalLength++;
						System.out.println("148 the angle: " + robot.theMaze.ang);
					}
					catch( HitObstacleException wallContact  ) {
						System.out.println("line 159");
					}
					break;

				case 4: //original direction was moving right, we must rotate to the left

					try{
						robot.rotate(90);
						
						if (robot.isAtGoal())
							return true;
						else if (robot.hasStopped())
							return false;
						robot.move(1, true);
						totalLength++;
						System.out.println("161 the angle: " + robot.theMaze.ang);
					}
					catch( HitObstacleException wallContact  ) {System.out.println("line 170");}
					catch(UnsupportedArgumentException unsupportedDegree){
						System.out.println("line 172");
					}
					break;
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
						
						if (robot.isAtGoal())
							return true;
						else if (robot.hasStopped())
							return false;
						
						System.out.println("189 the angle: " + robot.theMaze.ang);
					}
					catch(UnsupportedArgumentException unsupportedDegree){
						System.out.println("line 199");
					}

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










