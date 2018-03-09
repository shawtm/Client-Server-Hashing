package cs455.scaling.util;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WorkUnit implements Runnable {
	private SelectionKey key;
	public WorkUnit(SelectionKey key){
		this.key = key;
	}
	public static String SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException {
		 MessageDigest digest = MessageDigest.getInstance("SHA1");
		 byte[] hash = digest.digest(data);
		 BigInteger hashInt = new BigInteger(1, hash);
		 return hashInt.toString(16);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		SocketChannel channel = (SocketChannel) key.channel();
		// Read in from channel
		ByteBuffer buf = ByteBuffer.allocate(8192);
		int read = 0;
		//System.out.println("Reading!");
		while (buf.hasRemaining() && read != -1) {
			try {
				read = channel.read(buf);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// calculate hash
		try {
			String hash = SHA1FromBytes(buf.array());
			//System.out.println("made hash: " + hash);
			byte[] hashbytes = hash.getBytes();
			byte[] bytes = new byte[40];
			for (int i = 0; i < hashbytes.length; i++)
				bytes[i] = hashbytes[i];
			//System.out.println(bytes.length);
			// send hash
			ByteBuffer send = ByteBuffer.wrap(bytes);
			//System.out.println("Sending!");
			while (send.hasRemaining())
				channel.write(send);
			key.interestOps(SelectionKey.OP_READ);
			//System.out.println("Sent!");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
