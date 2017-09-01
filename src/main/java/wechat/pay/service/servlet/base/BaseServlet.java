package wechat.pay.service.servlet.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wechat.pay.service.exception.BaseException;
import wechat.pay.service.exception.RequestToMapException;
import wechat.pay.service.util.CommonUtils;
import wechat.pay.service.util.JsonUtil;
import wechat.pay.service.util.Result;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@SuppressWarnings("all")
public class BaseServlet<T> extends HttpServlet {

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
		Result<T> result = new Result<>();
		try {
			CommonUtils.getRequestInfo(request, logger);
			T t = processPost(request, response);
			logger.info(String.format(" POST result --> 【%s】", t));
			result.setResult(t);
		} catch (BaseException e) {
			e.printStackTrace();
			result = new Result<>(e);
		}
		response.getWriter().print(JsonUtil.writeValue(result));
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		Result<T> result = new Result<>();
		try {
			CommonUtils.getRequestInfo(request, logger);
			T t = processGet(request, response);
			logger.info(String.format(" GET result --> 【%s】", t));
			result.setResult(t);
		} catch (BaseException e) {
			e.printStackTrace();
			result = new Result<>(e);
		}
		logger.info(JsonUtil.writeValue(result));
		response.getWriter().print(JsonUtil.writeValue(result));
	}

    protected T processPost(HttpServletRequest request, HttpServletResponse response) throws BaseException {
		return null;
	}

    protected T processGet(HttpServletRequest request, HttpServletResponse response) throws BaseException {
		return null;
	}


}
