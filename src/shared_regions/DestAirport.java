/**
 * 
 */
package shared_regions;

import common_infrastructures.EPassengerState;
import common_infrastructures.EPilotState;
import entities.Passenger;
import entities.Pilot;
import genclass.GenericIO;
import main.SimulParams;

/**
 * @author tomasfilipe7
 *
 */
public class DestAirport 
{
	private GeneralRepos repos;
	private Passenger[] passengers;
	/**
	 * @param repos
	 */
	public DestAirport(GeneralRepos repos) {
		super();
		this.repos = repos;
		passengers = new Passenger[SimulParams.P];
	}
	
	public synchronized void announceArrival()
	{
		GenericIO.writelnString("We have arrived");
		notifyAll();
		Pilot p = (Pilot)Thread.currentThread();
		p.setPilotState(EPilotState.DEBOARDING);
		repos.reportStatus(" arrived.");
		repos.setPilotState(EPilotState.DEBOARDING);
	}
	
}
