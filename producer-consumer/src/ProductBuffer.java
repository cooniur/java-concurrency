/**
 * 
 */

/**
 * @author cooniur
 * 
 */
public class ProductBuffer {
	private int		_content;
	private boolean	_available;

	public ProductBuffer() {
		this._available = false;
		this._content = 0;
	}

	synchronized public int getContent() throws InterruptedException {
		while (!this._available) {
			this.wait();
		}

		this._available = false;
		this.notifyAll();
		return this._content;
	}

	synchronized public void setContent(int value) throws InterruptedException {
		while (this._available) {
			this.wait();
		}

		this._available = true;
		this._content = value;
		this.notifyAll();
	}
}
