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

/**
 * 
 * @author Takehito Tanabe (dateofrock at gmail dot com)
 */
class Reflector {

	String findQueueName(Class<? extends AbstractJobTicket> clazz) {
		SQSJobTicket anno = clazz.getAnnotation(SQSJobTicket.class);
		if (anno == null) {
			throw new SimpleSQSException("SQSJobTicketアノテーションがありません");
		}
		String queueName = anno.queueName();
		if (queueName == null || queueName.isEmpty()) {
			throw new SimpleSQSException("queueNameがありません");
		}
		return queueName;
	}

}
