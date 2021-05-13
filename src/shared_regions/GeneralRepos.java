package shared_regions;

import java.util.Objects;

import common_infrastructures.*;
import entities.EHostessState;
import entities.EPassengerState;
import entities.EPilotState;
import main.*;
import genclass.GenericIO;
import genclass.TextFile;

/**
 * @author tomasfilipe7
 * @author marciapires
 *
 */

/**
 * General Repository.
 * 
 * Responsible to keep the visible internal state of the problem and to provide means for it to be printed in the logging file.
 * */

public class GeneralRepos {
	
	/**
	 * Name of the logging file
	 */
	private final String fileName;
	
	
	/**
	 * State of the Passengers
	 */
	private EPassengerState [] passengerState;
	
	/**
	 * State of the Hostess
	 */
	private EHostessState hostessState;
	
	/**
	 * State of the Pilot
	 */
	private EPilotState pilotState;
	private MemFIFO<Integer> finalStatistics_passengers;
	private int inQueue = 0;
	private int inFlight = 0;
	private int inPTAL = 0;
	private int flightNum = 1;
	
	
	/**
	 * Instantiation of a general repository object
	 * 
	 * @param fileName name of the logging file
	 */
	
	public GeneralRepos(String fileName) {
		if ((fileName == null) || Objects.equals(fileName, "")) {
			this.fileName = "logger";
		} 
		else {
			this.fileName = fileName;
		}
		
		hostessState = EHostessState.WAIT_FOR_FLIGHT;
		pilotState = EPilotState.AT_TRANSFER_GATE;
		passengerState = new EPassengerState[SimulParams.P];
		for(int i = 0; i < SimulParams.P; i++) {
			passengerState[i] = EPassengerState.GOING_TO_AIRPORT;
		}
		this.finalStatistics_passengers = new MemFIFO<Integer>(SimulParams.P);
		reportInitialStatus();
		
	}
	
	/**
	 * Set passenger state.
	 * 
	 * @param id passenger id
	 * @param state passenger state
	 */
	
	public synchronized void setPassengerState(int id, EPassengerState state) {
		passengerState[id] = state;
		reportStatus();
	}
	
	/**
	 * Set hostess state.
	 * 
	 * @param state hostess state
	 */
	
	public synchronized void setHostessState(EHostessState state) {
		hostessState = state;
		reportStatus();
	}
	
	/**
	 * Set pilot state.
	 * 
	 * @param state pilot state
	 */
	
	public synchronized void setPilotState(EPilotState state) {
		pilotState = state;
		reportStatus();
	}
	
	/**
	 * Set number of flights.
	 * 
	 *  @param flightNum flight number
	 */
	
	public void setFlightNum(int flightNum) {
		this.flightNum = flightNum;
	}
	
	/**
	 * Set number of passengers in queue.
	 * 
	 *  @param inQueue in queue
	 */
	
	public void setInQueue(int inQueue) {
		this.inQueue = inQueue;
	}
	
	/**
	 * Set number of passengers in flight.
	 * 
	 *  @param inFlight in flight
	 */

	public void setInFlight(int inFlight) {
		this.inFlight = inFlight;
	}
	
	/**
	 * Set number of passengers at destination.
	 * 
	 *  @param inPTAL passengers at destination
	 */

	public void setInPTAL(int inPTAL) {
		this.inPTAL = inPTAL;
	}
	
	/**
	 * Get number of passengers in queue.
	 * 
	 *  @return in queue
	 */
	
	public int getInQueue() {
		return inQueue;
	}
	
	/**
	 * Get number of passengers at destination 
	 * 
	 * @return in PTAL
	 */

	public int getInPTAL() {
		return inPTAL;
	}
	
	/**
	 * Get number of flights
	 * 
	 *  @return flight number
	 */

	public int getFlightNum() {
		return flightNum;
	}

	/**
	 * Write the header to the logging file.
	 * 
	 * The pilot is at the transfer gate, the hostess is waiting for the next flight and the passengers are going to the airport.
	 * Internal operation.
	 * 
	 */
	
	private void reportInitialStatus() {
		TextFile log = new TextFile();				// Instantiation of a text file handler
		
		if(!log.openForWriting(".", fileName)) {
			GenericIO.writelnString("The operation of creating the file " + fileName + " failed!");
			System.exit(1);
		}
		log.writelnString("             Airlift - Description of the internal state");
		log.writelnString("PT    HT    P00   P01   P02   P03   P04   P05   P06   P07   P08   P09   P10   P11   P12   P13   P14   P15   P16   P17   P18   P19   P20   InQ   InF   PTAL");
		
		if(!log.close()) {
			GenericIO.writelnString("The operation of closing the file " + fileName + " failed!");
			System.exit(1);
		}
		
		reportStatus();
		log.writelnString();
	}
	
