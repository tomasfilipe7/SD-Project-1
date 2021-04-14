/**
 * 
 */
package shared_regions;

import common_infrastructures.EPilotState;
import common_infrastructures.MemException;
import common_infrastructures.MemFIFO;
import entities.Passenger;

/**
 * @author tomasfilipe7
 *
 */
public class Plane 
{
	private int max_passengers;
	private int min_passengers;
	private MemFIFO<Passenger> passengers_on_plane;
	private int currentPassengers;
	
	/**
	 * @param max_passengers
	 * @param min_passengers
	 */
	public Plane(int max_passengers, int min_passengers) {
		super();
		this.max_passengers = max_passengers;
		this.min_passengers = min_passengers;
		this.currentPassengers = this.max_passengers;
		passengers_on_plane = new MemFIFO<Passenger>(this.max_passengers);
	}
	
	public void enterPassenger() throws MemException
	{
		Passenger passenger = (Passenger) Thread.currentThread();
		passengers_on_plane.write(passenger);
		currentPassengers += 1;
	}
	
	public boolean isFull()
	{
		return currentPassengers >= max_passengers;
	}
	public boolean isReady()
	{
		return currentPassengers >= min_passengers;
	}
	
	public boolean isEmpty()
	{
		return passengers_on_plane.isEmpty();
	}
	public EPilotState flyToDestinationPoint()
	{
		// Implement fly to destination point
		return EPilotState.FLYING_FORWARD;
	}
	
	public EPilotState flyToDeparturePoint()
	{
		// Implement fly to departure point
		return EPilotState.AT_TRANSFER_GATE;
	}
	
	public boolean waitForEndOfFlight()
	{
		// Implement wait for end of flight
		return true;
	}
}
