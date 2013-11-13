/**
 * 
 */

/**
 * @author cooniur
 *
 */
public interface ProducerEventHandler {
	void onProducerDisplay(int value);
	void onProducerInterrupted(InterruptedException e);
	void onProducerFinished();
}
