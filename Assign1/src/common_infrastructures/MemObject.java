/**
 * 
 */
package common_infrastructures;

/**
 * @author tomasfilipe7
 * @author marciapires
 *
 */
public abstract class MemObject<R> 
{
	protected R[] mem;
	protected int nMax;
	
	@SuppressWarnings("unchecked")
	protected MemObject(int nElem) throws MemException{
		mem = (R[]) new Object[nElem];
		nMax = nElem;
	}
	
	protected abstract void write(R val) throws MemException;
	protected abstract R read() throws MemException;
}
