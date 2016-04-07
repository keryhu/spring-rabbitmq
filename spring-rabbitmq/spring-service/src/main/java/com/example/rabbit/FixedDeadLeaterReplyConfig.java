package com.example.rabbit;

import org.springframework.amqp.core.DirectExchange;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FixedDeadLeaterReplyConfig {
	
	@Bean
	public DirectExchange ex() {
		return new DirectExchange("dlx.test.requestEx", false, true);
	}
	
	/**
	 * 
	 * @param rabbitConnectionFactory
	 * @return
	 * 
	 * @Bean
	public RabbitTemplate fixedReplyQrabbitTemplate(CachingConnectionFactory rabbitConnectionFactory){
		RabbitTemplate template=new RabbitTemplate(rabbitConnectionFactory);
		template.setExchange(ex().getName());
		template.setRoutingKey("dlx.reply.test");
		return template;
		
		
	}
	 */
	
	

}
