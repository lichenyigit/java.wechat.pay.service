package wechat.pay.service.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wechat.pay.service.exception.ReceiveWXMsgFailedException;
import wechat.pay.service.service.WechatService;
import wechat.pay.service.util.CommonUtils;
import wechat.pay.service.util.StringUtil;
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
@WebServlet(urlPatterns={"/notify"}, description = "微信异步回调通知")
public class NotifyServlet extends HttpServlet {
	private static final Logger logger = LogManager.getLogger(NotifyServlet.class);
	
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response){
		Writer writer = null;
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> notifyMap = null;
		//返回结果
		try {
			CommonUtils.getRequestInfo(request, logger);
			String notify = WXPayUtil.getWechatMessage(request);
			logger.info("微信异步回调通知\n\r"+notify);
			//验证签名
			if(WXPayUtil.isSignatureValid(notify)){
				logger.info("签名验证通过\n\r");
				//获取微信返回的通知
				if(StringUtil.isNotBlank(notify)){
					notifyMap = WXPayUtil.xmlToMap(notify);
					if(notifyMap.get("result_code") != null
							&& "SUCCESS".equals(notifyMap.get("result_code").toString())
							&& notifyMap.get("return_code") != null
							&& "SUCCESS".equals(notifyMap.get("return_code").toString())){
						//回掉通知(仅在成功的时候才会调用)
						WechatService.callback(notifyMap);
						//向数据库插入日志数据
						WechatService.insertLog(notifyMap);

						result.put("return_code", "SUCCESS");
						result.put("return_msg", "OK");
					}else{
						result.put("return_code", "FAIL");
						result.put("return_msg", "回调信息返回值异常");
					}
				}else{
					result.put("return_code", "FAIL");
					result.put("return_msg", "回调信息返回值异常");
				}
			}

			writer = response.getWriter();
			request.setCharacterEncoding("UTF-8");

		} catch (ReceiveWXMsgFailedException e) {
			result.put("return_code", "FAIL");
			result.put("return_msg", "获取支付通知异常");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			result.put("return_code", "FAIL");
			result.put("return_msg", "转码异常");
			e.printStackTrace();
		} catch (IOException e) {
			result.put("return_code", "FAIL");
			result.put("return_msg", "IO异常");
			e.printStackTrace();
		} catch (Exception e) {
			result.put("return_code", "FAIL");
			result.put("return_msg", "微信异步回调通知异常");
			e.printStackTrace();
		}finally {
			if(notifyMap != null){
				//回掉通知(仅在成功的时候才会调用)
				WechatService.callback(notifyMap);
				//向数据库插入日志数据
				WechatService.insertLog(notifyMap);
			}
			//TODO 回调通知一定要使用xml
			try {
				writer.write(WXPayUtil.mapToXml(result));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
