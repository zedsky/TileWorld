/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tileworld.agent;
import sim.util.Int2D;

import java.util.ArrayList;
import java.util.HashMap;
import tileworld.environment.TWEntity;
import tileworld.environment.TWDirection;
import tileworld.environment.TWEnvironment;
import tileworld.environment.TWTile;
import tileworld.environment.TWHole;
import tileworld.environment.TWFuelStation;
import tileworld.exceptions.CellBlockedException;
import tileworld.planners.DefaultTWPlanner;

import java.util.Map;

/**
 * TWContextBuilder
 *
 * @author michaellees
 * Created: Feb 6, 2011
 *
 * Copyright michaellees Expression year is undefined on line 16, column 24 in Templates/Classes/Class.java.
 *
 *
 * Description:
 *
 */
public class SimpleTWAgent extends TWAgent{
	public DefaultTWPlanner planner;
	private String name;
    public SimpleTWAgent(String name, int xpos, int ypos, TWEnvironment env, double fuelLevel) {
        super(xpos,ypos,env,fuelLevel);
        this.name = name;
        //this.memory = new MyMemory(this, env.schedule, env.getxDimension(), env.getyDimension());
        planner = new DefaultTWPlanner();
    }
    
    public void sense() {
        sensor.sense();
    }
    
    
    
    public void communicate() {
    	//Share basic information for auction
        if (planner.currentGoal != null) {
        	Map<String, Object> message = new HashMap<String, Object>();
        	Message m = new Message(this.getName(), "all", message);
        	message.put("Information", 1);
        	message.put("Position", new Int2D(this.getX(),this.getY()));
        	message.put("currrentGoal", new Int2D(planner.currentGoal.getX(),planner.currentGoal.getY()));
        	this.getEnvironment().receiveMessage(m);
        }
        
        
    	//Broadcast fuel station if found
    	if (planner.fuelStation == null && planner.findFuelStation == false) {
			TWFuelStation fuelStation = (TWFuelStation) this.getMemory().fuelStation;
			if (fuelStation != null) {
				Map<String, Object> message = new HashMap<String, Object>();
				planner.fuelStation = fuelStation;
				Message m = new Message("","", message);
				m.addFuelStationPosition(planner.fuelStation);
				this.getEnvironment().receiveMessage(m);
				planner.findFuelStation = true;
				System.out.println("Fuel station found");
			}
    	}
    	
    	//Share the location of tiles if agent is in need
    	planner.handleMessages(this.getEnvironment().getMessages(), "Need tiles", this);
    	//Share the location of holes if agent is in need
    	planner.handleMessages(this.getEnvironment().getMessages(), "Need holes", this);
    }
    
    protected TWThought think() {
//        getMemory().getClosestObjectInSensorRange(Tile.class);
        System.out.println("Simple Score: " + this.score);
//        return new TWThought(TWAction.MOVE,getRandomDirection());
        
        if (planner.fuelStation == null) {
        	planner.handleMessages(this.getEnvironment().getMessages(), "Fuel", this);
        }else {planner.receiveResponse(this.getEnvironment().getMessages(), this);}
        
        TWDirection direction = planner.execute(this);
        
        if (this.getMemory().getMemoryGrid().get(this.getX(), this.getY()) instanceof TWTile && this.getTilesNum() < 3) {
        	TWThought thought = new TWThought(TWAction.PICKUP, direction);
        	return thought;
        }else if (this.getMemory().getMemoryGrid().get(this.getX(), this.getY()) instanceof TWHole && (this.getTilesNum() > 0)) {
        	TWThought thought = new TWThought(TWAction.PUTDOWN, direction);
        	return thought;
        }else if (this.getMemory().getMemoryGrid().get(this.getX(), this.getY()) instanceof TWFuelStation && this.getFuelLevel() < 450) {
        	TWThought thought = new TWThought(TWAction.REFUEL, direction);
        	return thought;
        }else {	
        	TWThought thought = new TWThought(TWAction.MOVE, direction);
        	return thought;
        }
        
    }

    @Override
    protected void act(TWThought thought) {
    	TWEntity entity = (TWEntity) this.getMemory().getMemoryGrid().get(this.getX(), this.getY());
        //You can do:
        //move(thought.getDirection());
        //pickUpTile(Tile)
        //putTileInHole(Hole)
        //refuel()
    	if (thought.getAction() == TWAction.PICKUP) {
    		this.pickUpTile((TWTile)entity);
    		this.getMemory().removeObject((TWTile)entity);
    	} else if (thought.getAction() == TWAction.PUTDOWN) {
    		this.putTileInHole((TWHole)entity);
    		this.getMemory().removeObject((TWHole)entity);
    	}else if (thought.getAction() == TWAction.REFUEL) {
    		this.refuel();
    	}else {
            try {
                this.move(thought.getDirection());
            } catch (CellBlockedException ex) {
            }
    	}
    
    }


    private TWDirection getRandomDirection(){

        TWDirection randomDir = TWDirection.values()[this.getEnvironment().random.nextInt(5)];

        if(this.getX()>=this.getEnvironment().getxDimension() ){
            randomDir = TWDirection.W;
        }else if(this.getX()<=1 ){
            randomDir = TWDirection.E;
        }else if(this.getY()<=1 ){
            randomDir = TWDirection.S;
        }else if(this.getY()>=this.getEnvironment().getxDimension() ){
            randomDir = TWDirection.N;
        }

       return randomDir;

    }

    @Override
    public String getName() {
        return name;
    }
    //Not used
    public DefaultTWPlanner getPlanner() {
        return planner;
    }
}
