package common_infrastructures;

public class MemFIFO<R> extends MemObject<R> 
{
	private int inPnt = 0,
				outPnt =0;
	private boolean empty = true;
	
	public MemFIFO(int nElem) throws MemException{
		super(nElem);
	}
	
	@Override
	public void write(R val) throws MemException{		
		if ((inPnt != outPnt || empty)) {
			mem[inPnt] = val;
			inPnt +=1;
			inPnt %= nMax;
			empty = false;
		}
	}
	
	@Override
	public R read() throws MemException{
		R val = null;
		
		if ((outPnt != inPnt || !empty)) {
			val = mem[outPnt];
			outPnt +=1;
			outPnt %= nMax;
			empty = (inPnt == outPnt);
		} else throw new MemException("Fifo empty!");
		
		return val;
	}
	
	public boolean isEmpty()
	{
		return empty;
	}

}
