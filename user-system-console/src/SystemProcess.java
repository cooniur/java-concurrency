/**
 * 
 */

/**
 * @author cooniur
 * 
 */
public class SystemProcess extends Process {

	public SystemProcess(String name, Console c) {
		super(name, c, Process.Type.SYSTEM);
	}

}
