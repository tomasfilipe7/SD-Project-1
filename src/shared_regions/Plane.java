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
	private int flightNum;
	private boolean is_comming_back;
	
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
		this.is_comming_back = false;
	}
	
	public synchronized void enterPassenger() throws MemException
	{
		Passenger passenger = (Passenger) Thread.currentThread();
		for(int i = 0; i < this.passengers_on_plane.length; i++)
		{
			if(this.passengers_on_plane[i] == null)
			{
				this.passengers_on_plane[i] = passenger;
				break;
			}
		}
		this.currentPassengers += 1;
		repos.setInFlight(this.currentPassengers);
		GenericIO.writelnString("Current passengers: " + this.currentPassengers);
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
	
	public synchronized void flyToDestinationPoint()
	{
		GenericIO.writelnString("Plane taking off");
		Pilot p = (Pilot)Thread.currentThread();
		repos.reportStatus("departed with " + this.currentPassengers + " passengers.");
		p.setPilotState(EPilotState.FLYING_FORWARD);
		repos.setPilotState(EPilotState.FLYING_FORWARD);
		try {
			Pilot.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void flyToDeparturePoint()
	{
		this.has_arrived = true;
		while(currentPassengers > 0)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		GenericIO.writelnString("Returning");
		Pilot p = (Pilot)Thread.currentThread();
		repos.reportStatus(" returning.");
		p.setPilotState(EPilotState.FLYING_BACK);
		repos.setPilotState(EPilotState.FLYING_BACK);
		try {
			Pilot.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.flightNum += 1;
		this.repos.setFlightNum(this.flightNum);
	}
	
	public synchronized void waitForEndOfFlight()
	{
		// Implement wait for end of flight
		while(!this.has_arrived)
		{
			GenericIO.writelnString("Plane arrived: " + this.has_arrived);
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
			GenericIO.writelnString("Plane arrived: " + this.has_arrived);
		}
		GenericIO.writelnString("Flight Ended");
	}
	
	public synchronized void leaveThePlane()
	{
		GenericIO.writelnString("Leaving plane");
		// Implement leave the plane
		Passenger p = (Passenger) Thread.currentThread();
		try {
			Passenger.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < this.passengers_on_plane.length; i++)
		{
			if(this.passengers_on_plane[i].getPassengerId() == p.getPassengerId())
			{
				this.passengers_on_plane[i] = null;
				break;
			}
		}
		currentPassengers -= 1;
		this.repos.setInFlight(this.currentPassengers);
		if(currentPassengers <= 0)
		{
			GenericIO.writelnString("Leaving");
			notifyAll();
		}
		p.setPassengerState(EPassengerState.AT_DESTINATION);
		repos.setPassengerState(p.getPassengerId(), EPassengerState.AT_DESTINATION);
	}

	public void setHas_arrived(boolean has_arrived) {
		this.has_arrived = has_arrived;
	}

	public boolean isIs_comming_back() {
		return is_comming_back;
	}

	public void setIs_comming_back(boolean is_comming_back) {
		this.is_comming_back = is_comming_back;
	}
	
	
	
	
}
