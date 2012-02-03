package com.dateofrock.aws.simplesqs;

import java.util.Random;

import com.dateofrock.simpledbmapper.SimpleDBAttribute;
import com.dateofrock.simpledbmapper.SimpleDBEntity;

@SQSJobTicket(queueName = "simple-sqs-testing")
@SimpleDBEntity(domainName = "SimpleSQS-HelloJobTicket-Testing")
public class HelloJobTicket extends AbstractJobTicket {

	@SimpleDBAttribute(attributeName = "userName")
	public String userName;

	@Override
	public void prepareJob() throws Exception {
		String[] users = { "Ken", "Sachiko", "John" };
		Random rand = new Random();
		int idx = rand.nextInt(users.length);
		this.userName = users[idx];
		System.out.println(String.format("%s is waiting...", this.userName));
	}

	@Override
	public void executeJob() throws Exception {
		System.out.println(String.format("%s says, Hello World!!", this.userName));
		Thread.sleep(1000);
	}

}
