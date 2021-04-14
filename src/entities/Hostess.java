/**
 * 
 */
package entities;

import java.lang.Thread.State;

import common_infrastructures.EHostessState;
import shared_regions.DepAirport;
import shared_regions.DestAirport;
import shared_regions.Plane;

/**
 * @author tomasfilipe7
 *
 */
public class Hostess extends Thread
{
	private EHostessState state;
	
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
	public Hostess(EHostessState state, DepAirport depAirport, DestAirport destAirport, Plane plane) {
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
				case WAIT_FOR_FLIGHT:
					state = depAirport.prepareForPassBoarding();
					break;
				case WAIT_FOR_PASSENGER:
					if(plane.isFull() || (depAirport.QueueIsEmpty() && plane.isReady()))
					{
						state = depAirport.informPlaneReadyToTakeOff();
					}
					else if(!plane.isFull() && !depAirport.QueueIsEmpty())
					{
						state = depAirport.checkDocuments();
					}
					break;
				case CHECK_PASSENGER:
					state = depAirport.waitForNextPassenger();
					break;
				case READY_TO_FLY:
					state = depAirport.waitForNextFlight(); 
					break;
			}
		}
	}
}
