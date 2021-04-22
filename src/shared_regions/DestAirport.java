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
	private int pass_arrived;
	/**
	 * @param repos
	 */
	public DestAirport(GeneralRepos repos) {
		super();
		this.repos = repos;
		passengers = new Passenger[SimulParams.P];
		this.pass_arrived = 0;
	}
	
	public synchronized void passengerArrived()
	{
		this.pass_arrived += 1;
		this.repos.setInPTAL(this.pass_arrived);
	}
	public synchronized void announceArrival()
	{
		GenericIO.writelnString("Announce Arrival");
		Pilot p = (Pilot)Thread.currentThread();
		repos.reportStatus(" arrived.");
		p.setPilotState(EPilotState.DEBOARDING);
		repos.setPilotState(EPilotState.DEBOARDING);
		p.getPlane().setHas_arrived(true);
		notifyAll();
	}
	
}