	/**
	 * Update passengers statistics.
	 * 
	 */
	
	public synchronized void updateStatistics(int num_passengers)
	{
		try {
			this.finalStatistics_passengers.write(num_passengers);
		} catch (MemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Write final statistics at the end of the logging file.
	 * 
	 * Airlift sum up with all the flights and how many each passengers each of them transported.
	 * 
	 */
	
	public synchronized void writeStatistics()
	{
		TextFile log = new TextFile();		
		if(!log.openForAppending(".", fileName)) {
			GenericIO.writelnString("The operation of opening for appending the file" + fileName + " failed!");
			System.exit(1);
		}
		
		log.writelnString();
		log.writelnString("Airlift sum up:");
		int i = 1;
		while(!this.finalStatistics_passengers.isEmpty())
		{
			try {
				log.writeString("Flight " + i + " transported " + this.finalStatistics_passengers.read() + " passengers");
			} catch (MemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(this.finalStatistics_passengers.isEmpty())
			{
				log.writeString(".");
			}
			else
			{
				log.writelnString();
			}
			i += 1;
		}
		
		
		if(!log.close()) {
			GenericIO.writelnString("The operation of closing the file " + fileName + " failed");
			System.exit(1);
		}
	}
	
	/**
	 * Write different flight conditions at the logging file during the simulation. 
	 */
	
	public synchronized void reportStatus(String condition)
	{
		TextFile log = new TextFile();
		String lineStatus = "";
		
		if(!log.openForAppending(".", fileName)) {
			GenericIO.writelnString("The operation of opening for appending the file" + fileName + " failed!");
			System.exit(1);
		}
		
		lineStatus += "Flight " + this.flightNum + ": " + condition;
		log.writelnString();
		log.writelnString(lineStatus);
		
		if(!log.close()) {
			GenericIO.writelnString("The operation of closing the file " + fileName + " failed");
			System.exit(1);
		}
	}
	
	
	/**
	 * Write a state line at the end of the logging file. 
	 * 
	 * The current state of the pilot, hostess and passengers is organized in a line to be printed, 
	 * as well as the number of passengers presently forming a queue to board the plane,
	 * the number of passengers in the plane
	 * and the number of passengers that have already arrived at their destination. */
	
	private void reportStatus() {
		TextFile log = new TextFile();			// Instantiation of a text file handler
		
		String lineStatus = "";					// state line to be printed
		
		if(!log.openForAppending(".", fileName)) {
			GenericIO.writelnString("The operation of opening for appending the file" + fileName + " failed!");
			System.exit(1);
		}
		
		switch(pilotState) {
			case AT_TRANSFER_GATE:
				lineStatus += "ATRG  ";
				break;
			case READY_FOR_BOARDING:
				lineStatus += "RDFB  ";
				break;
			case WAITING_FOR_BOARDING:
				lineStatus += "WTFB  ";
				break;
			case FLYING_FORWARD:
				lineStatus += "FLFW  ";
				break;
			case DEBOARDING:
				lineStatus += "DRPP  ";
				break;
			case FLYING_BACK:
				lineStatus += "FLBK  ";
				break;
		}
		
		switch(hostessState) {
		case WAIT_FOR_FLIGHT:
			lineStatus += "WTFL  ";
			break;
		case WAIT_FOR_PASSENGER:
			lineStatus += "WTPS  ";
			break;
		case CHECK_PASSENGER:
			lineStatus += "CKPS  ";
			break;
		case READY_TO_FLY:
			lineStatus += "RDTF  ";
			break;
	}
		
		for(int i = 0; i < SimulParams.P; i++) {
			switch(passengerState[i]) {
				case GOING_TO_AIRPORT:
					lineStatus += "GTAP  ";
					break;
				case IN_QUEUE:
					lineStatus += "INQE  ";
					break;
				case IN_FLIGHT:
					lineStatus += "INFL  ";
					break;
				case AT_DESTINATION:
					lineStatus += "ATDS  ";
					break;
			}
		}
		lineStatus +=this.inQueue + "     " + this.inFlight + "     " + this.inPTAL;
		log.writelnString(lineStatus);
		if(!log.close()) {
			GenericIO.writelnString("The operation of closing the file " + fileName + " failed");
			System.exit(1);
		}
			
	}

}
