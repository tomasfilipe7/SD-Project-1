/**
 * 
 */
package entities;

import java.lang.Thread.State;

import common_infrastructures.EHostessState;
import common_infrastructures.MemException;
import genclass.GenericIO;
import shared_regions.DepAirport;
import shared_regions.DestAirport;
import shared_regions.Plane;

/**
 * @author tomasfilipe7
 * @author marciapires
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
					if(plane.isIs_comming_back() && depAirport.QueueIsEmpty())
					{
						has_finished = true;
						break;
					}
					depAirport.prepareForPassBoarding();
					break;
				case WAIT_FOR_PASSENGER:
					GenericIO.writelnString("Hostess: 'Passengers on plane: + " + depAirport.getPassengers_admitted() +  ".'");
					GenericIO.writelnString("#################################");
					GenericIO.writelnString("IsEmpty: " + depAirport.QueueIsEmpty() );
					GenericIO.writelnString("Num: " + depAirport.getNpassengersQueue() );
					GenericIO.writelnString("Num 2!!: " + depAirport.getPassengers_left_on_queue() );
					GenericIO.writelnString("Admitted: " + depAirport.getPassengers_admitted());
					GenericIO.writelnString("On Plane: " + plane.getCurrentPassengers());
					GenericIO.writelnString("#################################");
					if(depAirport.getPassengers_admitted() >= plane.getMax_passengers() || (depAirport.getPassengers_left_on_queue() <= 0 && depAirport.getPassengers_admitted() >= plane.getMin_passengers()) || (depAirport.getPassengersLeft() == 0 && depAirport.QueueIsEmpty()))
					{
						GenericIO.writelnString("Hostess: 'All set, lets inform the plane.'");
						depAirport.informPlaneReadyToTakeOff();
					}
					else if(!plane.isFull() && !depAirport.QueueIsEmpty())
					{
						try {
							depAirport.checkDocuments();
						} catch (MemException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				case CHECK_PASSENGER:
					depAirport.waitForNextPassenger();
					break;
				case READY_TO_FLY:
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
