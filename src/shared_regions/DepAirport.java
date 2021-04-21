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
	private GeneralRepos repos;
	
	/**
	 * Passengers waiting to be called by hostess
	 */
	private boolean waiting_for_call;
	/**
	 * Hostess waiting to check documents;
	 */
	private boolean checking_documents;
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
		this.waiting_for_call = true;
		this.checking_documents = true;
		this.plane_has_arrived = false;
		this.ready_to_takeoff = false;
	}
	
	public Passenger getNextPassenger() throws MemException
	{
		return passengersQueue.read();
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
		} catch (MemException e1) {
			// TODO Auto-generated catch block
			GenericIO.writelnString("Catching writing on queue");
			e1.printStackTrace();
		}
		GenericIO.writelnString("Waiting in Queue (Passenger)");
		notifyAll();
		while(this.waiting_for_call)
		{
			try {
				wait();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				GenericIO.writelnString("Catching waiting for call");
				e.printStackTrace();
			}
		}
		p.setPassengerState(EPassengerState.IN_QUEUE);
		repos.setPassengerState(p.getPassengerId(), EPassengerState.IN_QUEUE);
		GenericIO.writelnString("Waiting in Queue After wait (Passenger)");
	}
	
	public synchronized void showDocuments()
	{
		// Implement show documents
		Passenger p = (Passenger)Thread.currentThread();
		notifyAll();
		this.checking_documents = false;
		GenericIO.writelnString("Showing Documents (Passenger)");
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
		GenericIO.writelnString("Showing Documents After wait  (Passenger)");
	}
	
	public void boardThePlane()
	{
		// Implement travel to airport
		Passenger p = (Passenger)Thread.currentThread();
		GenericIO.writelnString("Boarding the plane (Passenger)");
		try {
			Passenger.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			GenericIO.writelnString("Catching error boarding plane");
			e.printStackTrace();
		}
		p.setPassengerState(EPassengerState.IN_FLIGHT);
		repos.setPassengerState(p.getPassengerId(), EPassengerState.IN_FLIGHT);
	}

	public void parkAtTransferGate()
	{
		Pilot p = (Pilot)Thread.currentThread();
		GenericIO.writelnString("Parking plane (Pilot)");
		try {
			Pilot.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			GenericIO.writelnString("Catching error parking at transfer gate");
			e.printStackTrace();
		}
		p.setPilotState(EPilotState.AT_TRANSFER_GATE);
		repos.setPilotState(EPilotState.AT_TRANSFER_GATE);
	}
	
	public synchronized void informPlaneReadyForBoarding()
	{
		notifyAll();
		GenericIO.writelnString("informPlaneReadyForBoarding (Pilot)");
		this.plane_has_arrived = true;
		Pilot p = (Pilot)Thread.currentThread();
		p.setPilotState(EPilotState.READY_FOR_BOARDING);
		repos.setPilotState(EPilotState.READY_FOR_BOARDING);
	}
	
	public synchronized void waitForAllBoard()
	{
		Pilot p = (Pilot)Thread.currentThread();
		p.setPilotState(EPilotState.WAITING_FOR_BOARDING);
		repos.setPilotState(EPilotState.WAITING_FOR_BOARDING);
		GenericIO.writelnString("Wait for all board (Pilot)");
		while(!this.ready_to_takeoff)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				GenericIO.writelnString("Catching error waiting for all aboard");
				e.printStackTrace();
			}
		}
		GenericIO.writelnString("Wait for all board after wait(Pilot)");
		this.ready_to_takeoff = false;
	}
	
	public synchronized void prepareForPassBoarding()
	{
		GenericIO.writelnString("Prepare for pass boarding (Hostess)");
		Hostess h = (Hostess)Thread.currentThread();
		while(!plane_has_arrived)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				GenericIO.writelnString("Catched prepare for passboarding error");
				e.printStackTrace();
			}
		}
		repos.reportStatus("boarding started.");
		GenericIO.writelnString("Prepare for pass boarding After Wait (Hostess)");
		h.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
		repos.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
	}
	
	public synchronized void checkDocuments()
	{
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.CHECK_PASSENGER);	
		repos.setHostessState(EHostessState.CHECK_PASSENGER);
		notifyAll();
		this.waiting_for_call = false;
		GenericIO.writelnString("Asking for documents (Hostess)");
		while(this.checking_documents)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				GenericIO.writelnString("Catching error checking documents");
				e.printStackTrace();
			}
		}
		GenericIO.writelnString("Documents handed Hostess)");
		Passenger p;
		try {
			p = passengersQueue.read();
			p.setDocuments_validated(true);
			notifyAll();
			repos.reportStatus("passenger " + p.getPassengerId() + " checked.");
		} catch (MemException e) {
			// TODO Auto-generated catch block
			GenericIO.writelnString("Catching error validating documents");
			e.printStackTrace();
		}
		GenericIO.writelnString("Documents checked (Hostess)");
	}
	
	public synchronized void waitForNextPassenger()
	{ 
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
		repos.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
		notifyAll();
		this.waiting_for_call = true;
		GenericIO.writelnString("Waiting for next Passenger (Hostess)");
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
		GenericIO.writelnString("Waiting for next Passenger After wait(Hostess)");
	}
	
	public synchronized void informPlaneReadyToTakeOff()
	{
		notifyAll();
		GenericIO.writelnString("Plane ready to take off (Hostess)");
		this.plane_has_arrived = false;
		this.ready_to_takeoff = true;
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.READY_TO_FLY);
		repos.setHostessState(EHostessState.READY_TO_FLY);
	}
	
	public void waitForNextFlight()
	{
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_FLIGHT);
		repos.setHostessState(EHostessState.WAIT_FOR_FLIGHT);
		GenericIO.writelnString("Let's wait for next plane (Hostess)");
			
	}
	
}
