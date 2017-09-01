package wechat.pay.service.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wechat.pay.service.exception.BaseException;
import wechat.pay.service.service.WechatService;
import wechat.pay.service.servlet.base.BaseReturnJsonServlet;
import wechat.pay.service.util.*;
import wechat.pay.service.util.WXPay.WXPayConstants;
import wechat.pay.service.util.WXPay.WXPayUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@SuppressWarnings("all")
@WebServlet(urlPatterns={"/getPrePayInfo"}, description = "获取统一支付信息")
public class GetPrePayInfoServlet extends BaseReturnJsonServlet<Map<String, Object> > {
	private static final Logger logger = LogManager.getLogger(GetPrePayInfoServlet.class);
	
	private static final long serialVersionUID = 1L;

	protected Map<String, Object>  processPost(HttpServletRequest request, HttpServletResponse response) throws BaseException {

		String attach = ConvertUtil.getTrimStringFromRequestParam(request, "attach", "");
		String device_info = ConvertUtil.getTrimStringFromRequestParam(request, "device_info", WXPayConstants.DEVICE_INFO_DEFAULT.WEB.toString());
		String body = ConvertUtil.getNonEmptyStringFromRequestParam(request, "body");
		String detail = ConvertUtil.getTrimStringFromRequestParam(request, "detail", "");
		String out_trade_no = ConvertUtil.getNonEmptyStringFromRequestParam(request, "out_trade_no");
		String total_fee = ConvertUtil.getNonEmptyStringFromRequestParam(request, "total_fee");
		String trade_type = ConvertUtil.getTrimStringFromRequestParam(request, "trade_type", WXPayConstants.TRADE_TYPE.JSAPI.toString());
		String product_id = ConvertUtil.getNonEmptyStringFromRequestParam(request, "product_id");
		String openid = ConvertUtil.getNonEmptyStringFromRequestParam(request, "openid");

		Map<String, Object> parametersMap = CommonUtils.createMap("appid", WXPayConstants.APP_ID);//appid
		CommonUtils.generateMap(parametersMap, "attach", attach);//附加数据
		CommonUtils.generateMap(parametersMap, "mch_id", WXPayConstants.PAY_STORE_ID);//商户号
		CommonUtils.generateMap(parametersMap, "device_info", device_info);//设备号 自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
		CommonUtils.generateMap(parametersMap, "nonce_str", WXPayUtil.generateNonceStr());
		CommonUtils.generateMap(parametersMap, "sign_type", WXPayConstants.SignType.MD5);
		CommonUtils.generateMap(parametersMap, "body", body);//商品描述
		CommonUtils.generateMap(parametersMap, "detail", detail);//商品详情
		CommonUtils.generateMap(parametersMap, "out_trade_no", out_trade_no);//自己系统的订单号
		CommonUtils.generateMap(parametersMap, "fee_type", WXPayConstants.FEE_TYPE.CNY);
		CommonUtils.generateMap(parametersMap, "total_fee", total_fee);//单位位分
		CommonUtils.generateMap(parametersMap, "spbill_create_ip", CommonUtils.getIpAddr(request));
		CommonUtils.generateMap(parametersMap, "notify_url", WXPayConstants.NOTIFY_URL);
		CommonUtils.generateMap(parametersMap, "trade_type", trade_type);
		CommonUtils.generateMap(parametersMap, "product_id", product_id);//商品id
		CommonUtils.generateMap(parametersMap, "openid", openid);//open id

		return WechatService.prepayId(parametersMap);
	}
		
}
