/**
 * 
 */
package entities;
/**
 * @author tomasfilipe7
 * @author marciapires
 *
 */
public enum EPilotState 
{
	/**
	 * The pilot is at the transfer gate
	 */
	
	AT_TRANSFER_GATE,
	
	/**
	 * The pilot informs the hostess that the plane is ready for boarding
	 */
	
	READY_FOR_BOARDING,
	
	/**
	 * The pilot is waiting for the passengers to board the plane
	 */
	
	WAITING_FOR_BOARDING,
	
	/**
	 * Flying to the destination point 
	 */
	
	FLYING_FORWARD,
	
	/**
	 * The pilot is waiting for the passangers to deboard the plane
	 */
	
	DEBOARDING,
	
	/**
	 * Flying back to the departure point 
	 */
	FLYING_BACK
}
