/**
 * 
 */
package entities;

import common_infrastructures.EPilotState;
import genclass.GenericIO;
import shared_regions.DepAirport;
import shared_regions.DestAirport;
import shared_regions.Plane;


/**
 * @author tomasfilipe7
 *
 */
public class Pilot extends Thread
{
	private EPilotState pilotState;
	private final DepAirport depAirport;
	private final DestAirport destAirport;
	private final Plane plane;
	private boolean has_finished;
	
	/**
	 * @param state
	 * @param depAirport
	 * @param destAirport
	 * @param plane
	 */
	public Pilot(EPilotState pilotState, DepAirport depAirport, DestAirport destAirport, Plane plane) {
		super();
		this.pilotState = pilotState;
		this.depAirport = depAirport;
		this.destAirport = destAirport;
		this.plane = plane;
		this.has_finished = false;
	}

	@Override
	public void run()
	{
		while(!has_finished)
		{
			switch(pilotState)
			{
				case AT_TRANSFER_GATE:
					GenericIO.writelnString("Pilot AT_TRANSFER_GATE (Pilot state)");
					if(plane.isIs_comming_back() && depAirport.QueueIsEmpty())
					{
						has_finished = true;
						break;
					}
					depAirport.informPlaneReadyForBoarding();
					break;
				case READY_FOR_BOARDING:
					depAirport.waitForAllBoard();
					break;
				case WAITING_FOR_BOARDING:
					plane.flyToDestinationPoint();
					break;
				case FLYING_FORWARD:
					destAirport.announceArrival();
					break;
				case DEBOARDING:
					plane.flyToDeparturePoint();
					break;
				case FLYING_BACK:
					depAirport.parkAtTransferGate();
					plane.setIs_comming_back(true);
					break;
			}
		}
	}

	public EPilotState getPilotState() {
		return pilotState;
	}

	public void setPilotState(EPilotState pilotState) {
		this.pilotState = pilotState;
	}

	public Plane getPlane() {
		return plane;
	}
	
	

}
