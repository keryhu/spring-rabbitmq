package com.example.rabbit;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.amqp.core.Queue;
/**
 * 自己创建一个url，实现send rabbitmq message的方法调用，
 * @author hushuming
 *
 */
@RestController
public class Sender {
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Autowired
	RetrySend retrySend;
	
	@Autowired
	TopicRabbitConfig  topicRabbitConfig;
	
	
	
	@Autowired
	@Qualifier("replyQueue")
	Queue replyQueue;
	
	
	/**
	 * 用来验证topic exchange的发送模式，一个exchange只能捆绑一个queue，所以这个是point-to-point方式
	 * @return
	 */
	
	
	@RequestMapping("/emit")
	
	public  String send_topic(){
		//发布消息的时候，如果实行json2格式，会达不到预期。如果发送的是message对象，则重新试下。
		//调用send 方法。第一个参数是route－key，第二个参数是发送的message具体的内容
		
		Message message=MessageBuilder.withBody("This is my first message".getBytes())
				.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
				.setMessageId("123456")
				//这个userId，需要在amqp中建立用户，然后用户有读取 vhost的权限
				.setUserId("wfij")
				//.setReplyTo("my.reply.queue")
				.setHeader("bar", "baz")
				.setCorrelationId(UUID.randomUUID().toString().getBytes())
				.build();
		
		rabbitTemplate.setConfirmCallback(new ConfirmCallback(){
			@Override
			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
				// TODO Auto-generated method stub
				//如果发送message失败，那么就重试。
				if(!ack){
					retrySend.send("spring-boots5", message,new CorrelationData(UUID.randomUUID().toString()));
					//以后加上log日志，报错，Log.ERROR()
					System.out.println("confirm correlationData is : "+correlationData+"  , ack is : "+
							ack);
				}
				
			}
			
		});
		rabbitTemplate.setReturnCallback(new ReturnCallback(){

			@Override
			public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
				// TODO Auto-generated method stub
				System.out.println("returned message is : "+message);
				System.out.println("Received returnedMessage with result {}"
	                    + routingKey);
			}
			
		});
		//设置发送confirm和回复的必须要设置下面这个为true
		rabbitTemplate.setMandatory(true);
		//设置rabbitTemplate的retry 功能
		
		String result = (String)rabbitTemplate.convertSendAndReceive("spring-boots5", message,new CorrelationData(UUID.randomUUID().toString()));

		System.out.println("result is  : "+result);
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
		Message message=MessageBuilder.withBody("This is my first message".getBytes())
				.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
				.setMessageId("123456")
				//这个userId，需要在amqp中建立用户，然后用户有读取 vhost的权限
				.setUserId("wfij")
				.setReplyTo("my.reply.queue")
				.setHeader("bar", "baz")
				.setCorrelationId(UUID.randomUUID().toString().getBytes())
				.build();
		//发布消息的时候，如果实行json2格式，会达不到预期。如果发送的是message对象，则重新试下。
		//调用send 方法。
		//第一个参数是，fanout－exchange的名字，第二个参数应该是 route-key，但是因为fanout exchange不需要
		//rout-key，所以设置为空的string
		rabbitTemplate.convertAndSend("fanout","", message);
		
		return "fanout message to queue1";
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping("/o")
	public String o(){
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<CorrelationData> confirmCD = new AtomicReference<CorrelationData>();
		rabbitTemplate.setConfirmCallback(new ConfirmCallback() {

			@Override
			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
				confirmCD.set(correlationData);
				System.out.println("correlationData is : "+correlationData);
				//latch.countDown();
			}
		});
		CorrelationData correlationData = new CorrelationData("abc");
		rabbitTemplate.setMandatory(true);
		//rabbitTemplate.setReplyAddress(replyQueue.getName());
		System.out.println(" is : "+rabbitTemplate.convertSendAndReceive("my.request.queue",(Object)"hello world !",correlationData));
		System.out.println("confirmCD.get() is : "+confirmCD.get());
		return "this. is test rabbitConnectionFactory value ";
	}

}
