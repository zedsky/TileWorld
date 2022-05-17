/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tileworld.planners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;
import java.util.Random;

import jdk.internal.agent.Agent;
import sim.util.Int2D;
import tileworld.environment.TWDirection;
import tileworld.environment.TWEntity;
import tileworld.agent.TWAgent;
import tileworld.agent.TWThought;
import tileworld.agent.Message;
import tileworld.environment.TWDirection;
import tileworld.agent.TWAction;
import tileworld.agent.TWAgentWorkingMemory;
import tileworld.environment.TWTile;
import tileworld.environment.TWHole;
import tileworld.environment.TWFuelStation;
import tileworld.Parameters;



/**
 * DefaultTWPlanner
 *
 * @author michaellees
 * Created: Apr 22, 2010
 *
 * Copyright michaellees 2010
 *
 * Here is the skeleton for your planner. Below are some points you may want to
 * consider.
 *
 * Description: This is a simple implementation of a Tileworld planner. A plan
 * consists of a series of directions for the agent to follow. Plans are made,
 * but then the environment changes, so new plans may be needed
 *
 * As an example, your planner could have 4 distinct behaviors:
 *
 * 1. Generate a random walk to locate a Tile (this is triggered when there is
 * no Tile observed in the agents memory
 *
 * 2. Generate a plan to a specified Tile (one which is nearby preferably,
 * nearby is defined by threshold - @see TWEntity)
 *
 * 3. Generate a random walk to locate a Hole (this is triggered when the agent
 * has (is carrying) a tile but doesn't have a hole in memory)
 *
 * 4. Generate a plan to a specified hole (triggered when agent has a tile,
 * looks for a hole in memory which is nearby)
 *
 * The default path generator might use an implementation of A* for each of the behaviors
 *
 */




public class DefaultTWPlanner{
	public int threshold = 25;
	private Int2D randomLocation;
	public boolean findFuelStation = false;
	public boolean ifstart = false;
	public boolean moveDown = false;
	public boolean moveRight = false;
	public boolean moveLeft = false;
	public boolean firstMove = false;
	public boolean endSearch = false;
	public TWFuelStation fuelStation = null;
	public Int2D currentGoal = null;
	public Int2D lastPosition = null;
	public int stuckCount = 0;
	public int region = 0;
	
	
	
    public TWPath generatePlan(TWAgent agent) {
    	//AstarPathGenerator astar= new AstarPathGenerator(agent.getEnvironment(), agent, 50*50);
    	//collect tile if < 3
    	//if (agent.getTiles() < 3) {
    	//	TWTile nearbyT = agent.getMemory().getNearbyTile(agent.getX(), agent.getY(), 100);
    	//	System.out.println(nearbyT);
    	//	if (nearbyT != null) {
    	//		return astar.findPath(agent.getX(), agent.getY(), nearbyT.getX(), nearbyT.getY());
    	//	}else {
    	//		return astar.findPath(agent.getX(), agent.getY(), 0, 0);
    	//	}
    	///} else {
    	//	
    	//}
    	throw new UnsupportedOperationException("Not supported yet.");
    	
    }

    public boolean hasPlan() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void voidPlan() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public Int2D findFuleStationPlan(TWAgent agent) {
    	
    	throw new UnsupportedOperationException("Not supported yet.");
    	
    }
    
