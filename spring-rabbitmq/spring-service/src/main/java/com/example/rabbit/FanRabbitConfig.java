package com.example.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 测试amqp的 fanout exchange broadcast功能
 * @author hushuming
 *
 */
@Component
public class FanRabbitConfig {
	
	final static String queueName1 = "fanout-queue1";
	final static String queueName2 = "fanout-queue2";
	
	/**
	 * 第一个参数是，队列的名字，第二个参数是是否durable，第三个参数是否exclusive（是否独有的queue)
	 * 第三个参数是否autoDelete，第四个参数是 map
	 * @return
	 */
	@Bean
	Queue queue1() {
		return new Queue(queueName1, true,false, true, null);
	}
	
	@Bean
	Queue queue2() {
		return new Queue(queueName2, true,false, true, null);
	}
	
	
	/**
	 * 定义一个广播fanout exchange，测试多个通道捆绑一起
	 * @return
	 */
	@Bean
	public FanoutExchange fanout() {
		return new FanoutExchange("fanout",true,true);
	}
	
	/**
	 * 将queue1和queue2绑定到 fanout exchange上。有几个queue需要绑定，那么就需要几个binding
	 * @return
	 */
	@Bean
    public Binding trashRouteBinding() {
        return BindingBuilder.bind(queue1()).to(fanout());
    }

    @Bean
    public Binding webAppBinding() {
        return BindingBuilder.bind(queue2()).to(fanout());
    }

 /**
  * 设置message converter
  * @return
  */
	@Bean
    public MessageConverter json2MessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
	//configure a routing connection factory ,this is a long-lived container
   /**
    * 
    * @param connectionFactory  含有在application.yml中配置信息的connection 设置
    * @param listenerAdapter   指监听queue1 的 适配器配置
    * @param listenerAdapter2  指监听queue2 的 适配器配置
    * @return  返回spring amqp的container
    */
	SimpleMessageListenerContainer container(CachingConnectionFactory connectionFactory, 
			@Qualifier("fanoutListenerAdapter")MessageListenerAdapter listenerAdapter,
			@Qualifier("fanoutListenerAdapter2")MessageListenerAdapter listenerAdapter2) {
		//设置的channelCacheSize 不能小于  application.yml中的concurrency: 3
		connectionFactory.setChannelCacheSize(5);
		//connectionFactory.setPublisherConfirms(true);
		//connectionFactory.setPublisherReturns(true);
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		//将queue1和queue2 都加载到 container中
		container.setQueueNames(queueName1,queueName2);
		//接受消息时候，json格式
		container.setMessageConverter(json2MessageConverter());
		//如果接听者接听失败，消息将回滚
		//Boolean flag to signal that all messages should be acknowledged in a transaction (either manually or automatically)
		container.setChannelTransacted(true);
		//it can cause messages already consumed not to be acknowledged until the timeout expires.
		container.setReceiveTimeout(3000);
		//将两个queue 监听器，设置到container上。
		container.setMessageListener(listenerAdapter);
		container.setMessageListener(listenerAdapter2);
		return container;
	}
    
    @Bean
	MessageListenerAdapter fanoutListenerAdapter(FanoutReceive1 receiver) {
	 //接受到消息后，调用receiveMessage方法
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
    
    @Bean
   	MessageListenerAdapter fanoutListenerAdapter2(FanoutReceive1 receiver) {
   	 //接受到消息后，调用receiveMessage2方法
   		return new MessageListenerAdapter(receiver, "receiveMessage2");
   	}
    
}
