package com.atguigu.mapper;

import com.atguigu.entiy.OrderItem;
import com.atguigu.entiy.OrderVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    @Select(value = {"SELECT a.order_no,SUM(b.price*b.count) amount from t_order a " ,
            "left JOIN t_order_item b " ,
            "on a.order_no = b.order_no" ,
            "GROUP BY a.order_no"})
    List<OrderVo> getOrderAmount();
}
