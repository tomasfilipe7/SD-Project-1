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
	private int passengers_left;
	private MemFIFO<Passenger> passengersQueue;
	private int npassengersQueue;
	private GeneralRepos repos;
	private int passengers_admitted;
	private int passengers_left_on_queue;
	
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
		this.passengers_left = passengersQueueMax;
		this.passengers_admitted = 0;
		this.passengers_left_on_queue = 0;
		
	}
	
	
	public int getPassengersLeft() {
		return passengers_left;
	}
	

	public int getNpassengersQueue() {
		return npassengersQueue;
	}


	public boolean QueueIsEmpty()
	{
		return passengersQueue.isEmpty();
	}
	
	public int getPassengers_admitted() {
		return passengers_admitted;
	}


	public int getPassengers_left_on_queue() {
		return passengers_left_on_queue;
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
			e1.printStackTrace();
		}
		p.setPassengerState(EPassengerState.IN_QUEUE);
		repos.setPassengerState(p.getPassengerId(), EPassengerState.IN_QUEUE);
		notifyAll();
		
		GenericIO.writelnString("Passenger " + p.getPassengerId() + " - 'I will wait in queue until Im called.'");
		while(!p.isTo_be_called())
		{
			try {
				wait();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		GenericIO.writelnString("Passenger " + p.getPassengerId() + "' - My turn to show my documents.'");
	}
	
	public synchronized void showDocuments()
	{
		// Implement show documents
		Passenger p = (Passenger)Thread.currentThread();
		GenericIO.writelnString("Passenger " + p.getPassengerId() + " - 'Im going to show my documents and wait.'");
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
		GenericIO.writelnString("Passenger " + p.getPassengerId() + " - *Showed documents and waited.*");
		
	}
	
	public synchronized void boardThePlane()
	{
		// Implement travel to airport
		Passenger p = (Passenger)Thread.currentThread();
		GenericIO.writelnString("Passenger " + p.getPassengerId() + " - 'Im leaving the airport and boarding the plane.'");
		try {
			p.getPlane().enterPassenger();
		} catch (MemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.passengers_left -= 1;
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
		GenericIO.writelnString("Pilot - 'Im parking the plane.");
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
		GenericIO.writelnString("Pilot - 'Hostess, you can start checking passengers.");
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
		GenericIO.writelnString("Pilot - 'Im waiting for everyone aboard.");
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
			GenericIO.writelnString("Pilot - 'YEEEEYY!!!!!!!!!!!!!!!!.");
		}
		this.ready_to_takeoff = false;
		GenericIO.writelnString("Pilot - 'AHH HOYYY EVERYONE ON BOARD!!!!!!!!!!!!!!!!.");
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
		GenericIO.writelnString("Hostess - 'Alright, Pilot. Ill wait for passengers to come.");
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
		GenericIO.writelnString("Hostess: - Sir " + p.getPassengerId() + ", your documents are valid.");
		p.setDocuments_validated(true);
		this.npassengersQueue -= 1;
		this.passengers_left_on_queue = this.npassengersQueue;
		repos.setInQueue(this.npassengersQueue);
		this.passengers_admitted += 1;
//		notifyAll();
		
	}
	
	public synchronized void waitForNextPassenger()
	{ 
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
		repos.setHostessState(EHostessState.WAIT_FOR_PASSENGER);
		notifyAll();
		GenericIO.writelnString("Hostess: - Next passenger please.");
		while(this.QueueIsEmpty() && this.passengers_left > 0)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				GenericIO.writelnString("Catching error waiting for next passenger");
				e.printStackTrace();
			}
			GenericIO.writelnString("Hostess - Preso aqui");
		}
		GenericIO.writelnString("Hostess - Saiu daqui");
	}
	
	public synchronized void informPlaneReadyToTakeOff()
	{
		GenericIO.writelnString("Hostess - 'Pilot, everything is ready.'");
		this.ready_to_takeoff = true;
		notifyAll();
		this.plane_has_arrived = false;
		this.passengers_admitted = 0;
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.READY_TO_FLY);
		repos.setHostessState(EHostessState.READY_TO_FLY);
	}
	
	public synchronized void waitForNextFlight()
	{
		GenericIO.writelnString("Hostess - 'And now I will wait for the next flight.'");
		Hostess h = (Hostess)Thread.currentThread();
		h.setHostessState(EHostessState.WAIT_FOR_FLIGHT);
		repos.setHostessState(EHostessState.WAIT_FOR_FLIGHT);
			
	}
	
}
