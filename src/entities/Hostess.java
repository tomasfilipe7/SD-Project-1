/**
 * 
 */
package entities;

import java.lang.Thread.State;

import common_infrastructures.EHostessState;
import genclass.GenericIO;
import shared_regions.DepAirport;
import shared_regions.DestAirport;
import shared_regions.Plane;

/**
 * @author tomasfilipe7
 *
 */
public class Hostess extends Thread
{	
	/**
	 * Current hostess state
	 */
	private EHostessState hostessState;
	
	/**
	 * Reference to the departure airport 
	 */
	private final DepAirport depAirport;
	
	/**
	 * Reference to the plane
	 */
	private final Plane plane;
	
	/**
	 * Condition to verify the hostess life cycle
	 */
	private boolean has_finished;
	

	/**
	 * @param state
	 * @param depAirport
	 * @param plane
	 */
	public Hostess(EHostessState hostessState, DepAirport depAirport, Plane plane) {
		super();
		this.hostessState = hostessState;
		this.depAirport = depAirport;
		this.plane = plane;
		this.has_finished = false;
	}


	@Override
	public void run()
	{
		while(!has_finished)
		{
			switch(hostessState)
			{
				case WAIT_FOR_FLIGHT:
					GenericIO.writelnString("Hostess WAIT_FOR_FLIGHT (Hostess state)");
					depAirport.prepareForPassBoarding();
					break;
				case WAIT_FOR_PASSENGER:
					GenericIO.writelnString("Hostess WAIT_FOR_PASSENGER (Hostess state)");
					if(plane.isFull() || (depAirport.QueueIsEmpty() && plane.isReady()))
					{
						depAirport.informPlaneReadyToTakeOff();
					}
					else if(!plane.isFull() && !depAirport.QueueIsEmpty())
					{
						depAirport.checkDocuments();
					}
					break;
				case CHECK_PASSENGER:
					GenericIO.writelnString("Hostess CHECK_PASSENGER (Hostess state)");
					depAirport.waitForNextPassenger();
					break;
				case READY_TO_FLY:
					GenericIO.writelnString("Hostess READY_TO_FLY (Hostess state)");
					depAirport.waitForNextFlight();
					break;
			}
		}
	}

	/**
	 * Get hostess state
	 * @return hostess state
	 */
	public EHostessState getHostessState() {
		return hostessState;
	}

	/**
	 * Set hostess state
	 * @param hostessState
	 */
	public void setHostessState(EHostessState hostessState) {
		this.hostessState = hostessState;
	}
	
	
}
