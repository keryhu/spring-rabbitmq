package com.example.rabbit;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@EnableRabbit
@Component
public class FanoutReceive1 {
	
	/**
	 * 监听queue1消息
	 * @param message，监听的消息内容
	 */
	@RabbitListener(queues = "fanout-queue1")
	public void receiveMessage(Message message){
		System.out.println("fanout-queue1 -- "+message+" >");
		
		
		
	}
	
	
	/**
	 * 监听queue2消息
	 * @param message，监听的消息内容
	 */
	@RabbitListener(queues = "fanout-queue2")
	public void receiveMessage2(Message message){
		System.out.println("fanout-queue2 -- "+message+" >");
		
		
		
	}
	
   

}
