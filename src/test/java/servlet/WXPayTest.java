package servlet;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import wechat.pay.service.exception.HttpRequestFailedException;
import wechat.pay.service.util.HttpUtil;

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
        map.put("url", "http://wechat.lichenyi.cn/index?");
        map.put("platform", "wechat");
        map.put("descrition", "支付描述测试");
        List<HttpUtil.Parameter> list = new ArrayList<>();
        list.add(new HttpUtil.Parameter("total_fee", "1"));
        list.add(new HttpUtil.Parameter("openid", "oVjO401S54VajTRlz_0GJcX8Cods"));
        list.add(new HttpUtil.Parameter("body", "二维码支付模式二"));
        list.add(new HttpUtil.Parameter("product_id", "11"));
        list.add(new HttpUtil.Parameter("detail", "商品详情描述"));
        list.add(new HttpUtil.Parameter("attach", JSON.toJSONString(map)));
        try {
            String result = HttpUtil.postString("http://pay.wechat.lichenyi.cn/getQRCode", list);
            System.out.println(JSON.parseObject(result, Map.class));
        } catch (HttpRequestFailedException e) {
            e.printStackTrace();
        }
    }

}
