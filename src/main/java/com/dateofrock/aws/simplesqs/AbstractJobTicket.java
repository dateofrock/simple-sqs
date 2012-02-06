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

import java.util.Date;

import com.dateofrock.simpledbmapper.SimpleDBAttribute;
import com.dateofrock.simpledbmapper.SimpleDBBlob;
import com.dateofrock.simpledbmapper.SimpleDBItemName;

/**
 * 
 * @author Takehito Tanabe (dateofrock at gmail dot com)
 */
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

	@SimpleDBBlob(attributeName = "exceptionStackTrace")
	public String exceptionStackTrace;

	abstract public void prepare() throws Exception;

	abstract public void execute() throws Exception;

	abstract public void processIfInProgress();

}