    public void assignRegions(TWAgent agent) {
    	//Int2D region1XBound = new Int2D (0, agent.getEnvironment().getxDimension()/2);
    	switch (agent.getName()) {
    	case "agent1":
    		region = 1;
    		break;
    	case "agent2":
    		region = 2;
    		break;
    	case "agent3":
    		region = 3;
    		break;
    	case "agent4":
    		region = 4;
    		break;
    	}
    }
    
    
    //Search fuelStation Plan
    public Int2D searchFuelStation(TWAgent agent) {
    	//stage one find a start position
    	double[] xRange = {0,0};
    	double[] yRange = {0,0};
    	int x = agent.getEnvironment().getxDimension();
    	int y = agent.getEnvironment().getyDimension();
    	int r = Parameters.defaultSensorRange;
    	if (region == 1) {
    		xRange = new double[] {r, Math.floor(x/2 - r)};
    		yRange = new double[] {r, Math.floor(y/2 - r)};
    	}else if (region == 2) {
    		xRange = new double[] {Math.floor(x/2 + r), x - r};
    		yRange = new double[] {r, Math.floor(y/2 - r)};
    	}else if (region == 3) {
    		xRange = new double[] {r, Math.floor(x/2 - r)};
    		yRange = new double[] {Math.floor(y/2 + r), y - r};
    	}else if (region == 4) {
    		xRange = new double[] {Math.floor(x/2 + r), x - r};
    		yRange = new double[] {Math.floor(y/2 + r), y - r};
    	}
    	Int2D start = new Int2D((int)xRange[0],(int)yRange[0]);
    	System.out.println(start);
    	Int2D end = new Int2D((int)xRange[1],(int)yRange[1]);
    	System.out.println(end);
    	//get to the start position of searching
    	if (ifstart == true) {
    		if(agent.getX() == end.getX() && agent.getY()==end.getY()) {
    			endSearch = true;
    		}
    	}
    	
    	if(ifstart == false) {
    		currentGoal = start;
    		if (agent.getX() == currentGoal.getX() && agent.getY()==currentGoal.getY()) {
    			currentGoal = new Int2D ((int)xRange[1],(int)yRange[0]);
    			ifstart = true;
    		}
    	}
    	//make the first non-looping move
    	if ( agent.getX() == currentGoal.getX() && agent.getY()==currentGoal.getY() && firstMove == false) {
    		currentGoal = new Int2D(agent.getX(), agent.getY()+6);
    		if (currentGoal.getY() >= agent.getEnvironment().getyDimension()) {
				currentGoal = new Int2D(end.getX(), end.getY());
			}
    		moveDown = true;
    		firstMove = true;
    	}
    	
    	//Start routing
    	if (moveDown == true) {
    		//first stage, move down for 3
    		if (currentGoal == null) {
    			currentGoal = new Int2D(agent.getX(), agent.getY()+6);
    			if (currentGoal.getY() >= agent.getEnvironment().getyDimension()) {
    				currentGoal = new Int2D(end.getX(), end.getY());
    			}
    			return currentGoal;
    		}
    		else if(agent.getX() == currentGoal.getX() && agent.getY()==currentGoal.getY()) {
    			moveDown = false;
    			if (agent.getX() == (int)xRange[0]) {
    				moveRight = true;
    				currentGoal = new Int2D((int)xRange[1], agent.getY());
    				return currentGoal;
    			}else if (agent.getX() == (int)xRange[1]) {
    				moveLeft = true;
    				currentGoal = new Int2D((int)xRange[0], agent.getY());
    				return currentGoal;
    			}
    		}
    	}
    	
    	if (moveRight == true) {
    		if (currentGoal == null) {
    			currentGoal = new Int2D((int)xRange[1], agent.getY());
    			return currentGoal;
    		}
    		else if(agent.getX() == currentGoal.getX() && agent.getY()==currentGoal.getY()) {
    			currentGoal = null;
    			moveRight = false;
    			moveDown = true;
    			currentGoal = new Int2D(end.getX(), agent.getY()+6);
    			if (currentGoal.getY() >= agent.getEnvironment().getyDimension()) {
    				currentGoal = new Int2D(end.getX(), end.getY());
    			}
    			return currentGoal;
    			}
    	}
    	
    	if (moveLeft == true) {
    		if (currentGoal == null) {
    			currentGoal = new Int2D((int)xRange[0], agent.getY());
    			return currentGoal;
    		}
    		else if(agent.getX() == currentGoal.getX() && agent.getY()==currentGoal.getY()) {
    			moveLeft = false;
    			moveDown = true;
    			currentGoal = new Int2D(start.getX(), agent.getY()+6);
    			if (currentGoal.getY() >= agent.getEnvironment().getyDimension()) {
    				currentGoal = new Int2D(end.getX(), end.getY());
    			}
    			return currentGoal;
    			}
    	}
    	System.out.println(moveDown);
    	return currentGoal;
    	
    }	
    
