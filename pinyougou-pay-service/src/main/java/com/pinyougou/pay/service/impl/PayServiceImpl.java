package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.utils.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: wangyilong
 * @Date: 2019/12/4 0004
 * @Description:  支付接口的实现类
 */
@Service
public class PayServiceImpl implements PayService {

    @Value("${appid}")
    private String appid;                                  //商户id
    @Value("${partner}")                                //商户号
     private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Value("${notifyurl}")
    private String notifyurl;

    //设置下单请求的url
    private String orderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //设置查询订单的url
    private String orderQueryUrl = "https://api.mch.weixin.qq.com/pay/orderquery";

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        try {
            //1 包装数据 创建参数
            Map paramMap = new HashMap();
            paramMap.put("appid",appid);                                                //公众号id
            paramMap.put("partner",partner);                                          //商户名
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr()); //随机字符串
            paramMap.put("body","品优购");                                           // 设置商品描述
            paramMap.put("spbill_create_ip","127.0.0.1");                      //终端ip
            paramMap.put("out_trade_no",out_trade_no);                        //订单号
            paramMap.put("total_fee",total_fee);                                       // 订单金额
            paramMap.put("trade_type","NATIVE ");                               // 支付方式
            paramMap.put("notifyurl",notifyurl);                                      //提示的url
            // 1. 2 将 map中的数据转换为xml格式
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //2 向后台发送下单请求
            HttpClient client = new HttpClient(orderUrl);
            //2.1 设置参数
            client.setHttps(true);                             //发出http请求
            client.setXmlParam(signedXml);           //设置请求参数
            client.post();                                         //发送请求
            //3 返回结果
            // 3.1 从微信后台获取返回的数据
            String content = client.getContent();
            //3.2 返回单的数据为xml格式，将xml格式的数转换为map
            Map<String, String> xmlMap = WXPayUtil.xmlToMap(content);
            //3.3 构造要返回的map集合 设置自定义的参数
            Map resultMap = new HashMap();
            resultMap.put("out_trade_no",out_trade_no);                        //订单号
            resultMap.put("total_fee",total_fee);
            resultMap.put("code_url",xmlMap.get("code_url"));
            //4 返回map结果
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap();
    }
    @Override
    public Map queryOrder(String out_trade_no) {
        try {
            //1 包装数据
            Map paramMap = new HashMap();
            paramMap.put("appid",appid);                                                //公众号id
            paramMap.put("partner",partner);                                          //商户名
            paramMap.put("out_trade_no",out_trade_no);                        //订单号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr()); //随机字符串
            //1.2 生成签名 格式为xml格式
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //2 向后台发送请求
            HttpClient client = new HttpClient(orderQueryUrl);
            client.setHttps(true);
            client.setXmlParam(signedXml);
            client.post();
            //3 得到请求}的数据并将结果返回
            //3.1 得到内容
            String content = client.getContent();
            //3.2 得到的内容为xml格式 将其转换为map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap();
    }
}
