package com.example.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@RequestMapping("/emit")
	public String send(){
		//调用send 方法。第一个参数是route－key，第二个参数是发送的message具体的内容
		rabbitTemplate.convertAndSend("spring-boots1", "{test: 'OK'}");
		//返回的对象，证明spring controller url正确
		return "emit to queue 1";
	}

}
