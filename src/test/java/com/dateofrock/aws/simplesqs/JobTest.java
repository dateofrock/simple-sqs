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

import java.util.UUID;

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

/**
 * 
 * @author Takehito Tanabe (dateofrock at gmail dot com)
 */
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
	public void test() throws Exception {
		Reflector reflector = new Reflector();
		String queueName = reflector.findQueueName(HelloJobTicket.class);
		String queueUrl = this.sqs.getQueueUrl(new GetQueueUrlRequest(queueName)).getQueueUrl();

		HelloJobTicket jobTicket = new HelloJobTicket();
		jobTicket.id = UUID.randomUUID().toString();

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
			Thread.sleep(1000);
		}
	}
}
