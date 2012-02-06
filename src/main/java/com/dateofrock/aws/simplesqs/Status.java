/*
 *	Copyright 2012 Takehito Tanabe (dateofrock at gmail dot com)
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package com.dateofrock.aws.simplesqs;

/**
 * 
 * @author Takehito Tanabe (dateofrock at gmail dot com)
 */
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
