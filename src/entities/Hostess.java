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
	private EHostessState hostessState;
	
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
	public Hostess(EHostessState hostessState, DepAirport depAirport, DestAirport destAirport, Plane plane) {
		super();
		this.hostessState = hostessState;
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
			int currentPassenger = 0;
			switch(hostessState)
			{
				case WAIT_FOR_FLIGHT:
					hostessState = depAirport.prepareForPassBoarding();
					break;
				case WAIT_FOR_PASSENGER:
					if(plane.isFull() || (depAirport.QueueIsEmpty() && plane.isReady()))
					{
						hostessState = depAirport.informPlaneReadyToTakeOff();
					}
					else if(!plane.isFull() && !depAirport.QueueIsEmpty())
					{
						hostessState = depAirport.checkDocuments(currentPassenger);
					}
					break;
				case CHECK_PASSENGER:
					hostessState = depAirport.waitForNextPassenger();
					break;
				case READY_TO_FLY:
					hostessState = depAirport.waitForNextFlight(); 
					break;
			}
		}
	}


	public EHostessState getHostessState() {
		return hostessState;
	}


	public void setHostessState(EHostessState hostessState) {
		this.hostessState = hostessState;
	}
	
	
}
