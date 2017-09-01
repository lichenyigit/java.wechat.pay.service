package wechat.pay.service.servlet.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wechat.pay.service.exception.*;
import wechat.pay.service.util.CommonUtils;
import wechat.pay.service.util.JsonUtil;
import wechat.pay.service.util.Result;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@SuppressWarnings("all")
public class BaseReturnJsonServlet<T> extends HttpServlet {

	private static final long serialVersionUID = -2766701061704928482L;

	private Logger logger;
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger = LogManager.getLogger(this.getClass());
		super.service(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		try {
			CommonUtils.getRequestInfo(request, logger);
			T responseStr = processPost(request, response);
			logger.info(String.format(" POST result --> 【%s】", responseStr));
			returnJson(responseStr, response);
		} catch (BaseException e) {
			e.printStackTrace();
			Result<String> result = new Result<>(e);
			response.getWriter().print(JsonUtil.writeValue(result));
		}
	}

    /**
     * 
     * @author lichenyi
     * @param val
     * @param response
     * @param sign=true 对返回结果进行封装，sign=false返回结果不再对结果进行封装
     * 2017年1月23日 下午12:33:36
     */
    public void returnJson(T val, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
		Result<T> obj = new Result<>();
		try {
			obj.setResult(val);
        } catch (JsonConvertFailedException e) {
            logger.error("JSON转换失败(toJson error)", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        try {
            response.getWriter().write(JsonUtil.writeValue(obj));
        } catch (Exception e) {
            logger.error("Get response writer failed!", e);
        }
    }

    protected T processPost(HttpServletRequest request, HttpServletResponse response) throws BaseException {
		return null;
	}
    
}
