/**
 * 
 */
package shared_regions;

import common_infrastructures.EPassengerState;
import common_infrastructures.EPilotState;
import common_infrastructures.MemException;
import common_infrastructures.MemFIFO;
import entities.Passenger;
import entities.Pilot;

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
	public void flyToDestinationPoint()
	{
		Pilot p = (Pilot)Thread.currentThread();
		p.setPilotState(EPilotState.FLYING_FORWARD);
		try {
			Pilot.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void flyToDeparturePoint()
	{
		Pilot p = (Pilot)Thread.currentThread();
		p.setPilotState(EPilotState.FLYING_BACK);
		try {
			Pilot.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		try {
			Passenger.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		passengers_on_plane[currentPassengers] = null;
		currentPassengers -= 1;
		if(currentPassengers <= 0)
		{
			notifyAll();
		}
		((Passenger) Thread.currentThread()).setPassengerState(EPassengerState.AT_DESTINATION);;
	}
}
