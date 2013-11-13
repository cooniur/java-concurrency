/**
 * 
 */

/**
 * @author cooniur
 *
 */
public interface ConsumerEventHandler {
	void onConsumerDisplay(int value);
	void onConsumerInterrupted(InterruptedException e);
	void onConsumerFinished();
}
