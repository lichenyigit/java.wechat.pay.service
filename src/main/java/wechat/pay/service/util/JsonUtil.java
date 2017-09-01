package wechat.pay.service.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("all")
public class JsonUtil {
	Logger logger = LogManager.getLogger(getClass());
	
	public static String writeValue(Object object){
		try {
			return JSON.toJSONString(object);
		} catch (Exception e) {
			throw new JSONException(" writeJson failed. ", e);
		}
	}
	
	public static <T> T readJson(String str, T t){
		try {
			if(StringUtil.isNotBlank(str)){
				return (T) JSON.parseObject(str, t.getClass());
			}
		} catch (Exception e) {
			throw new JSONException(" readJson failed. ", e);
		}
		return null;
	}
	
}
