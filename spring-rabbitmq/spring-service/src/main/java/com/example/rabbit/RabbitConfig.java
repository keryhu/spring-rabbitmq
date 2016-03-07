package com.example.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitConfig {
	
	final static String queueName = "spring-boots1";
	

	
	
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
		return new Queue(queueName, true,false, false, null);
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
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		//the lookup key is queueName
		container.setQueueNames(queueName);
	
		container.setMessageListener(listenerAdapter);
		return container;
	}

    @Bean
    Receiver receiver() {
        return new Receiver();
    }
    
    /**
    Create a new MessageListenerAdapter for the given delegate.
    调用的是receive中 receiveMessage 方法。 其中receiver 是自己创建的，
     */
	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
	
	


}
