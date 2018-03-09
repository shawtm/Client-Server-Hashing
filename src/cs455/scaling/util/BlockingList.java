package cs455.scaling.util;

import java.util.LinkedList;

public class BlockingList {
	private LinkedList<WorkUnit> list;
	
	public BlockingList(){
		list = new LinkedList<>();
	}
	
	public void put(WorkUnit unit){
		synchronized (list){
			//System.out.println("Got List Lock");
			list.add(unit);
			list.notify();
		}
	}

	public WorkUnit take() throws InterruptedException{
		synchronized (list) {
			while (this.isEmpty())
				list.wait();
			return list.removeFirst();
		}
	}
	
	private boolean isEmpty(){
		synchronized (list) {
			return list.isEmpty();
		}
	}
}
