/**
 * 
 */
package shared_regions;

import common_infrastructures.EPassengerState;
import common_infrastructures.EPilotState;
import entities.Pilot;

/**
 * @author tomasfilipe7
 *
 */
public class DestAirport 
{
	
	public void announceArrival()
	{
		notifyAll();
		Pilot p = (Pilot)Thread.currentThread();
		p.setPilotState(EPilotState.DEBOARDING);
	}
	
}
