/**
 * 
 */
package entities;

import common_infrastructures.EPassengerState;
import common_infrastructures.MemException;
import shared_regions.DepAirport;
import shared_regions.DestAirport;
import shared_regions.Plane;

/**
 * @author tomasfilipe7
 *
 */
public class Passenger extends Thread
{
	/**
	 * Passenger Identification
	 */
	private int passengerId;
	/**
	 *  Current passenger state
	 */
	private EPassengerState passengerState;
	/**
	 * Reference to the departure airport
	 */
	private final DepAirport depAirport;
	/**
	 * Reference to the destination airport 
	 */
	private final DestAirport destAirport;
	/**
	 * Reference to the plane
	 */
	private final Plane plane;
	/**
	 * Condition to verify the life cycle of the passenger
	 */
	private boolean has_arrived_at_destination;
	/**
	 * Condition to check if the passenger documents were validated
	 */
	private boolean documents_validated;
	
	
	
	/**
	 * @param state
	 * @param depAirport
	 * @param destAirport
	 * @param plane
	 * @param has_arrived
	 */
	public Passenger(int passengerId, EPassengerState passengerState, DepAirport depAirport, DestAirport destAirport, Plane plane) {
		super();
		this.passengerId = passengerId;
		this.passengerState = passengerState;
		this.depAirport = depAirport;
		this.destAirport = destAirport;
		this.plane = plane;
//		this.has_arrived_at_airport = false;
		this.has_arrived_at_destination = false;
		this.documents_validated = false;
	}
	
	/**
	 * Life cycle of the Passenger
	 */
	@Override
	public void run()
	{
		while(!has_arrived_at_destination)
		{
			switch(passengerState)
			{
				case GOING_TO_AIRPORT:
					travelToAirport();								// The passenger travels to the airport.
					depAirport.waitInQueue();		// The passenger arrives at the queue and starts waiting.
					break;
				case IN_QUEUE:
					if(documents_validated)							// If passenger documents were validated, then he is ready to board the plane
					{
						depAirport.boardThePlane();
					}
					else											// If the passenger documents were not validated, then he shows his documents
					{
						depAirport.showDocuments();
					}
					break;
				case IN_FLIGHT:
					if(plane.waitForEndOfFlight())					// Passenger waits until the end of flight, and leaves the plane
					{
						plane.leaveThePlane();
					}
					break;
				case AT_DESTINATION:
					this.has_arrived_at_destination = true;			// End of passenger's life cycle.
					break;
			}
		}
	}
	
	/**
	 * Traveling to the airport
	 * Internal operation
	 */
	public void travelToAirport()
	{
		// Implement travel to airport
		try {
			sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Get passenger state
	 * @return passenger state
	 */
	public EPassengerState getPassengerState() {
		return passengerState;
	}
	/**
	 * Set the passenger state
	 * @param passengerState
	 */
	public void setPassengerState(EPassengerState passengerState) {
		this.passengerState = passengerState;
	}
	/**
	 * Get the passenger identification
	 * @return passenger identification
	 */
	public int getPassengerId() {
		return passengerId;
	}
	/**
	 * Set if the documents were validated
	 * @param documents_validated
	 */
	public void setDocuments_validated(boolean documents_validated) {
		this.documents_validated = documents_validated;
	}
	public boolean getDocuments_validated()
	{
		return this.documents_validated;
	}
	
	
}
