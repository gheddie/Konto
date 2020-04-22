package de.gravitex.accounting.filter.exception;

public class FilterException extends RuntimeException {

	private static final long serialVersionUID = 6995510588896943209L;
	
	public FilterException(String message, Exception root) {
		super(message, root);
	}
}