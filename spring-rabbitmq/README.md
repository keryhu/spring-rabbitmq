# spirng-rabbitmq-docker

this is just a spring rabbitmq docker demo  .

rabbitmq : 3-management  

in docker-compose.yml:

	rabbitmq:
		    image: rabbitmq:3-management
		    container_name: rabbitmq
		    hostname:  rabbitmq
		    restart: always
		    #此环境变量设置rabbitmq 的初始 用户，密码，和vhost，并且删除了默认的guest用户
		    #此变量与spring－service中的变量设置一致，一同修改
		    environment:
		      RABBITMQ_DEFAULT_USER: wfij
		      RABBITMQ_DEFAULT_PASS: kkslcm8923s
		      RABBITMQ_DEFAULT_VHOST: /wfij
		    ports:
		      - "5672:5672"
		      - "15672:15672"
		    networks:
		      - wfij


above ,it create a new user and password .and delelte default "guest":"guest" user account . And create a new vhost "/wfij",and set the wfij permission to access ,read and write Authorization

实现的功能：
 1 topic exchange，点对点的消息发送和接受
 2 fanout exchange  广播式消息发送，1个发送体，多个接受者的功能。
 3 增加了发送消息的retry功能
	