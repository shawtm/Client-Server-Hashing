package cs455.scaling.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class ThreadPoolManager implements Runnable {
	private Selector sel;
	private ServerSocketChannel serv;
	private BlockingList list;
	
	public ThreadPoolManager(int port, BlockingList list) throws IOException {
		this.list = list;
		sel = Selector.open();
		serv = ServerSocketChannel.open();
		serv.socket().bind(new InetSocketAddress(port));
		serv.configureBlocking(false);
		serv.register(sel, SelectionKey.OP_CONNECT);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			Set<SelectionKey> keys = sel.selectedKeys();
			for (SelectionKey key: keys) {
				if(key.isAcceptable()) {//copied base from http://tutorials.jenkov.com/java-nio/selectors.html#selecting-channels-via-a-selector
			        // a connection was accepted by a ServerSocketChannel so create new channel for receiving
					ServerSocketChannel servChan = (ServerSocketChannel) key.channel();
					try {
						SocketChannel chan = servChan.accept();
						chan.register(sel, SelectionKey.OP_READ);
					} catch (IOException e) {
						e.printStackTrace();
					}
			    } else if (key.isReadable()) {
			        // a channel is ready for reading so create new work unit
			    	list.put(new WorkUnit((SocketChannel) key.channel()));
			    } else if (key.isWritable()) {
			        // a channel is ready for writing
			    	//Do nothing 
			    }
			}
		}
	}
	
}
