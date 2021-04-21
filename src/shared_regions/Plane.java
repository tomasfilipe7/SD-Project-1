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
import genclass.GenericIO;

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
	private boolean has_arrived;
	private GeneralRepos repos;
	
	/**
	 * @param max_passengers
	 * @param min_passengers
	 */
	public Plane(int max_passengers, int min_passengers, GeneralRepos repos) {
		super();
		this.max_passengers = max_passengers;
		this.min_passengers = min_passengers;
		this.currentPassengers = 0;
		passengers_on_plane = new Passenger[max_passengers];
		this.repos = repos;
		this.has_arrived = false;
	}
	
	public void enterPassenger() throws MemException
	{
		GenericIO.writelnString("Entering passenger...");
		Passenger passenger = (Passenger) Thread.currentThread();
		passengers_on_plane[currentPassengers] = passenger;
		currentPassengers += 1;
	}
	
	/**
	 * Check if plane is full
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
		GenericIO.writelnString("Flying to destination point...");
		Pilot p = (Pilot)Thread.currentThread();
		p.setPilotState(EPilotState.FLYING_FORWARD);
		try {
			Pilot.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			GenericIO.writelnString("Catch flyToDestinationPoint error");
			e.printStackTrace();
		}
	}
	
	public synchronized void flyToDeparturePoint()
	{
		this.has_arrived = true;
		GenericIO.writelnString("Waiting for passengers to leave the plane...");
		while(currentPassengers > 0)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				GenericIO.writelnString("Catch error while currentPassengers > 0 at flyToDestinationPoint");
				e.printStackTrace();
			}
		}
		
		GenericIO.writelnString("All passengers out. Flying to departure point...");
		
		Pilot p = (Pilot)Thread.currentThread();
		p.setPilotState(EPilotState.FLYING_BACK);
		try {
			Pilot.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			GenericIO.writelnString("Catch error when pilot state = FLYING_BACK at flyToDestinationPoint");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized boolean waitForEndOfFlight()
	{
		// Implement wait for end of flight
		GenericIO.writelnString("Waiting for the end of flight...");
		while(!this.has_arrived)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				GenericIO.writelnString("Catch error while waiting for the end of flight");
				return false;
			}
		}
		
		GenericIO.writelnString("Flight finished...");
			
		return true;
	}
	
	public synchronized void leaveThePlane()
	{
		// Implement leave the plane
		try {
			Passenger.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			GenericIO.writelnString("Catch error while passengers are leaving the plane");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GenericIO.writelnString("Passengers leaving the plane...");
		passengers_on_plane[currentPassengers] = null;
		currentPassengers -= 1;
		if(currentPassengers <= 0)
		{
			notifyAll();
			GenericIO.writelnString("No more passengers here...");
		}
		((Passenger) Thread.currentThread()).setPassengerState(EPassengerState.AT_DESTINATION);;
	}
}
