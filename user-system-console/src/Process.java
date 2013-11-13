/**
 * 
 */

/**
 * @author cooniur
 * 
 */
public class Process implements Runnable {
	public enum Type {
		USER, SYSTEM
	}

	private String				_name;
	private Console				_console;
	private Process.Type		_type;
	private boolean				_running;
	private ProcessEventHandler	_handler	= null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		this.start();
		int i = 1;
		String str = "";
		while (this._running) {
			str = String.format("%s: I'm running (%d).\n", this.getName(), i);
			i++;
			try {
				if (!this._running)
					break;

				Thread.sleep(1000);
				this._console.write(this._type, str);
			} catch (InterruptedException e) {
				this._onInterrupted(e);
			}
		}
		this.stop();
		this._onFinished();
	}

	public Process(String name, Console c, Process.Type type) {
		this._name = name;
		this._console = c;
		this._type = type;
	}

	public String getName() {
		return this._name;
	}

	public Type getProcessType() {
		return this._type;
	}

	private void start() {
		this._running = true;
	}

	public void stop() {
		this._running = false;
	}

	public void setHandler(ProcessEventHandler h) {
		this._handler = h;
	}

	private void _onInterrupted(InterruptedException e) {
		if (this._handler != null)
			this._handler.onInterrupted(this, e);
	}

	private void _onFinished() {
		if (this._handler != null)
			this._handler.onFinished(this);
	}

	@Override
	public String toString() {
		return this.getName();
	}
}
