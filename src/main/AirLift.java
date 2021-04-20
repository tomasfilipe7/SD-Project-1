/**
 * 
 */
package main;

import shared_regions.*;

import java.io.IOException;

import entities.*;
import genclass.FileOp;
import genclass.GenericIO;

/**
 * @author tomasfilipe7
 *
 */
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
		
		int nIter;										// number of iterations of the passenger
		String fileName;								// logging file name
		char op;										// selected option
		boolean success;								// end of operation flag
		
		/* problem initialization */
		
		GenericIO.writelnString("\n" + "     Airlift\n");
		GenericIO.writeString("Number of iterations of the passenger life cycle? ");
		nIter = GenericIO.readlnInt();
		
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
		
		// falta acrescentar o repos nas shared regions, por isso estão comentadas
		repos = new GeneralRepos(fileName, nIter);
		// depAirport = new DepAirport(repos);
		// destAirport = new DestAirport(repos);
		// plane = new Plane(repos);
		
		for(int i = 0; i < SimulParams.P; i++) {
			// passenger[i] = new Passenger ("pass_" + (i+1), i, depAirport, destAirport, plane);
		}
		
		/* start of the simulation */
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
	}

}
