package cs455.scaling.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class ThreadPoolManager implements Runnable {
	private Selector sel;
	private BlockingList list;
	private HashMap<SelectionKey,Integer> counts;
	
	public ThreadPoolManager(int port, BlockingList list) throws IOException {
		this.list = list;
		sel = Selector.open();
		ServerSocketChannel serv = ServerSocketChannel.open();
		serv.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
		serv.configureBlocking(false);
		serv.register(sel, SelectionKey.OP_ACCEPT);
		counts = new HashMap<>();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				sel.select();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Set<SelectionKey> keys = sel.selectedKeys();
			for (SelectionKey key: keys) {
				if(key.isAcceptable()) {//copied base from http://tutorials.jenkov.com/java-nio/selectors.html#selecting-channels-via-a-selector
			        // a connection was accepted by a ServerSocketChannel so create new channel for receiving
					ServerSocketChannel servChan = (ServerSocketChannel) key.channel();
					try {
						System.out.println("Accepting new connection");
						SocketChannel chan = servChan.accept();
						chan.configureBlocking(false);
						SelectionKey newkey = chan.register(sel, SelectionKey.OP_READ);
						counts.put(newkey, 0);
						System.out.println("Successfully added new Connection");
					} catch (IOException e) {
						e.printStackTrace();
					}
			    } else if (key.isReadable()) {
			        // a channel is ready for reading so create new work unit
			    	System.out.println("Recieved a Work Unit");
			    	WorkUnit unit = new WorkUnit((SocketChannel) key.channel());
			    	System.out.println("created Unit");
			    	list.put(unit);
			    	counts.put(key, counts.get(key) + 1);
			    	System.out.println("Passed Off work unit");
			    } else if (key.isWritable()) {
			        // a channel is ready for writing
			    	// Do nothing 
			    }
				keys.remove(key);
			}
		}
	}
	public Diag getDiagnostics() {
		int numMessages = 0, numClients = 0;
		double meanMessages = 0.0, stdDev = 0.0;
		Collection<Integer> values = counts.values();
		for (Integer val: values) 
			numMessages += val;
		numClients = values.size();
		meanMessages = (new Double(numMessages) / new Double(numClients));
		// average Standard Deviation
		for (Integer val: values)
			stdDev += Math.pow((val-meanMessages),2);
		stdDev = Math.sqrt(stdDev) / values.size();
		// Reset counts
		for (SelectionKey key: counts.keySet())
			counts.put(key, 0);
		return new Diag(numMessages,numClients,meanMessages,stdDev);
	}
	public class Diag{
		private int numMessages, numClients;
		private double meanMessages, stdDev;
		public Diag(int numMessages, int numClients, double meanMessages, double stdDev) {
			this.numMessages = numMessages;
			this.numClients = numClients;
			this.meanMessages = meanMessages;
			this.stdDev = stdDev;
		}
		public String toString() {
			return new String(" Server Throughput: "+ numMessages +" messages/s, Active Client Connections:" + numClients
			+ "\nMean Per-client Throughput: "+ meanMessages +" messages/s, Std. Dev. Of Per-client Throughput: "+ stdDev + " messages/s");
		}
	}
}
