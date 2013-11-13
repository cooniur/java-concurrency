import java.util.logging.ConsoleHandler;

/**
 * 
 */

/**
 * @author cooniur
 * 
 */
public class Consumer implements Runnable {
	private ProductBuffer			_pb;
	private ConsumerEventHandler	_handler;
	private boolean					_running;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		this.start();
		while (this._running) {
			try {
				int value = this._pb.getContent();
				Thread.sleep(2000);
				this._onDisplay(value);
			} catch (InterruptedException e) {
				this._onInterrupted(e);
			}
		}
		this.stop();
		this._onFinished();
	}

	public Consumer(ProductBuffer pb) {
		this._pb = pb;
		this._running = false;
	}

	private void start() {
		this._running = true;
	}

	public void stop() {
		this._running = false;
	}

	public void setEventHandle(ConsumerEventHandler handler) {
		this._handler = handler;
	}

	private void _onInterrupted(InterruptedException e) {
		if (this._handler != null)
			this._handler.onConsumerInterrupted(e);
	}

	private void _onDisplay(int value) {
		if (this._handler != null)
			this._handler.onConsumerDisplay(value);
	}

	private void _onFinished() {
		if (this._handler != null)
			this._handler.onConsumerFinished();
	}
}
