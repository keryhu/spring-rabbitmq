package com.example.rabbit;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 自己创建一个url，实现send rabbitmq message的方法调用，
 * @author hushuming
 *
 */
@RestController
public class Sender {
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	Message message=MessageBuilder.withBody("This is my first message".getBytes())
			.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
			.setMessageId("123456")
			.setHeader("bar", "baz")
			.build();
	/**
	 * 用来验证topic exchange的发送模式，一个exchange只能捆绑一个queue，所以这个是point-to-point方式
	 * @return
	 */
	
	@RequestMapping("/emit")
	public String send_topic(){
		//发布消息的时候，如果实行json2格式，会达不到预期。如果发送的是message对象，则重新试下。
		//调用send 方法。第一个参数是route－key，第二个参数是发送的message具体的内容
		rabbitTemplate.convertAndSend("spring-boots5", message);
		//返回的对象，证明spring controller url正确
		return "emit to queue 1";
	}
	
	/**
	 * 用来验证fanout exchange的发送模式，只要发送一次到exchange，因为exchange binding 了 两个queue1 和
	 * queue2，所以只要发送一条消息，发送一次，那么监听queue1和queue2 队列的 listener将 获取到新消息
	 * @return
	 */
	@RequestMapping("/out")
	public String send_fanout(){
		//发布消息的时候，如果实行json2格式，会达不到预期。如果发送的是message对象，则重新试下。
		//调用send 方法。
		//第一个参数是，fanout－exchange的名字，第二个参数应该是 route-key，但是因为fanout exchange不需要
		//rout-key，所以设置为空的string
		rabbitTemplate.convertAndSend("fanout","", message);
		
		return "fanout message to queue1";
	}

}
