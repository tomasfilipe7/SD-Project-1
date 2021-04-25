/**
 * 
 */
package shared_regions;
import common_infrastructures.MemException;
import common_infrastructures.MemFIFO;
import entities.Passenger;
import entities.Pilot;
import entities.EHostessState;
import entities.EPassengerState;
import entities.EPilotState;
import entities.Hostess;

/**
 * @author tomasfilipe7
 * @author marciapires
 *
 */
public class DepAirport 
{
	/**
	 * Number of total passengers left to arrive at airport
	 */
	private int passengers_left;
	/**
	 * Passengers Queue of airport
	 */
	private MemFIFO<Passenger> passengersQueue;
	/**
	 * Number of passengers in Queue
	 */
	private int npassengersQueue;
	/**
	 * Reference to the General Repository
	 */
	private GeneralRepos repos;
	/**
	 * Number of passengers admitted per flight
	 */
	private int passengers_admitted;
	/**
	 * Numbers of passengers left on Queue after admitting the next passenger
	 */
	private int passengers_left_on_queue;
	
	/**
	 * If all passengers have validated and boarded the plane
	 */
	private boolean ready_to_takeoff;
	/**
	 * If the plane is at the airport
	 */
	private boolean plane_has_arrived;

	/**
	 * Department Airport instantiation
	 * 
	 * @param passengersQueueMax reference to the total of passengers
	 * @param repos reference to the General Repository
	 */
	public DepAirport(int passengersQueueMax, GeneralRepos repos) {
		super();
		this.passengersQueue = new MemFIFO<Passenger>(passengersQueueMax);
		this.repos = repos;
		this.plane_has_arrived = false;
		this.ready_to_takeoff = false;
		this.npassengersQueue = 0;
		this.passengers_left = passengersQueueMax;
		this.passengers_admitted = 0;
		this.passengers_left_on_queue = 0;
		
	}
	
	/**
	 * Getter of passengers_left
	 * @return passengers_left
	 */
	public int getPassengersLeft() {
		return passengers_left;
	}
	
	/**
	 * Getter of npassengersQueue
	 * @return npassengersQueue
	 */
	public int getNpassengersQueue() {
		return npassengersQueue;
	}

	/**
	 * Check if airport queue is empty
	 * @return passengersQueue.isEmpty()
	 */
	public boolean QueueIsEmpty()
	{
		return passengersQueue.isEmpty();
	}
	
	/**
	 * Getter of passengers_admitted
	 * @return passengers_admitted
	 */
	public int getPassengers_admitted() {
		return passengers_admitted;
	}

	/**
	 * Getter of passengers_left_on_queue
	 * @return passengers_left_on_queue
	 */
	public int getPassengers_left_on_queue() {
		return passengers_left_on_queue;
	}
	
	public boolean jobDone()
	{
		return this.passengers_left == 0;
	}
	/**
	 * Operation wait in queue.
	 * 
	 * Called by the passengers when arriving the airport and start waiting in the queue. 
	 * It will wait for a notify by the hostess to check the documents.
	 * 
	 */
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
			e1.printStackTrace();
		}
		p.setPassengerState(EPassengerState.IN_QUEUE);
		repos.setPassengerState(p.getPassengerId(), EPassengerState.IN_QUEUE);
		notifyAll();
		
		while(!p.isTo_be_called())
		{
			try {
				wait();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Operation show documents
	 * 
	 * It is called by the Passenger and will notify the hostess that he is ready to show the documents. He will then wait until the validation is completed.
	 * 
	 */
	public synchronized void showDocuments()
	{
		// Implement show documents
		Passenger p = (Passenger)Thread.currentThread();
		notifyAll();
		while(!p.getDocuments_validated())
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	/**
	 * Operation board the plane
	 * 
	 * It is called by the passengers after his documents are validated. He will then board the plane 
	 * 
	 */
	
	public synchronized void boardThePlane()
	{
		// Implement travel to airport
		Passenger p = (Passenger)Thread.currentThread();
		try {
			p.getPlane().enterPassenger();
		} catch (MemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.passengers_left -= 1;
		repos.setPassengerState(p.getPassengerId(), EPassengerState.IN_FLIGHT);
		p.setPassengerState(EPassengerState.IN_FLIGHT);
	}
	
	/**
	 * Operation park at transfer gate
	 * 
	 * It is called by the Pilot when he arrives at the airport and parks the plane at transfer gate
	 * 
	 */
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
	
	/**
	 * Operation inform plane ready for boarding
	 * 
	 * It is called by the Pilot, when the plane is ready for boarding. He will notify the Hostess, and she will wake up and start calling the passengers
	 */
	public synchronized void informPlaneReadyForBoarding()
	{
		Pilot p = (Pilot)Thread.currentThread();
		repos.reportStatus("boarding started.");
		p.setPilotState(EPilotState.READY_FOR_BOARDING);
		repos.setPilotState(EPilotState.READY_FOR_BOARDING);
		this.plane_has_arrived = true;
		notifyAll();
		
	}
	
	/**
	 * Operation wait for all board
	 * 
	 * It is called by the pilot while he is waiting until everyone is on the plane, so he can flight to the destination.
	 */
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
	
	/**
	 * Operation prepare for pass boarding
	 * 
	 * It is called by the Hostess. She will wait until the pilot notifies her when the plane is ready for pass boarding.
	 */
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
	
	/**
	 * Operation check documents
	 * 
	 * It is called by the Hostess when she is checking the passengers documents. 
	 * She will first notify them to show their documents and then she will wait for them to show and validate the passenger's documents
	 * 
	 * @throws MemException
	 */
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
		p.setDocuments_validated(true);
		this.npassengersQueue -= 1;
		this.passengers_left_on_queue = this.npassengersQueue;
		repos.setInQueue(this.npassengersQueue);
		this.passengers_admitted += 1;
		h.setHostessState(EHostessState.CHECK_PASSENGER);	
		repos.setHostessState(EHostessState.CHECK_PASSENGER);
	}
	
	/**
	 * Operation wait for next passenger
	 * 
	 * It is called by the Hostess when she is waiting for the next passenger to arrive at the queue
	 * 
	 */
	public synchronized void waitForNextPassenger()
	{ 
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
		repos.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
		notifyAll();
		while(this.QueueIsEmpty() && this.passengers_left > 0)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Operation inform plane ready to take off
	 * 
	 * It is called by the Hostess when all the passengers are aboard the plane. She will then notify the pilot to depart
	 */
	public synchronized void informPlaneReadyToTakeOff()
	{
		this.ready_to_takeoff = true;
		notifyAll();
		this.plane_has_arrived = false;
		this.passengers_admitted = 0;
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.READY_TO_FLY);
		repos.setHostessState(EHostessState.READY_TO_FLY);
	}
	
	/**
	 * Operation wait for next flight
	 * 
	 * It is called by the Hostess while waiting for the next flight to arrive
	 */
	public synchronized void waitForNextFlight()
	{
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_FLIGHT);
		repos.setHostessState(EHostessState.WAIT_FOR_FLIGHT);
			
	}
	
}
