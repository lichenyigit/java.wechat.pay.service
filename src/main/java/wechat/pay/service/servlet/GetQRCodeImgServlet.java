package wechat.pay.service.servlet;

import com.google.zxing.WriterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wechat.pay.service.exception.BaseException;
import wechat.pay.service.service.WechatService;
import wechat.pay.service.util.CommonUtils;
import wechat.pay.service.util.ConvertUtil;
import wechat.pay.service.util.QRUtils;
import wechat.pay.service.util.WXPay.WXPayConstants;
import wechat.pay.service.util.WXPay.WXPayUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

@SuppressWarnings("all")
@WebServlet(urlPatterns={"/getQRCodeImg"}, description = "生成预支付交易并根据交易链接（code_url）返回图片,扫码支付模式二")
public class GetQRCodeImgServlet extends HttpServlet {
	private static final Logger logger = LogManager.getLogger(GetQRCodeImgServlet.class);
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		Writer writer = null;
		OutputStream toStream = null;
		try {
			toStream = response.getOutputStream();
			String attach = ConvertUtil.getNonEmptyStringFromRequestParam(request, "attach");
			String body = ConvertUtil.getNonEmptyStringFromRequestParam(request, "body");
			String detail = ConvertUtil.getTrimStringFromRequestParam(request, "detail", "");
			String total_fee = ConvertUtil.getNonEmptyStringFromRequestParam(request, "total_fee");
			String product_id = ConvertUtil.getNonEmptyStringFromRequestParam(request, "product_id");
			String openid = ConvertUtil.getNonEmptyStringFromRequestParam(request, "openid");

			Map<String, Object> parametersMap = CommonUtils.createMap("appid", WXPayConstants.APP_ID);//appid
			CommonUtils.generateMap(parametersMap, "attach", attach);//附加数据
			CommonUtils.generateMap(parametersMap, "mch_id", WXPayConstants.PAY_STORE_ID);//商户号
			CommonUtils.generateMap(parametersMap, "device_info", WXPayConstants.DEVICE_INFO_DEFAULT.WEB.toString());//设备号 自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
			CommonUtils.generateMap(parametersMap, "nonce_str", WXPayUtil.generateNonceStr());
			CommonUtils.generateMap(parametersMap, "sign_type", WXPayConstants.SignType.MD5);
			CommonUtils.generateMap(parametersMap, "body", body);//商品描述
			CommonUtils.generateMap(parametersMap, "detail", detail);//商品详情
			CommonUtils.generateMap(parametersMap, "out_trade_no", WXPayUtil.generateOrderId());//自己系统的订单号
			CommonUtils.generateMap(parametersMap, "fee_type", WXPayConstants.FEE_TYPE.CNY);
			CommonUtils.generateMap(parametersMap, "total_fee", total_fee);//单位位分
			CommonUtils.generateMap(parametersMap, "spbill_create_ip", CommonUtils.getIpAddr(request));
			CommonUtils.generateMap(parametersMap, "notify_url", WXPayConstants.NOTIFY_URL);
			CommonUtils.generateMap(parametersMap, "trade_type", WXPayConstants.TRADE_TYPE.NATIVE.toString());
			CommonUtils.generateMap(parametersMap, "product_id", product_id);//商品id
			CommonUtils.generateMap(parametersMap, "openid", openid);//open id
			logger.info("统一支付参数：\n\r"+ parametersMap);
			Map<String, Object> prepayMap = WechatService.prepayId(parametersMap);
			if(prepayMap.get("return_code") != null && "SUCCESS".equals(prepayMap.get("return_code").toString())
			   && prepayMap.get("result_code") != null && "SUCCESS".equals(prepayMap.get("result_code").toString())){
				QRUtils.createRqCode(prepayMap.get("code_url").toString(), 400, 400, toStream);
			}
		} catch (BaseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriterException e) {
			e.printStackTrace();
		}finally {
			try {
				toStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
		
}
