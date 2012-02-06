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

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.dateofrock.simpledbmapper.SimpleDBMapper;

/**
 * 
 * @author Takehito Tanabe (dateofrock at gmail dot com)
 */
public class JobProducer {

	protected SimpleDBMapper sdbMapper;
	protected AmazonSQS sqs;
	protected Reflector reflector;

	public JobProducer(SimpleDBMapper sdbMapper, AmazonSQS sqs) {
		super();
		this.sdbMapper = sdbMapper;
		this.sqs = sqs;
		this.reflector = new Reflector();
	}

	public void put(AbstractJobTicket jobTicket) {
		String queueName = this.reflector.findQueueName(jobTicket.getClass());

		try {
			jobTicket.prepare();
		} catch (Exception e) {
			throw new SimpleSQSException(e);
		}

		Date now = new Date();

		if (jobTicket.createdAt == null) {
			jobTicket.createdAt = now;
		}
		jobTicket.updatedAt = now;
		jobTicket.status = Status.WAITING.getValue();
		this.sdbMapper.save(jobTicket);

		GetQueueUrlResult result = this.sqs.getQueueUrl(new GetQueueUrlRequest(queueName));
		String queueUrl = result.getQueueUrl();
		this.sqs.sendMessage(new SendMessageRequest(queueUrl, jobTicket.id));
	}

}
