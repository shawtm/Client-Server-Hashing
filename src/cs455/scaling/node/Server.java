package cs455.scaling.node;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cs455.scaling.util.BlockingList;
import cs455.scaling.util.ThreadPoolManager;
import cs455.scaling.util.Threadpool;

public class Server {
	private BlockingList list;
	private Threadpool pool;
	private ThreadPoolManager manager;
	public Server(int port, int threadPoolSize) throws IOException{
		list = new BlockingList();
		pool = new Threadpool(threadPoolSize, list);
		manager = new ThreadPoolManager(port, list);
	}
	public void start(){
		// start manager thread
		new Thread(manager).start();
		while (true) {
			//print diagnostics
			System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + manager.getDiagnostics());
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length != 2){
			System.out.println("incorrect Number of arguments");
		}
		try {
			Server s = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			s.start();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
