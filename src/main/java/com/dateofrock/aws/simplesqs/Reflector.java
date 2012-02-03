package com.dateofrock.aws.simplesqs;

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
