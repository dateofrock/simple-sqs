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

import java.util.Random;

import com.dateofrock.simpledbmapper.SimpleDBAttribute;
import com.dateofrock.simpledbmapper.SimpleDBEntity;

/**
 * 
 * @author Takehito Tanabe (dateofrock at gmail dot com)
 */
@SQSJobTicket(queueName = "SimpleSQS-HelloJobTicket")
@SimpleDBEntity(domainName = "SimpleSQS-HelloJobTicket", s3BucketName = "dateofrock-testing", s3KeyPrefix = "SimpleSQS-HelloJobTicket")
public class HelloJobTicket extends AbstractJobTicket {

	@SimpleDBAttribute(attributeName = "userName")
	public String userName;

	@Override
	public void prepare() throws Exception {
		String[] presenResult = { "スベった", "ドン引きされた", "感動巨編だった" };
		Random rand = new Random();
		int idx = rand.nextInt(presenResult.length);
		this.userName = presenResult[idx];
		System.out.println(String.format("プレゼン結果を準備しました...", this.userName));
	}

	@Override
	public void execute() throws Exception {
		System.out.println(String.format("あなたのプレゼンは、「%s」ようです。", this.userName));
		Thread.sleep(1000);
	}

	@Override
	public void processIfInProgress() {
		// TODO
	}

}
