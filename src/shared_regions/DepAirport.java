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
	public synchronized boolean travelToAirport() throws MemException
	{
		// Implement travel to airport
		Passenger passenger = (Passenger) Thread.currentThread();
		passengersQueue.write(passenger);
		return true;
	}
	
	public EPassengerState waitInQueue()
	{
		// Implement wait in queue
		return EPassengerState.IN_QUEUE;
	}
	
	public void showDocuments()
	{
		// Implement show documents
	}
	
	public EPassengerState boardThePlane()
	{
		// Implement board the plane
		return EPassengerState.IN_FLIGHT;
	}

	public EPilotState parkAtTransferGate()
	{
		// Implement park at transfer gate
		return EPilotState.AT_TRANSFER_GATE;
	}
	
	public EPilotState informPlaneReadyForBoarding()
	{
		// Implement inform plane ready for Boarding
		return EPilotState.READY_FOR_BOARDING;
	}
	
	public EPilotState waitForAllBoard()
	{
		// Implement wait for all board
		return EPilotState.WAITING_FOR_BOARDING;
	}
	
	public EHostessState prepareForPassBoarding()
	{
		// Implement prepare for pass boarding
		return EHostessState.WAIT_FOR_PASSENGER;
	}
	
	public EHostessState checkDocuments()
	{
		// Implement check documents
		return EHostessState.CHECK_PASSENGER;
	}
	
	public EHostessState waitForNextPassenger()
	{ 
		// implement wait for next passenger 
		return EHostessState.WAIT_FOR_PASSENGER;
	}
	
	public EHostessState informPlaneReadyToTakeOff()
	{
		// Implement inform plane ready to take off
		return EHostessState.READY_TO_FLY;
	}
	
	public EHostessState waitForNextFlight()
	{
		// Implement wait for next flight
		return EHostessState.WAIT_FOR_FLIGHT;
	}
	
}
