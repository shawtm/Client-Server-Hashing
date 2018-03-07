package cs455.scaling.node;

import cs455.scaling.util.BlockingList;
import cs455.scaling.util.Threadpool;

public class Server {
	private BlockingList list;
	private Threadpool pool;
	
	public Server(int threadPoolSize){
		list = new BlockingList();
		pool = new Threadpool(threadPoolSize, list);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
