package cs455.scaling.util;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WorkUnit implements Runnable {
	private SocketChannel channel;
	public WorkUnit(SocketChannel channel){
		this.channel = channel;
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
		// Read in from channel
		ByteBuffer buf = ByteBuffer.allocate(8192);
		int read = 0;
		System.out.println("Reading!");
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
			// send hash
			ByteBuffer send = ByteBuffer.allocate(hash.getBytes().length);
			System.out.println("Sending!");
			while (send.hasRemaining())
				channel.write(send);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
