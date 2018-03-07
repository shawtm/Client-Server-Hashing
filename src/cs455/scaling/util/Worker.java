package cs455.scaling.util;

public class Worker implements Runnable {
	private BlockingList list;
	
	public Worker(BlockingList list){
		this.list = list;
	}
	@Override
	public void run() {
		WorkUnit unit;
		while(true){
			try {
				unit = list.take();
				unit.run();
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}

}
