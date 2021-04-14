/**
 * 
 */
package entities;

import common_infrastructures.EPassengerState;

/**
 * @author tomasfilipe7
 *
 */
public class Passenger extends Thread
{
	private EPassengerState state;
	/**
	 * 
	 */
	public Passenger() {
		super();
	}
	public void changeState(EPassengerState state)
	{
		this.state = state;
	}
	@Override
	public void run()
	{
		
	}
}
