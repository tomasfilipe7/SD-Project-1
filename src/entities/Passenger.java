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
	private EPassengerState state;
	private DepAirport depAirport;
	private DestAirport destAirport;
	private Plane plane;
	
	private boolean has_arrived_at_airport;
	private boolean has_arrived_at_destination;
	private boolean documents_validated;
	
	
	
	/**
	 * @param state
	 * @param depAirport
	 * @param destAirport
	 * @param plane
	 * @param has_arrived
	 */
	public Passenger(EPassengerState state, DepAirport depAirport, DestAirport destAirport, Plane plane) {
		super();
		this.state = state;
		this.depAirport = depAirport;
		this.destAirport = destAirport;
		this.plane = plane;
		this.has_arrived_at_airport = false;
		this.has_arrived_at_destination = false;
		this.documents_validated = false;
	}
	
	public void validat_documents()
	{
		this.documents_validated = true;
	}
	@Override
	public void run()
	{
		while(!has_arrived_at_destination)
		{
			switch(state)
			{
				case GOING_TO_AIRPORT:
					try {
						depAirport.travelToAirport();
					} catch (MemException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(has_arrived_at_airport)
					{
						state = depAirport.waitInQueue();
					}
					break;
				case IN_QUEUE:
					try {
						if(depAirport.getNextPassenger() == this)
						{
							depAirport.showDocuments();
						}
					} catch (MemException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(documents_validated)
					{
						state = depAirport.boardThePlane();
					}
					break;
				case IN_FLIGHT:
					if(plane.waitForEndOfFlight())
					{
						state = destAirport.leaveThePlane();;
					}
					break;
				case AT_DESTINATION:
					this.has_arrived_at_destination = true;
					break;
			}
		}
	}
}
