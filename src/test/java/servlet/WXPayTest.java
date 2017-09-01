package servlet;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import wechat.pay.service.exception.HttpRequestFailedException;
import wechat.pay.service.util.HttpUtil;
import wechat.pay.service.util.WXPay.WXPayUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lichenyi
 * @date 2017-8-31 0031
 */
public class WXPayTest {

    @Test
    public void getQRCodeServletTest(){
        Map<String, Object> map = new HashMap<>();
        map.put("url", "http://wechat.lichenyi.cn/index?");//回调url。 请求方式post， 返回的参数样例（http://wechat.lichenyi.cn/index?】, parameter --> 【{"transaction_id":"4006892001201709019539268994","out_trade_no":"wx20170911sdfadfaadg","total_fee":"1","return_code":"SUCCESS"}）
        map.put("platform", "wechat");                      //调用平台
        map.put("descrition", "支付描述测试");                //支付描述测试

        List<HttpUtil.Parameter> list = new ArrayList<>();
        list.add(new HttpUtil.Parameter("total_fee", "1"));                     //支付金额
        list.add(new HttpUtil.Parameter("order_id", WXPayUtil.generateOrderId()));      //订单id
        list.add(new HttpUtil.Parameter("body", "二维码支付模式二"));              //商品描述
        list.add(new HttpUtil.Parameter("product_id", "11"));                   //商品id
        list.add(new HttpUtil.Parameter("detail", "商品详情描述"));               //商品详情
        list.add(new HttpUtil.Parameter("attach", JSON.toJSONString(map)));             //自定义参数
        try {
            String result = HttpUtil.postString("http://pay.wechat.lichenyi.cn/getQRCode", list);
            System.out.println(result);
        } catch (HttpRequestFailedException e) {
            e.printStackTrace();
        }
    }

}
