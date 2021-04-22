/**
 * 
 */
package shared_regions;
import common_infrastructures.EHostessState;
import common_infrastructures.EPassengerState;
import common_infrastructures.EPilotState;
import common_infrastructures.MemException;
import common_infrastructures.MemFIFO;
import entities.Passenger;
import entities.Pilot;
import genclass.GenericIO;
import entities.Hostess;

/**
 * @author tomasfilipe7
 *
 */
public class DepAirport 
{
	private MemFIFO<Passenger> passengersQueue;
	private int npassengersQueue;
	private GeneralRepos repos;
	
	/**
	 * Hostess waiting to check documents;
	 */
	private boolean ready_to_takeoff;
	/**
	 * If the plane is at the airport
	 */
	private boolean plane_has_arrived;
	/**
	 * @param passengersQueue
	 */
	public DepAirport(int passengersQueueMax, GeneralRepos repos) {
		super();
		this.passengersQueue = new MemFIFO<Passenger>(passengersQueueMax);
		this.repos = repos;
		this.plane_has_arrived = false;
		this.ready_to_takeoff = false;
		this.npassengersQueue = 0;
	}
	
	public boolean QueueIsEmpty()
	{
		return passengersQueue.isEmpty();
	}
	
	public synchronized void waitInQueue()
	{
		// Implement wait in queue
		Passenger p = (Passenger)Thread.currentThread();
		try {
			passengersQueue.write(p);
			this.npassengersQueue += 1;
			repos.setInQueue(this.npassengersQueue);
		} catch (MemException e1) {
			// TODO Auto-generated catch block
			GenericIO.writelnString("Catching writing on queue");
			e1.printStackTrace();
		}
		notifyAll();
		p.setPassengerState(EPassengerState.IN_QUEUE);
		repos.setPassengerState(p.getPassengerId(), EPassengerState.IN_QUEUE);
		
		while(!p.isTo_be_called())
		{
			try {
				wait();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				GenericIO.writelnString("Catching waiting for call");
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void showDocuments()
	{
		// Implement show documents
		GenericIO.writelnString("What is happening?");
		Passenger p = (Passenger)Thread.currentThread();
		notifyAll();
		while(!p.getDocuments_validated())
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				GenericIO.writelnString("Catching error getting documents validated");
				e.printStackTrace();
			}
		}
		GenericIO.writelnString("Show documents after wait");
		
	}
	
	public synchronized void boardThePlane()
	{
		// Implement travel to airport
		Passenger p = (Passenger)Thread.currentThread();
		this.npassengersQueue -= 1;
		repos.setInQueue(this.npassengersQueue);
		p.setPassengerState(EPassengerState.IN_FLIGHT);
		repos.setPassengerState(p.getPassengerId(), EPassengerState.IN_FLIGHT);
//		try {
//			Passenger.sleep((long)(1 + 10 * Math.random()));
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

	public synchronized void parkAtTransferGate()
	{
		Pilot p = (Pilot)Thread.currentThread();
		try {
			Pilot.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p.setPilotState(EPilotState.AT_TRANSFER_GATE);
		repos.setPilotState(EPilotState.AT_TRANSFER_GATE);
	}
	
	public synchronized void informPlaneReadyForBoarding()
	{
		Pilot p = (Pilot)Thread.currentThread();
		repos.reportStatus("boarding started.");
		p.setPilotState(EPilotState.READY_FOR_BOARDING);
		repos.setPilotState(EPilotState.READY_FOR_BOARDING);
		this.plane_has_arrived = true;
		notifyAll();
		
	}
	
	public synchronized void waitForAllBoard()
	{
		Pilot p = (Pilot)Thread.currentThread();
		p.setPilotState(EPilotState.WAITING_FOR_BOARDING);
		repos.setPilotState(EPilotState.WAITING_FOR_BOARDING);
		while(!this.ready_to_takeoff)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.ready_to_takeoff = false;
	}
	
	public synchronized void prepareForPassBoarding()
	{
		Hostess h = (Hostess)Thread.currentThread();
		while(!plane_has_arrived)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		h.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
		repos.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
	}
	
	public synchronized void checkDocuments() throws MemException
	{
		Hostess h = (Hostess)Thread.currentThread();
		notifyAll();
		
		Passenger p;
		p = passengersQueue.read();
		p.setTo_be_called(true);		
		while(p.getPassengerState() != EPassengerState.IN_QUEUE)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		repos.reportStatus("passenger " + p.getPassengerId() + " checked.");
		h.setHostessState(EHostessState.CHECK_PASSENGER);	
		repos.setHostessState(EHostessState.CHECK_PASSENGER);
		GenericIO.writelnString("Sir, your documents are valid.");
		p.setDocuments_validated(true);
//		notifyAll();
		
	}
	
	public synchronized void waitForNextPassenger()
	{ 
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
		repos.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
		notifyAll();
		while(this.QueueIsEmpty())
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				GenericIO.writelnString("Catching error waiting for next passenger");
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void informPlaneReadyToTakeOff()
	{
		GenericIO.writelnString("Plane, you can go now");
		notifyAll();
		this.plane_has_arrived = false;
		this.ready_to_takeoff = true;
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.READY_TO_FLY);
		repos.setHostessState(EHostessState.READY_TO_FLY);
	}
	
	public synchronized void waitForNextFlight()
	{
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_FLIGHT);
		repos.setHostessState(EHostessState.WAIT_FOR_FLIGHT);
			
	}
	
}
