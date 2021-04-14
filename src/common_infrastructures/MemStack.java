/**
 * 
 */
package common_infrastructures;

/**
 * @author tomasfilipe7
 *
 */
public class MemStack<R> extends MemObject<R>
{
	private int stackPnt = 0;

	public MemStack(int nElem) {
		super(nElem);
	}
	
	public void write (R val) throws MemException{
		if(stackPnt != nMax)
		{ mem[stackPnt] = val;
			stackPnt +=1;
		}
		else throw new MemException("Stack full!");
	}
	
	public R read () throws MemException{
		if(stackPnt != 0) {
			stackPnt -=1;
			return (mem[stackPnt]);
		}
		else throw new MemException("Stack empty!");
	}
}
