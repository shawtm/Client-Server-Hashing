package cs455.scaling.util;

public class Threadpool {
	private Thread[] pool;
	
	public Threadpool(int size, BlockingList list){
		pool = new Thread[size];
		for (int i = 0; i < size; i++)
			pool[i] = new Thread(new Worker(list));
		this.startThreads();
	}
	private void startThreads(){
		for (int i = 0; i < pool.length; i++)
			pool[i].start(); 
	}
}
