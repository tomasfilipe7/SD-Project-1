/**
 * 
 */
package entities;

/**
 * @author tomasfilipe7
 * @author marciapires
 *
 */
public enum EPassengerState 
{
	/*
	 * The passenger is traveling to the airport
	 */
	GOING_TO_AIRPORT,
	
	/*
	 * The passenger is waiting on the queue to get his documents validated
	 */
	IN_QUEUE,
	
	/*
	 * Currently on the plane, waiting for the flight to reach it's destination
	 */
	IN_FLIGHT,
	
	/*
	 * Arrived at the destination
	 */
	AT_DESTINATION
}
