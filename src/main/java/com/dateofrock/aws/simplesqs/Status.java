package com.dateofrock.aws.simplesqs;

public enum Status {

	WAITING("waiting"), IN_PROGRESS("in progress"), SUCCESS("success"), FAILURE("failure");

	private String value;

	private Status(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static Status fromJobTicket(AbstractJobTicket jobTicket) {
		if (jobTicket == null) {
			throw new IllegalArgumentException("jobTicket is null");
		}
		String statusValue = jobTicket.status;
		for (Status status : Status.values()) {
			if (status.value.equals(statusValue)) {
				return status;
			}
		}
		throw new IllegalArgumentException(String.format("invalid value: %s", statusValue));
	}

	public static Status fromValue(String value) {
		if (value == null) {
			throw new IllegalArgumentException("value is null.");
		}
		for (Status status : Status.values()) {
			if (status.value.equals(value)) {
				return status;
			}
		}
		throw new IllegalArgumentException(String.format("invalid value: %s", value));
	}
}
