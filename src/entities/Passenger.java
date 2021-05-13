/**
 * 
 */
package entities;

import genclass.GenericIO;
import shared_regions.DepAirport;
import shared_regions.DestAirport;
import shared_regions.Plane;

/**
 * @author tomasfilipe7
 * @author marciapires
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
	 * Passenger instantiation
	 * 
	 * @param passengerId 
	 * @param passengerState
	 * @param depAirport reference to dep airport
	 * @param destAirport reference to dest airport
	 * @param plane reference to plane
	 */
	public Passenger(int passengerId, EPassengerState passengerState, DepAirport depAirport, DestAirport destAirport, Plane plane) {
		super();
		this.passengerId = passengerId;
		this.passengerState = passengerState;
		this.depAirport = depAirport;
		this.destAirport = destAirport;
		this.plane = plane;
		this.has_arrived_at_destination = false;
	}

	/**
	 * Life cycle of the Passenger
	 */
	@Override
	public void run()
	{
		while(!has_arrived_at_destination)							// Condition to check the end of Passenger life cycle
		{
			switch(passengerState)									// Check passenger state
			{
				case GOING_TO_AIRPORT:								// State: Going to airport
					travelToAirport();								// The passenger travels to the airport.
					depAirport.waitInQueue();						// The passenger arrives at the queue and starts waiting.
					break;
				case IN_QUEUE:										// State: In Queue
					depAirport.showDocuments();						// The passenger shows his documents to the hostess
					depAirport.boardThePlane();						// Having his documents validated, the passenger will then board the plane
					break;
				case IN_FLIGHT:										// State: In Flight
					plane.waitForEndOfFlight();						// Passenger waits until the end of flight, and leaves the plane
					plane.leaveThePlane();
					break;
				case AT_DESTINATION:								// State: At Destination
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
			sleep((long)(1 + 50 * Math.random()));
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
	 * Getter of DestAirport
	 * @return destAirport
	 */
	public DestAirport getDestAirport() {
		return destAirport;
	}

	/**
	 * Getter of plane
	 * @return plane
	 */
	public Plane getPlane() {
		return plane;
	}

	
}
