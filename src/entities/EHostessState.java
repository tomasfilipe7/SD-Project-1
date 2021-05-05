/**
 * 
 */
package entities;
/**
 * @author tomasfilipe7
 * @author marciapires
 *
 */

public enum EHostessState 
{
	/**
	 * The hostess is waiting for the flight
	 */
	WAIT_FOR_FLIGHT,
	
	/**
	 * The hostess is waiting for the next passenger on the queue
	 */
	WAIT_FOR_PASSENGER,
	
	/**
	 * Check the documents of the passenger
	 */
	CHECK_PASSENGER,
	
	/*
	 * Signaling the pilot that the plane is ready 
	 */
	READY_TO_FLY
}
