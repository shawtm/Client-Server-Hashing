package cs455.scaling.util;

import java.util.LinkedList;

public class BlockingList {
	private LinkedList<WorkUnit> list;
	
	public BlockingList(){
		list = new LinkedList<>();
	}
	
	public synchronized void put(WorkUnit unit){
		list.add(unit);
		list.notify();
	}
	
	public synchronized WorkUnit take() throws InterruptedException{
		while (this.isEmpty())
			list.wait();
		return list.removeFirst();
	}
	
	private synchronized boolean isEmpty(){
		return list.isEmpty();
	}
}
