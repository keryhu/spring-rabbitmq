package com.example.rabbit;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * spring rabbitmq receiver 的接收器
 * @author hushuming
 *
 */
@EnableRabbit
@Component

public class Receiver {
	
	@RabbitListener(queues = "spring-boots5")
	public void receiveMessage1(Message message){
		System.out.println("0000000 nihaonihao -- "+message+" >");
		//接受消息是，打印的是这行内容，其中message 是由 rabbitTemplate的send 的第二个参数来 填充。
		//如果message内容不为空，那么就调用另外一个方法，print(),这个非常有用，接受到什么消息，做出什么决定，
		//例如用户注册成功后，促发邮件验证功能。
		if(message.getBody()!=null){
			print();
		}
	
		
	}
	
	
    public void  print(){
    	System.out.println("正在调用print方法");
    }
	
    
    @RabbitListener(queues = "my.request.queue")
	public String receiveMessage2(String s){
		
    	return s.toUpperCase();
		
	}
   

}
