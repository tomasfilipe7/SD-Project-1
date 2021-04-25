/**
 * 
 */
package main;

import shared_regions.*;
import entities.*;
import genclass.FileOp;
import genclass.GenericIO;

/**
 * @author tomasfilipe7
 * @author marciapires
 *
 */

/**
 * Airlift.
 * 
 * */
public class AirLift {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DepAirport depAirport;							// reference to the departure airport
		DestAirport destAirport;						// reference to the destination airport
		Plane plane;									// reference to the plane
		GeneralRepos repos;								// reference to the general repository
		Passenger [] passenger = new Passenger [SimulParams.P]; 	// array of passenger threads
		Hostess hostess;								// reference to the hostess
		Pilot pilot;									// reference to the pilot
		
		String fileName;								// logging file name
		char op;										// selected option
		boolean success;								// end of operation flag
		
		/* problem initialization */
		
		GenericIO.writelnString("\n" + "     Airlift\n");

		do {
			GenericIO.writeString("Logging file name: ");
			fileName = GenericIO.readlnString();
			
			if(FileOp.exists(".", fileName)) {
				do {
					GenericIO.writeString("There is already a file with this name. Delete it (y - yes; n - no)? ");
					op = GenericIO.readlnChar();
				} while ((op != 'y') && (op != 'n'));
				
				if(op == 'y') {
					success = true;
				} else {
					success = false;
				}
			} else {
				success = true;
			}
		} while (!success);
		
		/* Instantiate the shared regions*/
		repos = new GeneralRepos(fileName);
		depAirport = new DepAirport(SimulParams.P,repos);
		destAirport = new DestAirport(repos);
		plane = new Plane(SimulParams.Min_Cap, SimulParams.Max_Cap, repos);
		
		pilot = new Pilot(EPilotState.AT_TRANSFER_GATE, depAirport, destAirport, plane);
		hostess = new Hostess(EHostessState.WAIT_FOR_FLIGHT, depAirport, plane);
		/* Instantiate the entities*/
		for(int i = 0; i < SimulParams.P; i++) {
			 passenger[i] = new Passenger (i, EPassengerState.GOING_TO_AIRPORT, depAirport, destAirport, plane);
		}
		
		
		/* start of the simulation */
		pilot.start();
		hostess.start();
		for(int i = 0; i < SimulParams.P; i++) {
			passenger[i].start();
		}
		
		
		/* waiting for the end of the simulation */
		GenericIO.writelnString();
		for(int i = 0; i < SimulParams.P; i++) {
			try {
				passenger[i].join();
			}
			catch(InterruptedException e) {}
			
			GenericIO.writelnString("The passenger " + (i+1) + " has terminated.");
		}
		GenericIO.writelnString();
		
		try {
			hostess.join();
		}catch(InterruptedException e) {}
		GenericIO.writelnString("The hostess has terminated.");
		GenericIO.writelnString();
		
		try {
			pilot.join();
		}catch(InterruptedException e) {}
		GenericIO.writelnString("The pilot has terminated.");
		GenericIO.writelnString();
		repos.writeStatistics();
	}

}
