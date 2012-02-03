package com.dateofrock.aws.simplesqs;

import java.util.Date;
import java.util.UUID;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.dateofrock.simpledbmapper.SimpleDBMapper;

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
			jobTicket.prepareJob();
		} catch (Exception e) {
			throw new SimpleSQSException(e);
		}

		Date now = new Date();
		jobTicket.id = UUID.randomUUID().toString();// TODO
		jobTicket.createdAt = now;
		jobTicket.updatedAt = now;
		jobTicket.status = Status.WAITING.getValue();
		this.sdbMapper.save(jobTicket);

		GetQueueUrlResult result = this.sqs.getQueueUrl(new GetQueueUrlRequest(queueName));
		String queueUrl = result.getQueueUrl();
		this.sqs.sendMessage(new SendMessageRequest(queueUrl, jobTicket.id));
	}

}
