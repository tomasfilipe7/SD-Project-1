/**
 * 
 */
package shared_regions;

import common_infrastructures.MemException;
import entities.EPassengerState;
import entities.EPilotState;
import entities.Passenger;
import entities.Pilot;

/**
 * @author tomasfilipe7
 * @author marciapires
 *
 */

/**
 * Plane class
 * 
 * */
public class Plane 
{	
	/**
	 * Maximum number of passengers on the plane
	 */
	
	private int max_passengers;
	
	/**
	 * Minimum number of passengers on the plane
	 */
	
	private int min_passengers;
	
	/**
	 *  Passengers inside the plane
	 */
	
	private Passenger[] passengers_on_plane;
	
	/**
	 * Number of current passengers in the plane 
	 */
	
	private int currentPassengers;
	
	/**
	 * Condition to verify if the plane has arrived
	 */
	
	private boolean has_arrived;
	
	/**
	 * Reference to the general repository 
	 */
	
	private GeneralRepos repos;
	
	/**
	 * Number of flights 
	 */
	
	private int flightNum;
	
	/**
	 * 
	 * Plane constructor
	 * 
	 * 
	 * @param min_passengers
	 * @param max_passengers
	 * @param repos
	 */
	public Plane(int min_passengers, int max_passengers, GeneralRepos repos) {
		super();
		this.max_passengers = max_passengers;
		this.min_passengers = min_passengers;
		this.currentPassengers = 0;
		passengers_on_plane = new Passenger[max_passengers];
		this.repos = repos;
		this.has_arrived = false;
		this.flightNum = 1;
	}
	
	/**
	 * Set if plane has arrived.
	 * 
	 *  @param has_arrived has arrived
	 */
	
	public void setHas_arrived(boolean has_arrived) {
		this.has_arrived = has_arrived;
	}
	
	
	/**
	 * Get maximum number of passengers.
	 * 
	 * @return max number of passengers 
	 */
	
	public int getMax_passengers() {
		return max_passengers;
	}
	
	/**
	 * Get minimum number of passengers.
	 * 
	 * @return min number of passengers 
	 */
	
	public int getMin_passengers() {
		return min_passengers;
	}
	
	/**
	 * Get current number of passengers.
	 * 
	 * @return current number of passengers
	 */

	public int getCurrentPassengers() {
		return currentPassengers;
	}

	/**
	 * Check if plane is full
	 * 
	 * @return true if the number of current passengers is greater or equal than maximum passengers
	 */
	
	public boolean isFull()
	{
		return currentPassengers >= max_passengers;
	}
	
	/**
	 * Check if plane is ready to depart
	 * 
	 * @return true if the number of current passengers is greater or equal than minimum passengers
	 */
	
	public boolean isReady()
	{
		return currentPassengers >= min_passengers;
	}
	
	/**
	 * Check if plane is empty
	 * 
	 * @return true if the number of current passengers is equal to zero
	 */
	
	public boolean isEmpty()
	{
		return currentPassengers == 0;
	}
	
	/**
	 * Passenger enters the plane 
	 * 
	 * It is called by a passenger when he boards the plane.
	 * 
	 */
	
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
	}
	
	/**
	 * Operation fly to destination point
	 * 
	 * It is called by the pilot when he starts flying to destination point.
	 * 
	 */
	
	public synchronized void flyToDestinationPoint()
	{
		Pilot p = (Pilot)Thread.currentThread();
		repos.reportStatus("departed with " + this.currentPassengers + " passengers.");
		repos.updateStatistics(this.currentPassengers);
		p.setPilotState(EPilotState.FLYING_FORWARD);
		repos.setPilotState(EPilotState.FLYING_FORWARD);
		try {
			Pilot.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Operation fly to departure point
	 * 
	 * It is called by the pilot when he starts flying to the departure point. The pilot will wait until all passengers are out of the plane, to then proceed to fly back.
	 * 
	 */
	
	public synchronized void flyToDeparturePoint()
	{
		notifyAll();
		while(currentPassengers > 0)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Pilot p = (Pilot)Thread.currentThread();
		repos.reportStatus(" returning.");
		p.setPilotState(EPilotState.FLYING_BACK);
		repos.setPilotState(EPilotState.FLYING_BACK);
//		try {
//			Pilot.sleep((long)(1 + 10 * Math.random()));
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		this.flightNum += 1;
		this.repos.setFlightNum(this.flightNum);
		this.has_arrived = false;
	}
	
	/**
	 * Operation waiting for end of flight
	 * 
	 * It is called by the passenger while he is in the plane waiting for the end of flight.
	 * 
	 */
	
	public synchronized void waitForEndOfFlight()
	{
		// Implement wait for end of flight
		while(!this.has_arrived)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
		}
	}
	
	
	/**
	 * Operation leave the plane
	 * 
	 * It is called by the passenger when the plane arrives. The last passenger will then notify the pilot that the plane is now empty
	 * 
	 */
	
	public synchronized void leaveThePlane()
	{
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
			if(this.passengers_on_plane[i] != null && this.passengers_on_plane[i].getPassengerId() == p.getPassengerId())
			{
				this.passengers_on_plane[i] = null;
				break;
			}
		}
		
		currentPassengers -= 1;
		this.repos.setInFlight(this.currentPassengers);
		this.repos.setInPTAL(repos.getInPTAL()+1);
		p.getDestAirport().passengerArrived();
		p.setPassengerState(EPassengerState.AT_DESTINATION);
		repos.setPassengerState(p.getPassengerId(), EPassengerState.AT_DESTINATION);
		if(currentPassengers <= 0)
		{
			notifyAll();
		}
	}
	
}
