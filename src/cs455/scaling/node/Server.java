package cs455.scaling.node;

import java.io.IOException;
import java.nio.channels.Selector;

import cs455.scaling.util.BlockingList;
import cs455.scaling.util.Threadpool;

public class Server {
	private BlockingList list;
	private Threadpool pool;
	private Selector selector;
	
	public Server(int port, int threadPoolSize){
		list = new BlockingList();
		pool = new Threadpool(threadPoolSize, list);
		try {
			selector = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void start(){
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length != 2){
			System.out.println("incorrect Number of arguments");
		}
		Server s = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
	}

}
