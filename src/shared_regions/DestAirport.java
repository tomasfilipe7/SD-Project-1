/**
 * 
 */
package shared_regions;

import common_infrastructures.EPassengerState;
import common_infrastructures.EPilotState;
import entities.Pilot;
import genclass.GenericIO;

/**
 * @author tomasfilipe7
 *
 */
public class DestAirport 
{
	private GeneralRepos repos;
	
	/**
	 * @param repos
	 */
	public DestAirport(GeneralRepos repos) {
		super();
		this.repos = repos;
	}

	public synchronized void announceArrival()
	{
		GenericIO.writelnString("We have arrived");
		notifyAll();
		Pilot p = (Pilot)Thread.currentThread();
		p.setPilotState(EPilotState.DEBOARDING);
		repos.setPilotState(EPilotState.DEBOARDING);
	}
	
}
