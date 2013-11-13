/**
 * 
 */

/**
 * @author cooniur
 * 
 */
public class Console {
	public enum Access {
		ALLOW, DENY
	}

	private volatile Console.Access	_access		= Access.ALLOW;
	public ConsoleEventHandler		_handler	= null;

	public synchronized void write(Process.Type type, String str)
			throws InterruptedException {

		if (Process.Type.USER == type) {
			while (Access.DENY == this.getAccess()) {
				this.wait();
			}
		}
		this._onWrite(str);
		this.notifyAll();
	}

	public synchronized Access getAccess() {
		return this._access;
	}

	public synchronized void setAccess(Console.Access a) {
		this._access = a;
		this.notifyAll();
	}

	public void setHandler(ConsoleEventHandler h) {
		this._handler = h;
	}

	private void _onWrite(String str) {
		if (this._handler != null)
			this._handler.onWrite(this, str);
	}

}
