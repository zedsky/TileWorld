package tileworld.agent;

import java.util.HashMap;
import java.util.Map;
import tileworld.environment.TWFuelStation;
import sim.util.Bag;
import sim.util.Int2D;

public class Message {
	/*
	 * Usage Tutorial
	 * 
	 * Method 1: Create an empty Message and send it
	 * 		Message msgFuel = new Message("", "");
	 * 		this.getEnvironment().receiveMessage(msgFuel);
	 * 
	 * Method 2: Create a Message containing one thing (eg. Int2D fuelStationPosition) and send it
	 * 		Message msgFuel = new Message("", "");
	 * 		Int2D fuel = new Int2D(this.memory.getFuelStation().getX(), this.memory.getFuelStation().getY());
	 * 		msgFuel.addFuelStationPosition(fuel);
	 * 		this.getEnvironment().receiveMessage(msgFuel);
	 * 
	 * Method 3: Create a Message containing many things
	 * 		Message msg = new Message("", "");
	 * 		Int2D fuel;
	 * 		Bag sensedObjects;
	 * 		msg.addFuelStationPosition(fuel);
	 * 		msg.addSensedObjects(sensedObjects);
	 * 		this.getEnvironment().receiveMessage(msgFuel);
	 * 
	 */
	private String from; // the sender
	private String to; // the recepient
	private String mes;
	/*
	 * String "fuelStationPosition": Object (Int2D) Position of FuelStation
	 * String "sensedObjects": Object (Bag) the objects that sensed by sensor (May contain null)
	 */
	Map<String, Object> message = new HashMap<String, Object>();
	
	public Message(String from, String to, String mes){
		this.from = from;
		this.to = to;
		this.mes = mes;
	}
	
	public Message(String from, String to) {
		this.from = from;
		this.to = to;
		this.message = null;
	}
	
	public Message(String from, String to, Map<String, Object> message){
		this.from = from;
		this.to = to;
		this.message = message;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public Map<String, Object> getMessage() {
		return this.message;
	}
	
	public void addMessage(String Key, Object Value) {
		this.message.put(Key, Value);
	}
	
	public void addFuelStationPosition(TWFuelStation fuelStationPosition) {
		this.addMessage("fuelStationPosition", fuelStationPosition);
	}
	
	public TWFuelStation getFuelStationPosition() {
		if (this.message.containsKey("fuelStationPosition")) {
			return (TWFuelStation) this.message.get("fuelStationPosition");
		}
		else return null;
	}
	
	public void addSensedObjects(Bag sensedObjects) {
		this.addMessage("sensedObjects", sensedObjects);
	}
	
	public Bag getSensedObjects() {
		if (this.message.containsKey("sensedObjects")) {
			return (Bag) this.message.get("sensedObjects");
		}
		else return null;
	}
}