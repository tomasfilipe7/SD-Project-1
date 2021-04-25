/**
 * 
 */
package entities;

import shared_regions.DepAirport;
import shared_regions.DestAirport;
import shared_regions.Plane;


/**
 * @author tomasfilipe7
 * @author marciapires
 *
 */
public class Pilot extends Thread
{
	/**
	 * Current state of the pilot
	 */
	private EPilotState pilotState;
	
	/**
	 * Reference to the department airport
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
	 * Condition to check if the life cycle of the pilot has finished
	 */
	private boolean has_finished;
	
	/**
	 * @param state
	 * @param depAirport reference to department airport
	 * @param destAirport reference to destination airport
	 * @param plane reference to the plane
	 */
	public Pilot(EPilotState pilotState, DepAirport depAirport, DestAirport destAirport, Plane plane) {
		super();
		this.pilotState = pilotState;
		this.depAirport = depAirport;
		this.destAirport = destAirport;
		this.plane = plane;
		this.has_finished = false;
	}

	/**
	 * Pilot life cycle
	 */
	@Override
	public void run()
	{
		while(!has_finished)															// Condition to check the end of Pilot life cycle
		{
			switch(pilotState)															// Check pilot state
			{
				case AT_TRANSFER_GATE:													// State: At transfer gate
					if(depAirport.jobDone())											// If there are no passangers left, the Pilot ends her life cycle
					{
						has_finished = true;					
						break;
					}
					depAirport.informPlaneReadyForBoarding();							// Inform the hostess that the plane is ready for boarding
					break;
				case READY_FOR_BOARDING:												// State: Ready for boarding
					depAirport.waitForAllBoard();										// Wait for all passengers to board the plane
					break;
				case WAITING_FOR_BOARDING:												// State: Waiting for boarding
					plane.flyToDestinationPoint();										// Fly to destination airport
					break;
				case FLYING_FORWARD:													// State: Flying Forward
					destAirport.announceArrival();										// Annouce to the passangers that the plane has arrived the destination
					break;
				case DEBOARDING:														// State: Deboarding
					plane.flyToDeparturePoint();										// Fly back to the department airport
					break;
				case FLYING_BACK:														// State: Flying Back
					depAirport.parkAtTransferGate();									// Park the plane at the transfer gate
					break;
			}
		}
	}
	
	/**
	 * Getter of the pilot state
	 * @return
	 */
	public EPilotState getPilotState() {
		return pilotState;
	}
	
	/**
	 * Setter of the pilot state
	 * @param pilotState
	 */
	public void setPilotState(EPilotState pilotState) {
		this.pilotState = pilotState;
	}

	/**
	 * Getter of the reference to the plane
	 * @return plane
	 */
	public Plane getPlane() {
		return plane;
	}
	
	

}
