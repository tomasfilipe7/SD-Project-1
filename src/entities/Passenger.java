/**
 * 
 */
package entities;

import common_infrastructures.EPassengerState;
import common_infrastructures.MemException;
import genclass.GenericIO;
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
	 * 
	 */
	private boolean to_be_called;
	private boolean showing_documents;
	
	
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
		this.to_be_called = false;
		this.showing_documents = false;
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
					try {
						sleep((long)(1 + 50 * Math.random()));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					depAirport.showDocuments();
					GenericIO.writelnString("Documents Validated");
					depAirport.boardThePlane();
					try {
						plane.enterPassenger();
					} catch (MemException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case IN_FLIGHT:
					plane.waitForEndOfFlight();					// Passenger waits until the end of flight, and leaves the plane
					plane.leaveThePlane();
					break;
				case AT_DESTINATION:
					this.destAirport.passengerArrived();
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
	 * Set if the documents were validated
	 * @param documents_validated
	 */
	public void setDocuments_validated(boolean documents_validated) {
		GenericIO.writelnString("Documents validated setter: " + documents_validated);
		this.documents_validated = documents_validated;
	}
	public boolean getDocuments_validated()
	{
		return this.documents_validated;
	}
	
	public boolean isTo_be_called() {
		return to_be_called;
	}

	public void setTo_be_called(boolean to_be_called) {
		this.to_be_called = to_be_called;
	}

	public boolean isShowing_documents() {
		return showing_documents;
	}

	public void setShowing_documents(boolean showing_documents) {
		this.showing_documents = showing_documents;
	}
	
}
