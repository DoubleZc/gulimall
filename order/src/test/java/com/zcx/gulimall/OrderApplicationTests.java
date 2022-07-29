package com.zcx.gulimall;

import com.zcx.gulimall.order.OrderApplication;
import com.zcx.gulimall.order.entity.OrderReturnApplyEntity;
import com.zcx.gulimall.order.web.HelloController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Slf4j
@SpringBootTest(classes = OrderApplication.class)
class OrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;
    
    
    @Autowired
    RabbitTemplate rabbitTemplate;
    
    /***
     *
     * 创建交换机
     */
    @Test
    void exchange() {
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        //  交换机：名字         是否持久化       是否自动删除               参数
        DirectExchange directExchange = new DirectExchange("hello.java.exchange",true,false);
        amqpAdmin.declareExchange(directExchange);
        log.info("exchange:{}",directExchange.getName());
    
    }
    
    @Test
    void createqueue()
    {
    //String name, boolean durable, boolean exclusive, boolean autoDelete,@Nullable Map<String, Object> arguments
    //名字         是否持久化           是否排他                   是否自动删除               参数
        
        Queue queue = new Queue("hello-java-queue",true,false,false);
        String s = amqpAdmin.declareQueue(queue);
        log.info("queue:{}",queue.getName());
        log.info(s);
    }
    
    
    @Test
    void binding()
    {
        
        //String destination,   目的地     队列名
        // DestinationType destinationType,   目的地类型
        // String exchange,  交换机
        // String routingKey,   路由键
        //@Nullable Map<String, Object> arguments  参数
        
        Binding binding = new Binding("hello-java-queue", Binding.DestinationType.QUEUE,"hello.java.exchange","hello.java",null);
        amqpAdmin.declareBinding(binding);
        log.info("绑定成功{}",binding.getDestination());

    }
    
    
    @Autowired
    HelloController executor;
    
    @Test
    void sendMessage(){
    
        System.out.println(executor);
    
    }
    
    
    
    
    

}
