/**
 * 
 */
package entities;

import java.lang.Thread.State;

import common_infrastructures.EHostessState;

/**
 * @author tomasfilipe7
 *
 */
public class Hostess extends Thread
{
	private EHostessState state;
	
	public Hostess() {
		super();
	}
	
	public void changeState(EHostessState state)
	{
		this.state = state;
	}
	

	@Override
	public void run()
	{
		
	}
}
