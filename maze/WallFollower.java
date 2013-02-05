package maze;

import java.util.ArrayList;


import java.util.Random;


public class WallFollower implements RobotDriver{
	
	
	BasicRobot robot;
	int totalLength = 0;
	
	public WallFollower(Maze mz){
		
		robot = new BasicRobot();
		
		
		robot.provideMaze(mz);
		
		try{
			setRobot( robot );
		}
		catch (UnsuitableRobotException badRobot){}
	
	}
	
	public void setRobot(Robot r) throws UnsuitableRobotException{
		
		if ( robot.numberOfSensors == 0 )
			throw new UnsuitableRobotException();
		
		//try{
			//drive2Exit();
		//}
		//catch(Exception e){}
	}
	
	
	
	public boolean drive2Exit() throws Exception {
		
		while ( true ){
			
			

			if (robot.isAtGoal())
				return true;
			else if (robot.hasStopped())
				return false;
			
			
			
			try{
			if ( (robot.distanceToObstacleAhead() == 0) && (robot.distanceToObstacleOnLeft() == 0) ){
				robot.rotate(-90);
				
				if (robot.isAtGoal())
					return true;
				else if (robot.hasStopped())
					return false;
				
				continue;
			}
			}
			catch(UnsupportedMethodException invalidMethod){}
			catch(UnsupportedArgumentException badArgument){}
			
			
			try{
			
			//if there is a clearing to the left we want to rotate and proceed in that direction
			if ( robot.distanceToObstacleOnLeft() > 0 ){
				
				try{
				robot.rotate(90);
				
				if (robot.isAtGoal())
					return true;
				else if (robot.hasStopped())
					return false;
				
				robot.move(1, true);
				totalLength++;
				continue;
				}
				//catch(UnsupportedMethodException invalidMethod){}
				catch(HitObstacleException obstacleEncountered){}
			}
			}
			catch(UnsupportedMethodException invalidMethod){}
			catch(UnsupportedArgumentException badArgument){}
			
			try{
			if (robot.distanceToObstacleAhead() > 0){
				robot.move(1, true);
				totalLength++;
			}
			}
			catch(UnsupportedMethodException invalidMethod){}
			catch(HitObstacleException obstacleEncountered){}
			
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
		
		
		
		
		
		
		
		
		
		
	
	
	
	
}//end class
	