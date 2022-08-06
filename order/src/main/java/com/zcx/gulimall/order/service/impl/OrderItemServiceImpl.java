package com.zcx.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;
import com.zcx.gulimall.order.dao.OrderItemDao;
import com.zcx.gulimall.order.entity.OrderItemEntity;
import com.zcx.gulimall.order.service.OrderItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

   /* @RabbitHandler
    public void receiveMessage(Message msg, String orderReturnApplyEntity, Channel channel) throws InterruptedException
    {
        long deliveryTag = msg.getMessageProperties().getDeliveryTag();
        System.out.println(deliveryTag+"deli");
        try {
            
            //签收                tag             是否批量签收
            channel.basicAck(deliveryTag,false);
            
            //拒收                      tag    是否批量   requeue=true 发回交换机,重新入队  false丢弃
       //    channel.basicNack(deliveryTag,false,true);
        
        } catch (IOException e) {
            e.printStackTrace();
        }

    
        log.info("消息：{}",orderReturnApplyEntity);
        
        
    
    }
    
    @RabbitHandler
    public void receiveMessage(Message msg,OrderReturnApplyEntity orderReturnApplyEntity) throws InterruptedException
    {
        
        log.info("消息：{}",orderReturnApplyEntity);
        
    }
    */
    
    
}