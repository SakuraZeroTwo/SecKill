package com.seckill.lab.seckillsystem.service;

import com.seckill.lab.seckillsystem.config.RabbitMQConfig;
import com.seckill.lab.seckillsystem.entity.OrderInfo;
import com.seckill.lab.seckillsystem.entity.Product;
import com.seckill.lab.seckillsystem.entity.User;
import com.seckill.lab.seckillsystem.repository.OrderInfoRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService {

    @Autowired
    private OrderInfoRepository orderInfoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 防止重复下单的Redis键前缀
    private static final String ORDER_KEY_PREFIX = "order:user:";

    public OrderInfo createOrder(OrderInfo orderInfo) {
        return orderInfoRepository.save(orderInfo);
    }

    // 发送秒杀请求到消息队列
    public boolean sendSeckillMessage(Long userId, Long productId, Long activityId) {
        // 检查是否重复下单
        String orderKey = ORDER_KEY_PREFIX + userId + ":" + productId;
        Boolean notRepeated = redisTemplate.opsForValue().setIfAbsent(orderKey, 1, 10, TimeUnit.MINUTES);

        if (Boolean.TRUE.equals(notRepeated)) {
            // 创建消息
            Map<String, Object> message = new HashMap<>();
            message.put("userId", userId);
            message.put("productId", productId);
            message.put("activityId", activityId);
            message.put("createTime", new Date());

            // 发送消息
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.SECKILL_EXCHANGE,
                    RabbitMQConfig.SECKILL_ROUTING_KEY,
                    message
            );
            return true;
        }
        return false;
    }
}