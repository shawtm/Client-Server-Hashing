package cs455.scaling.util;

public class Threadpool {
	private Thread[] pool;
	
	public Threadpool(int size, BlockingList list){
		pool = new Thread[size];
		for (Thread t: pool)
			t = new Thread(new Worker(list));
		this.startThreads();
	}
	private void startThreads(){
		for (Thread t: pool)
			t.start(); 
	}
}
