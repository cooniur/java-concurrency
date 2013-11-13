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

	public synchronized void write(Process p, String str)
			throws InterruptedException {

		if (p.getProcessType() == Process.Type.USER) {
			while (Access.DENY == this.getAccess()) {
				p.TriggerOnAccessDenied();
				this.wait();
			}
		}
		this._onWrite(str);
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
