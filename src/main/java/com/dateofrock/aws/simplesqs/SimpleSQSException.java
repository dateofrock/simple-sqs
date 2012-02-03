package com.dateofrock.aws.simplesqs;

public class SimpleSQSException extends RuntimeException {

	private static final long serialVersionUID = -7171234237588238371L;

	public SimpleSQSException(String message) {
		super(message);
	}

	public SimpleSQSException(Throwable e) {
		super(e);
	}
}
