package cs455.scaling.util;

public class Threadpool {
	private Thread[] pool;
	
	public Threadpool(int size, BlockingList list){
		pool = new Thread[size];
		
	}
}
