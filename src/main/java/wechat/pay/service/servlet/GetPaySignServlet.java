package wechat.pay.service.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wechat.pay.service.exception.BaseException;
import wechat.pay.service.service.WechatService;
import wechat.pay.service.servlet.base.BaseReturnJsonServlet;
import wechat.pay.service.util.CommonUtils;
import wechat.pay.service.util.StringUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("all")
@WebServlet(urlPatterns={"/getPaySign"}, description = "获取支付签名")
public class GetPaySignServlet extends BaseReturnJsonServlet {
	private static final Logger logger = LogManager.getLogger(GetPaySignServlet.class);
	
	private static final long serialVersionUID = 1L;

	protected Object processPost(HttpServletRequest request, HttpServletResponse response) throws BaseException {
		String result = WechatService.getPaySign(CommonUtils.resquestParameter2Map(request));
		if(StringUtil.isNotBlank(result)){
			return result;
		}
		return "生成失败";
	}
		
}
