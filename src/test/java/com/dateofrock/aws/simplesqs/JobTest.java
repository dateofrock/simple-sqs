package com.dateofrock.aws.simplesqs;

import org.junit.Before;
import org.junit.Test;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.dateofrock.simpledbmapper.SimpleDBMapper;

public class JobTest {
	String simpleDBAPIEndPoint = "sdb.ap-northeast-1.amazonaws.com";
	String sqsAPIEndPoint = "sqs.ap-northeast-1.amazonaws.com";
	private SimpleDBMapper mapper;
	private AmazonSQS sqs;

	@Before
	public void setUp() throws Exception {
		AWSCredentials cred = new PropertiesCredentials(JobTest.class.getResourceAsStream("/AwsCredentials.properties"));

		AmazonSimpleDB sdb = new AmazonSimpleDBClient(cred);
		sdb.setEndpoint(this.simpleDBAPIEndPoint);
		AmazonS3 s3 = new AmazonS3Client(cred);
		this.mapper = new SimpleDBMapper(sdb, s3);
		this.sqs = new AmazonSQSClient(cred);
		this.sqs.setEndpoint(this.sqsAPIEndPoint);
	}

	@Test
	public void test() {
		Reflector reflector = new Reflector();
		String queueName = reflector.findQueueName(HelloJobTicket.class);
		String queueUrl = this.sqs.getQueueUrl(new GetQueueUrlRequest(queueName)).getQueueUrl();

		HelloJobTicket jobTicket = new HelloJobTicket();
		JobProducer jobProducer = new JobProducer(this.mapper, this.sqs);
		jobProducer.put(jobTicket);

		JobConsumer consumer = new JobConsumer(this.mapper, this.sqs);
		while (true) {
			consumer.consume(HelloJobTicket.class);
			GetQueueAttributesResult result = this.sqs.getQueueAttributes(new GetQueueAttributesRequest(queueUrl)
					.withAttributeNames("All"));
			String numOfMessages = result.getAttributes().get("ApproximateNumberOfMessages");
			if ("0".equals(numOfMessages)) {
				break;
			}
		}
	}
}
