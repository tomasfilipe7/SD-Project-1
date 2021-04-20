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
	 * @param passengersQueue
	 */
	public DepAirport(int passengersQueueMax) {
		super();
		this.passengersQueue = new MemFIFO<Passenger>(passengersQueueMax);
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
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void showDocuments()
	{
		// Implement show documents
		Passenger p = (Passenger)Thread.currentThread();
		p.notify();
		try {
			p.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p.setDocuments_validated(true);
	}
	
	public void boardThePlane()
	{
		// Implement travel to airport
		Passenger p = (Passenger)Thread.currentThread();
		try {
			p.sleep((long)(1 + 10 * Math.random()));
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
		Pilot p = (Pilot)Thread.currentThread();
		p.setPilotState(EPilotState.READY_FOR_BOARDING);
	}
	
	public void waitForAllBoard()
	{
		Pilot p = (Pilot)Thread.currentThread();
		p.setPilotState(EPilotState.WAITING_FOR_BOARDING);
		try {
			p.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void prepareForPassBoarding()
	{
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
		try {
			h.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void checkDocuments(int passengerId)
	{
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.CHECK_PASSENGER);
		notify();
		try {
			h.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void waitForNextPassenger()
	{ 
		notify();
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
		try {
			h.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void informPlaneReadyToTakeOff()
	{
		notifyAll();
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.READY_TO_FLY);
	}
	
	public void waitForNextFlight()
	{
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_FLIGHT);
		try {
			h.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
