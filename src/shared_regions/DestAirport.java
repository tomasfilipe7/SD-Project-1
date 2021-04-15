/**
 * 
 */
package shared_regions;

import common_infrastructures.EPassengerState;
import common_infrastructures.EPilotState;

/**
 * @author tomasfilipe7
 *
 */
public class DestAirport 
{
	public EPassengerState leaveThePlane(Plane plane)
	{
		// Implement leave the plane
		return EPassengerState.AT_DESTINATION;
	}
	
	public EPilotState announceArrival()
	{
		// Implement announce arrival
		return EPilotState.DEBOARDING;
	}
	
}
