package com.example.rabbit;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@EnableRabbit
@Component
public class PublisherCallbackReceive {
	
	//接受哪一个queue的消息，此时加入queue的名字。此方法是异步的
		@RabbitListener(queues = "spring-boots1")
		public void receiveMessage(String message){
			//接受消息是，打印的是这行内容，其中message 是由 rabbitTemplate的send 的第二个参数来 填充。
			System.out.println("Received from queue spring-boots1  < "+message+" >");
			
		}
		

}