    //Refuel Plan
    //In this plan, the agent will move to the fuel station when its fuel level is lower than a threshold.
    //During the tracking back to the fuel station, agent will also decide to pickup/putdown the tile
     
    public Int2D refuelPlan(TWAgent agent, TWFuelStation FuelStation) {
    	Int2D fuelPos = new Int2D (FuelStation.getX(), FuelStation.getY());
    	Int2D defaultGoal = defaultPlan(agent);
    	Int2D currentGoal;
    	int fuelCostToCurrentGoal;
    	int fuelCostToStation;
    	double fuelLevel= agent.getFuelLevel();
    	
    	AstarPathGenerator astar= new AstarPathGenerator(agent.getEnvironment(), agent, agent.getEnvironment().getxDimension()*agent.getEnvironment().getyDimension());
    	//Calculate fuel cost to station
    	TWPath pathToFuel = astar.findPath(agent.getX(), agent.getY(), fuelPos.getX(), fuelPos.getY());
    	if (pathToFuel != null) {
    		fuelCostToStation = pathToFuel.getpath().size();
    	}else {
    		fuelCostToStation = 0;
    	}
    	//Calculate fuel cost to default plan location
    	TWPath pathToCurrentGoal = astar.findPath(agent.getX(), agent.getY(), defaultGoal.getX(), defaultGoal.getY());
    	if (pathToCurrentGoal != null) {
    		fuelCostToCurrentGoal = pathToCurrentGoal.getpath().size();
    	}else {
    		fuelCostToCurrentGoal = 0;
    	}
    	//Do the default plan if the fuel level is adequate
    	if(fuelCostToStation + fuelCostToCurrentGoal < fuelLevel - 20) {
    		currentGoal = defaultGoal;
    		//currentGoal = fuelPos;
    		return currentGoal;
    	}
    	//Go to the fuel station if the above condition is not satisfied
    	currentGoal = fuelPos;
    	return currentGoal;
    }
    
    
    //Default exploring plan
    public Int2D defaultPlan(TWAgent agent) {
    	//Set up current goal accord
    	//Pick up tile when it is nearby
    	if(agent.getTilesNum() < 3  && (agent.getMemory().getNearbyTile(agent.getX(), agent.getY(), threshold) != null)){
    		TWTile nearbyT = agent.getMemory().getNearbyTile(agent.getX(), agent.getY(), threshold);
    		Int2D currentGoal = new Int2D(nearbyT.getX(),nearbyT.getY());
    		if (nearbyT.getX() == agent.getX() && nearbyT.getY() == agent.getY()) {
    			agent.getMemory().removeObject(nearbyT);
    		}
    		randomLocation = null;
    		System.out.println("Heading to tile");
    		return currentGoal;
    	//When carried tile > 0 and no tile nearby && there is a hole within the sensor range
    	} else if (agent.getTilesNum() > 0 && (agent.getMemory().senseHole() != null)) {
    		TWHole holeInRange = agent.getMemory().senseHole();
    		Int2D currentGoal = new Int2D(holeInRange.getX(),holeInRange.getY());
    		randomLocation = null;
    		System.out.println("Heading to hole");
    		return currentGoal;
    	//When carried tile = 3 and no hole within the sensor range, then find the nearby hole according to memory
    	} else if (agent.getTilesNum() > 2 && (agent.getMemory().getNearbyHole(agent.getX(), agent.getY(), threshold) != null)) {
    		TWHole nearbyH = agent.getMemory().getNearbyHole(agent.getX(), agent.getY(), threshold);
    		Int2D currentGoal = new Int2D(nearbyH.getX(),nearbyH.getY());
    		if (nearbyH.getX() == agent.getX() && nearbyH.getY() == agent.getY()) {
    			agent.getMemory().removeObject(nearbyH);
    		}
    		randomLocation = null;
    		System.out.println("Carried tile = 3, heading to hole");
    		return currentGoal;
    	//random walk
    	} else {
    		Int2D currentGoal = generateRandomLocation(agent);
    		System.out.println("randomwalk");
    		return currentGoal;
    	}
    }
    
