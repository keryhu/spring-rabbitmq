package com.example.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
/**
 * 测试topic exchange
 * @author hushuming
 *
 */
public class TopicRabbitConfig {
	
	final static String queueName = "spring-boots5";
	
	
	/**
	 Queue(String name)
The queue is durable, non-exclusive and non auto-delete.

Queue(String name, boolean durable)
Construct a new queue, given a name and durability flag.

Queue(String name, boolean durable, boolean exclusive, boolean autoDelete)
Construct a new queue, given a name, durability, exclusive and auto-delete flags.

Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String,Object> arguments)
Construct a new queue, given a name, durability flag, and auto-delete flag, and arguments.
	
	 */
	//excluseive 表示 if the server should only send messages to the declarer's connection.
	@Bean
	Queue queue() {
		return new Queue(queueName, false,false, false, null);
	}
	
	
	/**
	 TopicExchange(String name) 
TopicExchange(String name, boolean durable, boolean autoDelete) 
TopicExchange(String name, boolean durable, boolean autoDelete, Map<String,Object> arguments) 
	 */
	@Bean
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange",true, false);
	}
	
	
	
	 

	//binding q queue to a topicExchange with "${queneName}"  refer to route key
	//this is a fluent API
	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(queueName);
	}

	@Bean
	//configure a routing connection factory ,this is a long-lived container
	SimpleMessageListenerContainer container(CachingConnectionFactory connectionFactory, @Qualifier("topicListenerAdapter")MessageListenerAdapter listenerAdapter) {
		//设置的channelCacheSize 不能小于  application.yml中的concurrency: 3
		connectionFactory.setChannelCacheSize(5);
		//connectionFactory.setPublisherConfirms(true);
		//connectionFactory.setPublisherReturns(true);
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		//the lookup key is queueName
		container.setQueueNames(queueName);
		//接受消息时候，json格式
		container.setMessageConverter(json2MessageConverter());
		//如果接听者接听失败，消息将回滚
		//Boolean flag to signal that all messages should be acknowledged in a transaction (either manually or automatically)
		container.setChannelTransacted(true);
		//it can cause messages already consumed not to be acknowledged until the timeout expires.
		container.setReceiveTimeout(3000);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	
	
	
	
	@Bean
    public MessageConverter json2MessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    
    /**
    Create a new MessageListenerAdapter for the given delegate.
    调用的是receive中 receiveMessage 方法。 其中receiver 是自己创建的，
   
    
     */
	 @Bean
		MessageListenerAdapter topicListenerAdapter(Receiver receiver) {
		 //接受到消息后，调用receiveMessage方法
			return new MessageListenerAdapter(receiver, "receiveMessage1");
		}
	 
	


}
