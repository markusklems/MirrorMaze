/**
 * 
 */
package de.eorganization.crawler.client;

/**
 * @author mugglmenzel
 *
 */
public class OutOfQuotaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2309581430561552891L;

	/**
	 * 
	 */
	public OutOfQuotaException() {
		super();
	}

	/**
	 * @param arg0
	 */
	public OutOfQuotaException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public OutOfQuotaException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public OutOfQuotaException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