    //Main Part of the planner
    public TWDirection execute(TWAgent agent) {
    	assignRegions(agent);
    	System.out.println(agent.getName());
    	System.out.println("region is " + region);
    	// Broadcast fuel station if found
    	//TWTile sensedTile = agent.getMemory().senseTile();
    	AstarPathGenerator astar= new AstarPathGenerator(agent.getEnvironment(), agent, agent.getEnvironment().getxDimension() * agent.getEnvironment().getyDimension());
    	System.out.println("Fuel station is found as " + fuelStation);
    	System.out.println("current fuel level is" + agent.getFuelLevel());
    	if (fuelStation == null) {
    		currentGoal = searchFuelStation(agent);
    		System.out.println("Searching Mode");
    	}
    	else if (fuelStation != null && agent.getFuelLevel() < 300) {
    		currentGoal = refuelPlan(agent, fuelStation);
    	} else {
    		currentGoal = defaultPlan(agent);
    	}

    	//Bidding if agents have a conflict goal
		currentGoal = auction(agent.getEnvironment().getMessages(), agent);
    	//check if stuck
    	ifStuck(agent);
    	
    	System.out.println(currentGoal);
    	if (currentGoal != null) {
    		TWPath path =  astar.findPath(agent.getX(), agent.getY(), currentGoal.getX(), currentGoal.getY());
    		if (path == null) {
    			return TWDirection.Z;
    		}else {
    			return path.getStep(0).getDirection();
    		}
    	} else {
    		return getRandomDirection(agent);
    	}
    }
    
    
    // Further limit the range of random location 
    public Int2D randomLocationFit(TWAgent agent) {
    	int x;
    	int y;
    	int xBound = agent.getEnvironment().getxDimension();
    	int yBound = agent.getEnvironment().getyDimension();
    	int r = Parameters.defaultSensorRange;
    	
    	Int2D location = generateFarRandomLocation(agent.getX(), agent.getY(), 10, agent);
    	if (location.getX() - r < 0) {
    		x = r;
    	}else if (location.getX() + r > xBound) {
    		x =  xBound - r;
    	}else {
    		x=location.getX();
    	}
    	
    	if (location.getY() - r < 0) {
    		y = r;
    	}else if (location.getY() + r > yBound) {
    		y =  yBound - r;
    	}else {
    		y=location.getY();
    	}
    	
    	return new Int2D(x,y);
    }
    
    //Modified random location generator without accessing the object grid
    //This part only generate a random location within the board size
    public Int2D generateFarRandomLocation(int x, int y, int minDistance, TWAgent agent) {
        int gx = 1, gy = 1;
        Random rand = new Random();
        while(true) {
        	gx = rand.nextInt(agent.getEnvironment().getxDimension());
        	gy = rand.nextInt(agent.getEnvironment().getyDimension());
        	if (agent.getDistanceTo(gx, gy) > minDistance) {
        		break;
        	}
        }
        return new Int2D(gx, gy);
    }
    
    
    //This part will make sure the random goal is generated only for once
    //if the plan doesn't change.
    public Int2D generateRandomLocation (TWAgent agent) {
    	if (randomLocation == null) {
    		randomLocation = randomLocationFit(agent);
    	} 
    	else if (agent.getX() == randomLocation.getX() && agent.getY() == randomLocation.getY()) {
    		randomLocation = randomLocationFit(agent);
    	}
    	else {
    		return randomLocation;
    	}
    	while (true) {
    		if (randomLocation == null) {
    			randomLocation = randomLocationFit(agent);
    			continue;
    		}else {
    			return randomLocation;
    		}
    	}
    	
    }
    
