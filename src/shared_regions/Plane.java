/**
 * 
 */
package shared_regions;

import common_infrastructures.EPassengerState;
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
	private Passenger[] passengers_on_plane;
	private int currentPassengers;
	
	/**
	 * @param max_passengers
	 * @param min_passengers
	 */
	public Plane(int max_passengers, int min_passengers) {
		super();
		this.max_passengers = max_passengers;
		this.min_passengers = min_passengers;
		this.currentPassengers = 0;
		passengers_on_plane = new Passenger[max_passengers];
	}
	
	public void enterPassenger() throws MemException
	{
		Passenger passenger = (Passenger) Thread.currentThread();
		passengers_on_plane[currentPassengers] = passenger;
		currentPassengers += 1;
	}
	
	/**
	 * Check if plane is fool
	 * @return true if the number of current passengers is greater or equal then max passengers
	 */
	public boolean isFull()
	{
		return currentPassengers >= max_passengers;
	}
	/**
	 * Check if plane is ready to depart
	 * @return true if the number of current passengers is greater or equal then minimum passengers
	 */
	public boolean isReady()
	{
		return currentPassengers >= min_passengers;
	}
	
	public boolean isEmpty()
	{
		return currentPassengers == 0;
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
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}
	
	public void leaveThePlane()
	{
		// Implement leave the plane
		passengers_on_plane[currentPassengers] = null;
		currentPassengers -= 1;
		((Passenger) Thread.currentThread()).setPassengerState(EPassengerState.AT_DESTINATION);;
	}
}
