package com.example.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;


/**
 * 这个方法的作用是，如果发送message的时候，ack是false，那么就尝试重发。
 * @author hushuming
 *
 */
@EnableRetry
@Configuration
public class RetrySend {

	@Autowired
	RabbitTemplate rabbitTemplate;

	// maxAttempts 最多尝试的次数
	// backoff 意思是发生冲突或者失败后，等待一段时间再试
	// 下面的意思在失败后，等待100 and 2000 milliseconds后，进行最大尝试4次。
	// 可以设置value＝xxException，就是在某个exception出现的时候，重试
	@Retryable(maxAttempts = 4, backoff = @Backoff(delay = 100, maxDelay = 2000))
	void send(String routingKey, Object object, CorrelationData correlationData) {

		rabbitTemplate.convertAndSend(routingKey, object,correlationData);
		
	}

}
