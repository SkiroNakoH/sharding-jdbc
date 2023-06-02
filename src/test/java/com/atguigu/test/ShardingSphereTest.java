package com.atguigu.test;

import com.atguigu.entiy.*;
import com.atguigu.mapper.DictMapper;
import com.atguigu.mapper.OrderItemMapper;
import com.atguigu.mapper.OrderMapper;
import com.atguigu.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
class ShardingSphereTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private DictMapper dictMapper;

    //1.读写分离的测试
    @Test
    public void testInsert() {
        User user = new User();
        user.setUname("张三丰");
        userMapper.insert(user);
        List<User> userList1 = userMapper.selectList(null);
        List<User> userList2 = userMapper.selectList(null);
    }

    //2.测试事务
    @Transactional
    @Test
    public void testTransactional() {
        User user = new User();
        user.setUname("李四");
        userMapper.insert(user);
        List<User> userList1 = userMapper.selectList(null);
        List<User> userList2 = userMapper.selectList(null);
    }

    //3. 垂直拆分
    @Test
    public void testVerticalDatabase() {
        User user = new User();
        user.setUname("王五");
        userMapper.insert(user);

        Order order = new Order();
        order.setOrderNo("ATGUIGU00");
        order.setUserId(1L);
        order.setAmount(new BigDecimal("15"));
        orderMapper.insert(order);
    }

    //水平分片-行表达式
    @Test
    public void testLevelDatabase() {
        for (int i = 0; i < 4; i++) {
            Order order = new Order();
            order.setOrderNo("ATGUIGU00");
            order.setUserId((long) i);
            order.setAmount(new BigDecimal("15"));
            orderMapper.insert(order);
        }

    }

    @Test
    public void testSplitDbAndTb() {
        for (long i = 1; i < 5; i++) {
            Order order = new Order();
            order.setOrderNo("ATGUIGU" + i);
            order.setUserId(1L);
            order.setAmount(new BigDecimal(100));
            orderMapper.insert(order);
        }
        for (long i = 5; i < 9; i++) {
            Order order = new Order();
            order.setOrderNo("ATGUIGU" + i);
            order.setUserId(2L);
            order.setAmount(new BigDecimal(100));
            orderMapper.insert(order);
        }
    }


    //7.测试关联表插入
    @Test
    public void testInsertOrderAndOrderItem(){
        for (long i = 1; i < 3; i++) {
            Order order = new Order();
            order.setOrderNo("ATGUIGU" + i);
            order.setUserId(1L);
            orderMapper.insert(order);
            for (long j = 1; j < 3; j++) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderNo("ATGUIGU" + i);
                orderItem.setUserId(1L);
                orderItem.setPrice(new BigDecimal(10));
                orderItem.setCount(2);
                orderItemMapper.insert(orderItem);
            }
        }
        for (long i = 5; i < 7; i++) {
            Order order = new Order();
            order.setOrderNo("ATGUIGU" + i);
            order.setUserId(2L);
            orderMapper.insert(order);
            for (long j = 1; j < 3; j++) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderNo("ATGUIGU" + i);
                orderItem.setUserId(2L);
                orderItem.setPrice(new BigDecimal(1));
                orderItem.setCount(3);
                orderItemMapper.insert(orderItem);
            }
        }
    }

    //8.测试关联表查询
    @Test
    public void testRelevanceDB(){
        List<OrderVo> orderVoList = orderItemMapper.getOrderAmount();
        orderVoList.forEach(System.out::println);
    }

    //9.广播表
    @Test
    public void testBroadcast(){
        Dict dict = new Dict();
        dict.setDictType("apple pineapple");
        dictMapper.insert(dict);
    }
    @Test
    public void testFindBroadcast(){
      dictMapper.selectList(null);
      dictMapper.selectList(null);
    }
}
