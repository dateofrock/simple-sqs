package com.dateofrock.aws.simplesqs;

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
					jobTicket.executeJob();
				} catch (Exception e) {
					// TODO StackTraceをS3に保存
					// jobTicket.stackTrace=stacktrace
					jobTicket.status = Status.FAILURE.getValue();
					jobTicket.updatedAt = new Date();
					this.sdbMapper.save(jobTicket);
				}

				jobTicket.status = Status.SUCCESS.getValue();
				jobTicket.updatedAt = new Date();
				this.sdbMapper.save(jobTicket);

				break;
			case IN_PROGRESS:

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
