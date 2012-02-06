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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.dateofrock.simpledbmapper.SimpleDBMapper;
import com.dateofrock.simpledbmapper.SimpleDBMapperNotFoundException;

/**
 * 
 * @author Takehito Tanabe (dateofrock at gmail dot com)
 */
public class JobConsumer {

	protected Reflector reflector;
	protected SimpleDBMapper sdbMapper;
	protected AmazonSQS sqs;

	protected int maxNumberOfMessages = 1;

	public JobConsumer(SimpleDBMapper sdbMapper, AmazonSQS sqs) {
		this.reflector = new Reflector();
		this.sdbMapper = sdbMapper;
		this.sqs = sqs;
	}

	public void consume(Class<? extends AbstractJobTicket> clazz) {
		String queueName = this.reflector.findQueueName(clazz);
		GetQueueUrlResult result = this.sqs.getQueueUrl(new GetQueueUrlRequest(queueName));
		String queueUrl = result.getQueueUrl();

		ReceiveMessageResult receivedMessage = this.sqs.receiveMessage(new ReceiveMessageRequest(queueUrl)
				.withMaxNumberOfMessages(this.maxNumberOfMessages));
		List<Message> messages = receivedMessage.getMessages();
		for (Message message : messages) {
			String jobId = message.getBody();
			String receiptHandle = message.getReceiptHandle();

			AbstractJobTicket jobTicket = null;
			try {
				jobTicket = this.sdbMapper.load(clazz, jobId);
				jobTicket.receiptHandle = receiptHandle;

			} catch (SimpleDBMapperNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// FIXME Messageをけす？
				this.sqs.deleteMessage(new DeleteMessageRequest(queueUrl, receiptHandle));
				return;
			}

			Status currentStatus = Status.fromJobTicket(jobTicket);
			switch (currentStatus) {
			case WAITING:
				jobTicket.status = Status.IN_PROGRESS.getValue();
				jobTicket.updatedAt = new Date();
				this.sdbMapper.save(jobTicket);

				try {
					// ジョブ実行
					jobTicket.execute();
				} catch (Exception e) {
					// StackTraceを保存
					StringWriter writer = new StringWriter();
					PrintWriter printWriter = new PrintWriter(writer);
					e.printStackTrace(printWriter);
					printWriter.flush();
					printWriter.close();
					try {
						writer.close();
					} catch (IOException ignore) {
					}
					jobTicket.exceptionStackTrace = writer.toString();
					jobTicket.status = Status.FAILURE.getValue();
					jobTicket.updatedAt = new Date();
					this.sdbMapper.save(jobTicket);
					break;
				}

				jobTicket.status = Status.SUCCESS.getValue();
				jobTicket.updatedAt = new Date();
				this.sdbMapper.save(jobTicket);
				break;
			case IN_PROGRESS:
				jobTicket.processIfInProgress();
				break;
			case SUCCESS:

				break;
			case FAILURE:

				break;
			default:
				break;
			}

		}
	}
}
