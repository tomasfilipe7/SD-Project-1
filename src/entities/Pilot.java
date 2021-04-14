/**
 * 
 */
package entities;

import common_infrastructures.EPilotState;
import shared_regions.DepAirport;
import shared_regions.DestAirport;
import shared_regions.Plane;

/**
 * @author tomasfilipe7
 *
 */
public class Pilot extends Thread
{
	private EPilotState state;
	
	private DepAirport depAirport;
	private DestAirport destAirport;
	private Plane plane;
	
	private boolean has_finished;
	
	/**
	 * @param state
	 * @param depAirport
	 * @param destAirport
	 * @param plane
	 */
	public Pilot(EPilotState state, DepAirport depAirport, DestAirport destAirport, Plane plane) {
		super();
		this.state = state;
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
			switch(state)
			{
				case AT_TRANSFER_GATE:
					state = depAirport.informPlaneReadyForBoarding();
					break;
				case READY_FOR_BOARDING:
					state = depAirport.waitForAllBoard();
					break;
				case WAITING_FOR_BOARDING:
					state = plane.flyToDestinationPoint();
					break;
				case FLYING_FORWARD:
					state = destAirport.announceArrival();
					break;
				case DEBOARDING:
					if(plane.isEmpty())
					{
						state = plane.flyToDeparturePoint();
					}
					break;
				case FLYING_BACK:
					state = depAirport.parkAtTransferGate();
					break;
			}
		}
	}

}
