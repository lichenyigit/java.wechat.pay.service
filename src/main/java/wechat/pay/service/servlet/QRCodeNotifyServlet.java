package wechat.pay.service.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wechat.pay.service.exception.ReceiveWXMsgFailedException;
import wechat.pay.service.service.WechatService;
import wechat.pay.service.util.CommonUtils;
import wechat.pay.service.util.StringUtil;
import wechat.pay.service.util.WXPay.WXPayConstants;
import wechat.pay.service.util.WXPay.WXPayUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
@WebServlet(urlPatterns={"/qr_code_notify"}, description = "微信二维码异步回调通知,扫码支付模式一")
public class QRCodeNotifyServlet extends HttpServlet {
	private static final Logger logger = LogManager.getLogger(QRCodeNotifyServlet.class);
	
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		//返回给微信平台的参数
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> notifyMap = null;
		result.put("return_code", "SUCCESS");
		result.put("return_msg", "SUCCESS");
		result.put("result_code", "SUCCESS");
		Writer writer = null;
		//返回结果
		try {
			CommonUtils.getRequestInfo(request, logger);
			String notify = WXPayUtil.getWechatMessage(request);
			logger.info("微信二维码异步回调通知\n\r"+notify);
			writer = response.getWriter();
			request.setCharacterEncoding("UTF-8");

			String prepay_id = null;
			//获取微信返回的通知
			if(StringUtil.isNotBlank(notify)){
				//验证签名
				if(WXPayUtil.isSignatureValid(notify)) {
					logger.info("签名验证通过\n\r");
					notifyMap = WXPayUtil.xmlToMap(notify);
					//获取product_id
					String product_id = notifyMap.get("product_id").toString();
					String openid = notifyMap.get("openid").toString();
					//根据产品id获得统一下单（预支付id）prepay_id
					//TODO 根据产品id获取产品信息
					String total_fee = "1"; //TODO 从产品信息中获取
					String out_trade_no = WXPayUtil.generateOrderId();
					Map<String, Object> unifiedOrderMap = WechatService.prepayId(generateUnitizParameters(request, "", "扫码支付", "商品详情", out_trade_no, total_fee, product_id, openid));
					if(unifiedOrderMap.get("result_code") != null
							&& "SUCCESS".equals(unifiedOrderMap.get("result_code").toString())
							&& unifiedOrderMap.get("return_code") != null
							&& "SUCCESS".equals(unifiedOrderMap.get("return_code").toString())){
						prepay_id = unifiedOrderMap.get("prepay_id").toString();
						result.put("prepay_id", prepay_id);
					}else{
						result.put("return_code", "FAIL");
						result.put("return_msg", "获取prepay_id异常");
						result.put("err_code_des", "获取prepay_id异常");
					}
				}
			}else{
				result.put("return_code", "FAIL");
				result.put("return_msg", "获取prepay_id异常");
				result.put("err_code_des", "获取prepay_id异常");
			}


			//返回结果
			writer = response.getWriter();
			request.setCharacterEncoding("UTF-8");
			generateCallbackMap(result);
		} catch (ReceiveWXMsgFailedException e) {
			result.put("return_code", "FAIL");
			result.put("return_msg", "获取支付通知异常");
			result.put("result_code", "FAIL");
			result.put("err_code_des", "获取支付通知异常");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			result.put("return_code", "FAIL");
			result.put("return_msg", "转码异常");
			result.put("result_code", "FAIL");
			result.put("err_code_des", "转码异常");
			e.printStackTrace();
		} catch (IOException e) {
			result.put("return_code", "FAIL");
			result.put("return_msg", "IO异常");
			result.put("result_code", "FAIL");
			result.put("err_code_des", "IO异常");
			e.printStackTrace();
		} catch (Exception e) {
			result.put("return_code", "FAIL");
			result.put("err_code_des", "微信二维码异步回调通知异常");
			result.put("result_code", "FAIL");
			result.put("return_msg", "微信二维码异步回调通知异常");
			e.printStackTrace();
		}finally {
			//TODO
			if(notifyMap != null){
				//回掉通知(仅在成功的时候才会调用)
				WechatService.callback(notifyMap);
				//向数据库插入日志数据
				WechatService.insertLog(notifyMap);
			}

			//TODO 回调通知一定要使用xml
			try {
				String resultXMLStr = WXPayUtil.generateSignedXml(result);
				logger.info("微信二维码异步回调通知,向微信返回的结果\n\r"+ resultXMLStr);
				writer.write(resultXMLStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    private Map<String, Object> generateUnitizParameters(HttpServletRequest request, String attach, String body, String detail, String out_trade_no, String total_fee, String product_id, String openid){
        Map<String, Object> parametersMap = CommonUtils.createMap("appid", WXPayConstants.APP_ID);//appid
        CommonUtils.generateMap(parametersMap, "attach", attach);					//附加数据
        CommonUtils.generateMap(parametersMap, "mch_id", WXPayConstants.PAY_STORE_ID);//商户号
        CommonUtils.generateMap(parametersMap, "device_info", WXPayConstants.DEVICE_INFO_DEFAULT.WEB.toString());//设备号 自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
        CommonUtils.generateMap(parametersMap, "nonce_str", WXPayUtil.generateNonceStr());
        CommonUtils.generateMap(parametersMap, "sign_type", WXPayConstants.SignType.MD5);
        CommonUtils.generateMap(parametersMap, "body", body);//显示在手机页面上body中的title
        CommonUtils.generateMap(parametersMap, "detail", detail);
        //TODO 订单号的生成
        CommonUtils.generateMap(parametersMap, "out_trade_no", out_trade_no);//自己系统的订单号
        CommonUtils.generateMap(parametersMap, "fee_type", WXPayConstants.FEE_TYPE.CNY);
        CommonUtils.generateMap(parametersMap, "total_fee", total_fee);//单位位分
		//TODO 终端ip
        CommonUtils.generateMap(parametersMap, "spbill_create_ip", CommonUtils.getIpAddr(request));
        CommonUtils.generateMap(parametersMap, "notify_url", WXPayConstants.NOTIFY_URL);
        CommonUtils.generateMap(parametersMap, "trade_type", WXPayConstants.TRADE_TYPE.NATIVE); //trade_type=NATIVE时（即扫码支付），此参数必传
        CommonUtils.generateMap(parametersMap, "product_id", product_id);//商品id
        CommonUtils.generateMap(parametersMap, "openid", openid);//open id
        return parametersMap;
    }

    private void generateCallbackMap(Map<String, Object> map){
    	CommonUtils.generateMap(map, "appid", WXPayConstants.APP_ID);
    	CommonUtils.generateMap(map, "mch_id", WXPayConstants.PAY_STORE_ID);
    	CommonUtils.generateMap(map, "nonce_str", StringUtil.getRandomString(32));
	}

}
