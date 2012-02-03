package com.dateofrock.aws.simplesqs;

import java.util.Date;

import com.dateofrock.simpledbmapper.SimpleDBAttribute;
import com.dateofrock.simpledbmapper.SimpleDBItemName;

abstract public class AbstractJobTicket {

	@SimpleDBItemName
	public String id = null;

	@SimpleDBAttribute(attributeName = "status")
	public String status = Status.WAITING.toString();

	@SimpleDBAttribute(attributeName = "receiptHandle")
	public String receiptHandle = null;

	@SimpleDBAttribute(attributeName = "createdAt")
	public Date createdAt = null;

	@SimpleDBAttribute(attributeName = "updatedAt")
	public Date updatedAt = null;

	abstract public void prepareJob() throws Exception;

	abstract public void executeJob() throws Exception;

}
