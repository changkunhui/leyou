package com.leyou.order.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.wxpay.sdk.WXPayUtil;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.threadlocals.UserHolder;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SkuDTO;
import com.leyou.order.config.PayProperties;
import com.leyou.order.dto.CartDTO;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.entity.TbOrder;
import com.leyou.order.entity.TbOrderDetail;
import com.leyou.order.entity.TbOrderLogistics;
import com.leyou.order.enums.BusinessTypeEnum;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.mapper.TbOrderMapper;
import com.leyou.order.service.TbOrderDetailService;
import com.leyou.order.service.TbOrderLogisticsService;
import com.leyou.order.service.TbOrderService;
import com.leyou.order.vo.OrderDetailVO;
import com.leyou.order.vo.OrderLogisticsVO;
import com.leyou.order.vo.OrderVO;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.UserAddressDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author changkunhui
 * @since 2019-12-25
 */
@Service
public class TbOrderServiceImpl extends ServiceImpl<TbOrderMapper, TbOrder> implements TbOrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private TbOrderDetailService orderDetailService;

    @Autowired
    private TbOrderLogisticsService orderLogisticsService;

    /**
     * 保存订单信息,订单表,订单详情表,物流信息三个表的数据
     * @param order
     * @return
     */
    @Override
    @Transactional
    public Long saveOrder(OrderDTO order) {
        TbOrder tbOrder = new TbOrder();

        // 使用snowflake生成订单id
        long orderId = idWorker.nextId();
        tbOrder.setOrderId(orderId);
        //设置userId
        String userId = UserHolder.getUserId();
        tbOrder.setUserId(Long.parseLong(userId));
        tbOrder.setSourceType(2);// '订单来源：1:app端，2：pc端，3：微信端',
        tbOrder.setStatus(OrderStatusEnum.INIT.value());//订单状态,未支付
        tbOrder.setBType(BusinessTypeEnum.MALL.value());//'订单业务类型1- 商城订单 2、秒杀订单',

        //金额相关
        List<CartDTO> carts = order.getCarts();
        List<Long> skuIdList = carts.stream().map(CartDTO::getSkuId).collect(Collectors.toList());//获取skuId的集合
        //将skuId作为键,数量作为值,整理为一个map集合
        Map<Long, Integer> skuIdAndNumMap = carts.stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));

        List<SkuDTO> skuDTOList = itemClient.findSkuListByIds(skuIdList);

        Long totalFee = 0L;
        TbOrderDetail orderDetail = new TbOrderDetail();
        ArrayList<TbOrderDetail> orderDetailList = new ArrayList<>();
        for (SkuDTO skuDTO : skuDTOList) {
            Integer num = skuIdAndNumMap.get(skuDTO.getId());
            totalFee += (skuDTO.getPrice() * num);

            //订单详情表单信息
            orderDetail.setOrderId(orderId);
            orderDetail.setSkuId(skuDTO.getId());
            orderDetail.setNum(num);
            orderDetail.setTitle(skuDTO.getTitle());
            orderDetail.setOwnSpec(skuDTO.getOwnSpec());
            orderDetail.setPrice(skuDTO.getPrice());
            orderDetail.setImage(StringUtils.substringBefore(skuDTO.getImages(),","));
            orderDetailList.add(orderDetail);
        }

        tbOrder.setTotalFee(totalFee);//'总金额，单位为分',
        tbOrder.setPostFee(0L);//邮费。单位:分
        tbOrder.setActualFee(tbOrder.getTotalFee()+tbOrder.getPostFee());//'实付金额。单位:分
        tbOrder.setPaymentType(order.getPaymentType());//支付类型,页面传过来的


        //保存物流表的信息
        UserAddressDTO userAddress = userClient.findAddressById(order.getAddressId());
        TbOrderLogistics tbOrderLogistics = BeanHelper.copyProperties(userAddress, TbOrderLogistics.class);
        tbOrderLogistics.setOrderId(orderId);//设置orderId


        //执行保存
        boolean saveOrder = this.save(tbOrder);
        if(!saveOrder){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

        boolean saveDetail = orderDetailService.saveBatch(orderDetailList);
        if(!saveDetail){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

        boolean saveLogistics = orderLogisticsService.save(tbOrderLogistics);
        if(!saveLogistics){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

        //减库存
        itemClient.stockMinus(skuIdAndNumMap);

        return orderId;
    }

    /**
     * 根据订单id查询订单信息
     * @param id
     * @return
     */
    @Override
    public OrderVO findOrderById(Long id) {
        String userId = UserHolder.getUserId();
        TbOrder order = findOrder(id, Long.parseLong(userId), OrderStatusEnum.INIT.value());
        if(order==null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        OrderVO orderVO = BeanHelper.copyProperties(order, OrderVO.class);

        //设置订单详情数据
        QueryWrapper<TbOrderDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbOrderDetail::getOrderId,id);
        List<TbOrderDetail> orderDetailList = orderDetailService.list(queryWrapper);

        orderVO.setDetailList(BeanHelper.copyWithCollection(orderDetailList,OrderDetailVO.class));

        //设置物流信息数据
        TbOrderLogistics orderLogistics = orderLogisticsService.getById(id);
        orderVO.setLogistics(BeanHelper.copyProperties(orderLogistics,OrderLogisticsVO.class));

        return orderVO;
    }

    @Autowired
    private PayProperties prop;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String prefix = "ly:pay:url:";
    /**
     * 生成支付链接
     * @param orderId
     * @return
     */
    @Override
    public String getPayUrl(Long orderId) {


        //判断用户是否正确
        String userId = UserHolder.getUserId();
        if(StringUtils.isEmpty(userId)){
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }

        String codeUrl = null;

        //先从redis中获取支付链接
        codeUrl = redisTemplate.boundValueOps(prefix + orderId).get();
        if(StringUtils.isNotBlank(codeUrl)){
            return codeUrl;
        }

        //查询订单
        TbOrder tbOrder = this.getById(orderId);

        //构建参数
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("appid",prop.getAppID());
        paramMap.put("mch_id",prop.getMchID());
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
        paramMap.put("body","乐优商城");
        paramMap.put("out_trade_no",orderId.toString());
        // paramMap.put("total_fee",tbOrder.getActualFee().toString());//真正的总金额
        paramMap.put("total_fee","1");//TODO 总金额,正规应该从商品中来,这里测试使用1分
        paramMap.put("spbill_create_ip","127.0.0.1");
        paramMap.put("notify_url",prop.getNotifyurl());
        paramMap.put("trade_type","NATIVE");


        try {
            //将参数转为xml
            String paramXml = WXPayUtil.generateSignedXml(paramMap, prop.getKey());

            //发送请求,调用微信支付接口
            String resultXml = restTemplate.postForObject(url, paramXml, String.class);

            //转为Map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);

            codeUrl = resultMap.get("code_url");
            //支付链接有效时间两个小时,放到redis中
            redisTemplate.boundValueOps(prefix+orderId).set(codeUrl,2,TimeUnit.HOURS);

            return codeUrl;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 查询支付状态
     * @param orderId
     * @return
     */
    @Override
    public Integer queryPayState(Long orderId) {
        //判断用户是否正确
        String userId = UserHolder.getUserId();
        if(StringUtils.isEmpty(userId)){
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }

        //构建参数
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("appid",prop.getAppID());
        paramMap.put("mch_id",prop.getMchID());
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
        paramMap.put("out_trade_no",orderId.toString());


        try {
            //将参数转为xml
            String paramXml = WXPayUtil.generateSignedXml(paramMap, prop.getKey());

            //发送请求,调用微信支付接口
            String resultXml = restTemplate.postForObject(url, paramXml, String.class);

            //转为Map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);

            String tradeState = resultMap.get("trade_state");
            if ("SUCCESS".equals(tradeState)){
                //支付成功,修改订单状态和支付时间
                TbOrder tbOrder = new TbOrder();
                tbOrder.setOrderId(orderId);
                tbOrder.setStatus(OrderStatusEnum.PAY_UP.value());//设置订单状态已经支付
                tbOrder.setPayTime(new Date());
                boolean isUpdate = this.updateById(tbOrder);
                if(!isUpdate){
                    throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
                }
                return 1;
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 清理一个小时未支付的订单
     */
    @Override
    @Transactional
    public void cleanOverTimeOrder() {
        //查询订单详情表,获取需要恢复库存的商品和数量
        List<TbOrderDetail> orderDetailList = orderDetailService.findOvertimeOrderDetail();
        Map<Long, Integer> skuIdNumMap = orderDetailList.stream().collect(Collectors.toMap(TbOrderDetail::getSkuId, TbOrderDetail::getNum));

        if(CollectionUtils.isEmpty(orderDetailList)){
            return;
        }
        //更新订单,将这些订单的状态改为5
        //update tb_order set status = 5 where status = 1 and TIMESTAMPDIFF(MINUTE,create_time,now()) > 60
        this.getBaseMapper().closeOvertimeOrder();

        //恢复库存
        itemClient.stockPlus(skuIdNumMap);
    }


    private TbOrder findOrder(Long orderId,Long userId,Integer status){
        QueryWrapper<TbOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbOrder::getOrderId,orderId).eq(TbOrder::getUserId,userId).eq(TbOrder::getStatus,status);
        TbOrder tbOrder = this.getOne(queryWrapper);
        return tbOrder;
    }
}