    //Process the requirements from agents
    //Response to every need from other agents
    public void handleMessages(ArrayList<Message> messages, String need, TWAgent agent) {
    	
    	if (messages.isEmpty()) {
    		return;
    	}
    	if (need == "Fuel") {
    		for (int i = 0; i < messages.size(); i++) {
    			if (messages.get(i).getFuelStationPosition() != null) {
    				fuelStation = messages.get(i).getFuelStationPosition();
    				agent.getMemory().getMemoryGrid().set(fuelStation.getX(), fuelStation.getY(), (TWFuelStation) fuelStation);
    				System.out.println(agent.getMemory().getMemoryGrid().get(fuelStation.getX(), fuelStation.getY()) instanceof TWFuelStation);
    			}
    		}
    	}
    	//process requests from other agents
    	if (need == "Need tiles") {
    		for (int i = 0; i < messages.size(); i++) {
    			if (messages.get(i).getMessage().containsKey("Need tiles")) {
    				//get the requester's name
    				String toAgent = messages.get(i).getFrom();
    				//get requestor's position
    				Int2D agentPos = (Int2D) messages.get(i).getMessage().get("Position");
    				//find the tile that is near to the requester
    				TWTile tile = agent.getMemory().getNearbyTile(agentPos.getX(), agentPos.getY(), threshold);
    				if (currentGoal == null) {
    					return;
    				}
    				if (tile == null || (tile != null && (tile.getX() == currentGoal.getX() && tile.getY() == currentGoal.getY()))) {
    					continue;
    				}
    				Map<String, Object> message = new HashMap<String, Object>();
    				Message m = new Message(agent.getName(), toAgent, message);
    				message.put("Find tile near to you", tile);
    				agent.getEnvironment().receiveMessage(m);
    			}    		
    		}
    	}
    	
    	if (need == "Need holes") {
    		for (int i = 0; i < messages.size(); i++) {
    			if (messages.get(i).getMessage().containsKey("Need holes")) {
    				//get the requester's name
    				String toAgent = messages.get(i).getFrom();
    				//get requestor's position
    				Int2D agentPos = (Int2D) messages.get(i).getMessage().get("Position");
    				//find the hole that is near to the requester
    				TWHole hole = agent.getMemory().getNearbyHole(agentPos.getX(), agentPos.getY(), threshold);
    				if (currentGoal == null) {
    					return;
    				}
    				if (hole == null || (hole != null && (hole.getX() == currentGoal.getX() && hole.getY() == currentGoal.getY()))) {
    					continue;
    				}
    				Map<String, Object> message = new HashMap<String, Object>();
    				Message m = new Message(agent.getName(), toAgent, message);
    				message.put("Find hole near to you", hole);
    				agent.getEnvironment().receiveMessage(m);
    			}    		
    		}
    	}
    	
    }
    
    //receive the messages that other agents send to this agent
    public void receiveResponse(ArrayList<Message> messages, TWAgent agent) {
    	
    	if (messages.isEmpty()) {
    		return;
    	}
    	
    	for (int i = 0; i < messages.size(); i++) {
    		if (messages.get(i).getTo() == agent.getName()){
    			if (messages.get(i).getMessage().containsKey("Find tile near to you")) {
    				TWTile tile = (TWTile) messages.get(i).getMessage().get("Find tile near to you");
    				if (tile != null) {
    					agent.getMemory().getMemoryGrid().set(tile.getX(), tile.getY(), tile);
    					System.out.println(agent.getName() + ": tile received!");
    				}
    			}
    			if (messages.get(i).getMessage().containsKey("Find hole near to you")) {
    				TWHole hole = (TWHole) messages.get(i).getMessage().get("Find hole near to you");
    				if (hole != null) {
    					agent.getMemory().getMemoryGrid().set(hole.getX(), hole.getY(), hole);
    					System.out.println(agent.getName() + ": hole received!");
    				}
    			}
    			
    		}
    	}

    }
    
