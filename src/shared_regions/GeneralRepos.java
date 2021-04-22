package shared_regions;

import java.util.Objects;

import common_infrastructures.*;
import main.*;
import entities.*;
import genclass.GenericIO;
import genclass.TextFile;

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
	private int inQueue = 0;
	private int inFlight = 0;
	private int inPTAL = 0;
	private int flightNum = 1;
	
	
	/**
	 * Instatiation of a general repository object
	 * 
	 * @param fileName name of the logging file
	 * @param nIter
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
	
	public void setFlightNum(int flightNum) {
		this.flightNum = flightNum;
	}

	
	public void setInQueue(int inQueue) {
		this.inQueue = inQueue;
	}

	public void setInFlight(int inFlight) {
		this.inFlight = inFlight;
	}

	public void setInPTAL(int inPTAL) {
		this.inPTAL = inPTAL;
	}

	/**
	 * Write the header to the logging file.
	 * 
	 *  */
	private void reportInitialStatus() {
		TextFile log = new TextFile();				// Instantiation of a text file handler
		
		if(!log.openForWriting(".", fileName)) {
			GenericIO.writelnString("The operation of creating the file " + fileName + " failed!");
			System.exit(1);
		}
		log.writelnString("             Airlift");
		log.writelnString("PT    HT    P00   P01   P02   P03   P04   P05   P06   P07   P08   P09   P10   P11   P12   P13   P14   P15   P16   P17   P18   P19   P20   InQ   InF   PTAL");
		
		if(!log.close()) {
			GenericIO.writelnString("The operation of closing the file " + fileName + " failed!");
			System.exit(1);
		}
		
		reportStatus();
		log.writelnString();
	}
	
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
	 * Write a state line at the end of the logging file. */
	
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
