/**
 * 
 */
package common_infrastructures;

/**
 * @author tomasfilipe7
 *
 */
public class MemException extends Exception
{
	public MemException(String errorMessage) {
		super(errorMessage);
	}
	
	public MemException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}
}