    //Calculate manhattan distance between two points
    public double getDistance(int x, int y, int x1, int y1) {
        return Math.abs(x1 - x) + Math.abs(y1 - y);
    }
    
    //Simple bidding 
    //When several agents have conflict goal,
    //agent which is closer to the goal will have the goal,
    //and other will change plan
    public Int2D auction(ArrayList<Message> messages, TWAgent agent) {
    	int x = agent.getX();
    	int y = agent.getY();
    	//if the goal is null, no auction
    	if (currentGoal == null ) {
    		return currentGoal;
    	}
    	//if the agent is heading to fuelStation, then no auction is needed
    	if (fuelStation != null) {
    		if (currentGoal != null && currentGoal.getX() == fuelStation.getX() && currentGoal.getY() == fuelStation.getY()) {
    			return currentGoal;
    		}
    	}
    	
    	for (int i = 0; i < messages.size(); i++) {
    		if (messages.get(i).getMessage().containsKey("Information") && (messages.get(i).getFrom() != agent.getName())){
    			Int2D agentPos = (Int2D) messages.get(i).getMessage().get("Position");
    			Int2D agentGoal = (Int2D) messages.get(i).getMessage().get("currrentGoal");
    			if (agentGoal == null) {
    				continue;
    			}
    			//if conflict happens
    			else if (agentGoal.getX() == currentGoal.getX() && agentGoal.getY() == currentGoal.getY()) {
    				System.out.println("Bidding start!");
    				double agentToGoal = getDistance(x, y, currentGoal.getX(), currentGoal.getY());
    				double anotherAgentToGoal = getDistance(agentPos.getX(), agentPos.getY(), currentGoal.getX(), currentGoal.getY());
    				if (agentToGoal < anotherAgentToGoal) {
    					System.out.println(agent.getName() + " win the bid");
    					continue;
    				}else if (agentToGoal > anotherAgentToGoal) {
    					agent.getMemory().getMemoryGrid().set(currentGoal.getX(), currentGoal.getY(), null);
    					currentGoal = generateRandomLocation(agent);
    				}else {
    					currentGoal = generateRandomLocation(agent);
    				}
    			}else {
    				continue;
    			}
    		}
    	}
    	return currentGoal;
    }
    
    //Check if the agent is stuck at one position for two time step
    //if the agent is stuck, it will go to another random location
    public void ifStuck (TWAgent agent) {
    	if (agent.getFuelLevel() <= 0) {
    		return;
    	}
    	Int2D currentPos = new Int2D(agent.getX(),agent.getY());
    	if(lastPosition != null) {
    		if (lastPosition.getX() == currentPos.getX() && lastPosition.getY() == currentPos.getY()) {
    			stuckCount += 1;
    		} else {
    			stuckCount = 0;
    		}
        }
        lastPosition = currentPos;
        if (stuckCount > 1) {
        	System.out.println(agent.getName() + " is stuck, regenerating route");
        	currentGoal = generateFarRandomLocation(agent.getX(), agent.getY(), 12,agent);
        }
    }
    
    //Built-in function SimpleTWAgent
    private TWDirection getRandomDirection(TWAgent agent){
        TWDirection randomDir = TWDirection.values()[agent.getEnvironment().random.nextInt(5)];
        
        if(agent.getX()>=agent.getEnvironment().getxDimension() ){
            randomDir = TWDirection.W;
        }else if(agent.getX()<=1 ){
            randomDir = TWDirection.E;
        }else if(agent.getY()<=1 ){
            randomDir = TWDirection.S;
        }else if(agent.getY()>=agent.getEnvironment().getxDimension() ){
            randomDir = TWDirection.N;
        }

       return randomDir;

    }

}

