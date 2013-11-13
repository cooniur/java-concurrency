/**
 * 
 */

/**
 * @author cooniur
 * 
 */
public interface ProcessEventHandler {
	void onInterrupted(Process sender, InterruptedException e);

	void onAccessDenied(Process sender);

	void onFinished(Process sender);
}
