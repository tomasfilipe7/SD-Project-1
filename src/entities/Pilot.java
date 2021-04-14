/**
 * 
 */
package entities;

import common_infrastructures.EPilotState;

/**
 * @author tomasfilipe7
 *
 */
public class Pilot extends Thread
{
	private EPilotState state;
	
	public Pilot() {
		super();
	}
	
	public void changeState(EPilotState state)
	{
		this.state = state;
	}
	

	@Override
	public void run()
	{
		
	}

}
