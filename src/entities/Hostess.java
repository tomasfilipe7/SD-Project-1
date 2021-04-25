/**
 * 
 */
package entities;

import common_infrastructures.MemException;
import genclass.GenericIO;
import shared_regions.DepAirport;
import shared_regions.Plane;

/**
 * 
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
	 * Hostess instantiation
	 * 
	 * @param state reference to hostess state
	 * @param depAirport reference to department airport
	 * @param plane reference to the plane
	 */
	public Hostess(EHostessState hostessState, DepAirport depAirport, Plane plane) {
		super();
		this.hostessState = hostessState;
		this.depAirport = depAirport;
		this.plane = plane;
		this.has_finished = false;
	}


	@Override
	/**
	 * Hostess life cycle
	 */
	public void run()	
	{
		while(!has_finished)																	// Condition to check the end of Hostess life cycle
		{
			switch(hostessState)																// Check hostess State
			{
				case WAIT_FOR_FLIGHT:															// State: Waiting for next flight
					if(depAirport.jobDone())													// If there are no passengers left, the hostess ends her life cycle
					{
						has_finished = true;
						break;
					}
					depAirport.prepareForPassBoarding();										// Waiting for pilot to notify her
					break;
				case WAIT_FOR_PASSENGER:														// State: Wait for passenger
																								// Check if plane is ready to depart to destination.(If plane is full, or plane is at minimum capacity and airport queue is empty or there's left then minimum capacity left)
					if(plane.isFull() || (depAirport.getPassengers_left_on_queue() <= 0 && depAirport.getPassengers_admitted() >= plane.getMin_passengers()) || (depAirport.getPassengersLeft() == 0 && depAirport.QueueIsEmpty()))
					{
						depAirport.informPlaneReadyToTakeOff();									// Inform pilot that the plane is ready to take off
					}
					else if(!(depAirport.getPassengers_admitted() >= plane.getMax_passengers()) && !depAirport.QueueIsEmpty())						// If there are still passengers in queue and the plane is not full
					{
						try {
							depAirport.checkDocuments();										// Check if the documents of the passenger at the start of the queue are valid 
						} catch (MemException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				case CHECK_PASSENGER:															// State: Check Passenger
					depAirport.waitForNextPassenger();											// Wait for the next passenger on the Queue
					break;
				case READY_TO_FLY:																// State: Ready to Fly
					depAirport.waitForNextFlight();												// Wait for the next flight to arrive
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
