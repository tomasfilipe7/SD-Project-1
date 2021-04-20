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
	 * Number of iterations of the customer life cycle
	 */
	private final int nIter;
	
	/**
	 * State of the Passengers
	 */
	private final EPassengerState [] passengerState;
	
	/**
	 * State of the Hostess
	 */
	private final EHostessState hostessState;
	
	/**
	 * State of the Pilot
	 */
	private final EPilotState pilotState;
	
	
	/**
	 * @param fileName
	 * @param nIter
	 */
	
	public GeneralRepos(String fileName, int nIter) {
		if ((fileName == null) || Objects.equals(fileName, "")) {
			this.fileName = "logger";
		} 
		else {
			this.fileName = fileName;
		}
		this.nIter = nIter;
		
		hostessState = EHostessState.WAIT_FOR_FLIGHT;
		pilotState = EPilotState.AT_TRANSFER_GATE;
		passengerState = new EPassengerState[SimulParams.P];
		for(int i = 0; i < SimulParams.P; i++) {
			passengerState[i] = EPassengerState.GOING_TO_AIRPORT;
		}
		
		reportInitialStatus();
		
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
		log.writelnString("\nNumber of iterations = " + nIter + "\n");
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
			
			// continuar!!!
		}
	}

}
