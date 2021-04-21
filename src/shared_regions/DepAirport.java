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
		GenericIO.writelnString("Waiting in Queue");
		p.setPassengerState(EPassengerState.IN_QUEUE);
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
		GenericIO.writelnString("Was called");
	}
	
	public void showDocuments()
	{
		// Implement show documents
		Passenger p = (Passenger)Thread.currentThread();
		notifyAll();
		this.checking_documents = true;
		GenericIO.writelnString("Showing Documents");
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
		GenericIO.writelnString("Documents Showed");	
	}
	
	public void boardThePlane()
	{
		// Implement travel to airport
		Passenger p = (Passenger)Thread.currentThread();
		GenericIO.writelnString("Boarding the plane");
		try {
			Passenger.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			GenericIO.writelnString("Catching error boarding plane");
			e.printStackTrace();
		}
		GenericIO.writelnString("Plane boarded");
		p.setPassengerState(EPassengerState.IN_FLIGHT);
	}

	public void parkAtTransferGate()
	{
		Pilot p = (Pilot)Thread.currentThread();
		GenericIO.writelnString("Parking plane");
		try {
			Pilot.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			GenericIO.writelnString("Catching error parking at transfer gate");
			e.printStackTrace();
		}
		GenericIO.writelnString("Plane parked");
		p.setPilotState(EPilotState.AT_TRANSFER_GATE);
	}
	
	public void informPlaneReadyForBoarding()
	{
		notifyAll();
		this.plane_has_arrived = true;
		Pilot p = (Pilot)Thread.currentThread();
		p.setPilotState(EPilotState.READY_FOR_BOARDING);
	}
	
	public void waitForAllBoard()
	{
		Pilot p = (Pilot)Thread.currentThread();
		p.setPilotState(EPilotState.WAITING_FOR_BOARDING);
		GenericIO.writelnString("Waiting for passengers to board");
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
		GenericIO.writelnString("All aboard!");
		this.ready_to_takeoff = false;
	}
	
	public void prepareForPassBoarding()
	{
		GenericIO.writelnString("Waiting for plane to arrive");
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
		GenericIO.writelnString("Plane arrived");
		h.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
	}
	
	public synchronized void checkDocuments()
	{
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.CHECK_PASSENGER);	
		notifyAll();
		this.waiting_for_call = false;
		GenericIO.writelnString("Asking for documents");
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
		GenericIO.writelnString("Documents handed");
		Passenger p;
		try {
			p = passengersQueue.read();
			p.setDocuments_validated(true);
			notifyAll();
		} catch (MemException e) {
			// TODO Auto-generated catch block
			GenericIO.writelnString("Catching error validating documents");
			e.printStackTrace();
		}
		GenericIO.writelnString("Documents checked");
	}
	
	public void waitForNextPassenger()
	{ 
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
		notifyAll();
		this.waiting_for_call = true;
		GenericIO.writelnString("Waiting for next Passenger");
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
		GenericIO.writelnString("NextPassengerArrived");
	}
	
	public void informPlaneReadyToTakeOff()
	{
		notifyAll();
		GenericIO.writelnString("Plane ready to take off");
		this.plane_has_arrived = false;
		this.ready_to_takeoff = true;
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.READY_TO_FLY);
	}
	
	public void waitForNextFlight()
	{
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_FLIGHT);
		GenericIO.writelnString("Let's wait for next plane");
			
	}
	
}
