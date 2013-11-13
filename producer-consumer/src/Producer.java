/**
 * 
 */

/**
 * @author cooniur
 * 
 */
public class Producer implements Runnable {

	private ProductBuffer			_pb;
	private ProducerEventHandler	_handler;
	private int						_max_product_num;
	private boolean					_running;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		int i = 0;
		this.start();
		while (this._running && i < this._max_product_num) {
			try {
				this._pb.setContent(i);
				Thread.sleep(2000);
				this._onDisplay(i);
				i++;
			} catch (InterruptedException e) {
				this._onInterrupted(e);
			}
		}
		this.stop();
		this._onFinished();
	}

	public Producer(ProductBuffer pb, int max) {
		this._pb = pb;
		this._handler = null;
		this.setMaxProductNum(max);
		this._running = false;
	}

	public void setMaxProductNum(int value) {
		this._max_product_num = value;
	}

	private void start() {
		this._running = true;
	}
	
	public void stop() {
		this._running = false;
	}

	public void setEventHandler(ProducerEventHandler handler) {
		this._handler = handler;
	}

	private void _onDisplay(int value) {
		if (this._handler != null)
			this._handler.onProducerDisplay(value);
	}

	private void _onInterrupted(InterruptedException e) {
		if (this._handler != null)
			this._handler.onProducerInterrupted(e);
	}
	
	private void _onFinished() {
		if (this._handler != null)
			this._handler.onProducerFinished();
	}
}
