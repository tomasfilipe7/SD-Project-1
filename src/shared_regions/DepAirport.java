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
import entities.Hostess;

/**
 * @author tomasfilipe7
 *
 */
public class DepAirport 
{
	private MemFIFO<Passenger> passengersQueue;
	
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
	public DepAirport(int passengersQueueMax) {
		super();
		this.passengersQueue = new MemFIFO<Passenger>(passengersQueueMax);
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
			e1.printStackTrace();
		}
		p.setPassengerState(EPassengerState.IN_QUEUE);
		notifyAll();
		while(this.waiting_for_call)
		{
			try {
				wait();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void showDocuments()
	{
		// Implement show documents
		Passenger p = (Passenger)Thread.currentThread();
		notifyAll();
		this.checking_documents = true;
		while(!p.getDocuments_validated())
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void boardThePlane()
	{
		// Implement travel to airport
		Passenger p = (Passenger)Thread.currentThread();
		try {
			Passenger.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p.setPassengerState(EPassengerState.IN_FLIGHT);
	}

	public void parkAtTransferGate()
	{
		Pilot p = (Pilot)Thread.currentThread();
		p.setPilotState(EPilotState.AT_TRANSFER_GATE);
		try {
			Pilot.sleep((long)(1 + 10 * Math.random()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
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
	
	public void prepareForPassBoarding()
	{
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
		while(!plane_has_arrived)
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public synchronized void checkDocuments(int passengerId)
	{
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.CHECK_PASSENGER);	
		notifyAll();
		this.waiting_for_call = false;
		while(this.checking_documents)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Passenger p;
		try {
			p = passengersQueue.read();
			p.setDocuments_validated(true);
		} catch (MemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void waitForNextPassenger()
	{ 
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
		notifyAll();
		this.waiting_for_call = true;
		while(this.QueueIsEmpty())
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void informPlaneReadyToTakeOff()
	{
		notifyAll();
		this.plane_has_arrived = false;
		this.ready_to_takeoff = true;
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.READY_TO_FLY);
	}
	
	public void waitForNextFlight()
	{
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_FLIGHT);
		while(!this.plane_has_arrived)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
	}
	
}
