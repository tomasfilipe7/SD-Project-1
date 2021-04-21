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
		log.writelnString("Passenger 1 Passenger 2 Passenger 3 Passenger 4 ...");
		
		if(!log.close()) {
			GenericIO.writelnString("The operation of closing the file " + fileName + " failed!");
			System.exit(1);
		}
		
		reportStatus();
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
		
		switch(hostessState) {
			case WAIT_FOR_FLIGHT:
				lineStatus += "WAIT_FOR_FLIGHT";
				break;
			case WAIT_FOR_PASSENGER:
				lineStatus += "WAIT_FOR_PASSENGER";
				break;
			case CHECK_PASSENGER:
				lineStatus += "CHECK_PASSENGER";
				break;
			case READY_TO_FLY:
				lineStatus += "READY_TO_FLY";
				break;
		}
		
		switch(pilotState) {
			case AT_TRANSFER_GATE:
				lineStatus += "AT_TRANSFER_GATE";
				break;
			case READY_FOR_BOARDING:
				lineStatus += "READY_FOR_BOARDING";
				break;
			case WAITING_FOR_BOARDING:
				lineStatus += "WAITING_FOR_BOARDING";
				break;
			case FLYING_FORWARD:
				lineStatus += "FLYING_FORWARD";
				break;
			case DEBOARDING:
				lineStatus += "DEBOARDING";
				break;
			case FLYING_BACK:
				lineStatus += "FLYING_BACK";
				break;
		}
		
		for(int i = 0; i < SimulParams.P; i++) {
			switch(passengerState[i]) {
				case GOING_TO_AIRPORT:
					lineStatus += "GOING_TO_AIRPORT";
					break;
				case IN_QUEUE:
					lineStatus += "IN_QUEUE";
					break;
				case IN_FLIGHT:
					lineStatus += "IN_FLIGHT";
					break;
				case AT_DESTINATION:
					lineStatus += "AT_DESTINATION";
					break;
			}
		}
		
		log.writelnString(lineStatus);
		
		if(!log.close()) {
			GenericIO.writelnString("The operation of closing the file " + fileName + " failed");
			System.exit(1);
		}
			
	}

}
