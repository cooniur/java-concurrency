/**
 * 
 */

/**
 * @author cooniur
 * 
 */
public interface ProcessEventHandler {
	void onInterrupted(Process sender, InterruptedException e);

	void onFinished(Process sender);
}
