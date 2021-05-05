/**
 * 
 */
package shared_regions;

import entities.EPilotState;
import entities.Pilot;

/**
 * @author tomasfilipe7
 * @author marciapires
 *
 */
public class DestAirport 
{
	/**
	 * Reference to the general repository.
	 */
	
	private GeneralRepos repos;
	
	/**
	 * Number of passengers that have arrived. 
	 */
	
	private int pass_arrived;
	
	/**
	 * @param repos reference to the general repository.
	 */
	
	public DestAirport(GeneralRepos repos) {
		super();
		this.repos = repos;
		this.pass_arrived = 0;
		this.repos.setInPTAL(this.pass_arrived);
	}
	
	
	/**
	 * 
	 *  It is called when a passenger is arriving at the destination airport.
	 *  
	 */
	public synchronized void passengerArrived()
	{
		this.pass_arrived += 1;
		this.repos.setInPTAL(this.pass_arrived);
	}
	
	/**
	 * Operation announce arrival.
	 * 
	 * It is called by the pilot when they arrive at the destination airport. 
	 * 
	 */
	
	public synchronized void announceArrival()
	{
		Pilot p = (Pilot)Thread.currentThread();
		repos.reportStatus(" arrived.");
		p.setPilotState(EPilotState.DEBOARDING);
		repos.setPilotState(EPilotState.DEBOARDING);
		p.getPlane().setHas_arrived(true);
		
	}
	
}
