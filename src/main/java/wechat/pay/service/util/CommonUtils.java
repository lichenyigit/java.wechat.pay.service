package wechat.pay.service.util;

import org.apache.logging.log4j.Logger;
import wechat.pay.service.exception.RequestToMapException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Map.Entry;


public class CommonUtils {

	// 创建Map
	public static Map<String, Object> createMap(String key, Object value) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (value == null) {
			value = "";
		}
		map.put(key, CommonUtils.setString(value.toString()));
		return map;
	}

	public static Map<String, Object> generateMap(Map<String, Object> map,
			String key, Object value) {
		if (value == null) {
			value = "";
		}
		map.put(key, value);
		return map;
	}

	public static Map<String, Object> mapSort(Map<String, Object> map){
		//这里将map.entrySet()转换成list
		List<Entry<String, Object>> list = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Object>>() {
			@Override
			public int compare(Entry<String, Object> o1, Entry<String, Object> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
		Map<String, Object> result = new LinkedHashMap<>();
		for(Entry<String, Object> entry : list){
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static String setString(String str) {
		return setString(str, "");
	}

	public static String setString(String str, String defaultStr) {
		if (isNotBlank(str)) {
			return str;
		} else {
			return defaultStr;
		}
	}

	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	
	public static boolean isNull(String str){
		if(str == null)
			return true;
		return false;
	}
	
	public static boolean isNotNull(String str){
		return !isNull(str);
	}

	public static Map<String, Object> resquestParameter2Map(HttpServletRequest request) throws RequestToMapException {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String[]> url = request.getParameterMap();
		for (Entry<String, String[]> entry : url.entrySet()) {
			String key = entry.getKey();
			String[] valueArray = entry.getValue();
			String value;
			value = new String(valueArray[0]);
			map.put(key, value);
		}
		return map;
	}

	public static Long getLongFromString(String str){
		if(CommonUtils.isBlank(str)){
			return null;
		}
		try {
			return Long.valueOf(Long.parseLong(str));
		} catch (Exception e) {
		}
		return null;
	}
	
	public static void responseCORS(HttpServletResponse response){
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		response.setCharacterEncoding("UTF-8");
		//response.setContentType("application/json");
	}
	
	public static void getRequestInfo(HttpServletRequest request, Logger logger) throws RequestToMapException {
		logger.info(
				"\n------------------------------------------------------------------------------------------" +
						"\n\t   client IP    : " + getIpAddr(request) +
						"\n\t   content type : " + request.getMethod() +
						"\n\t   request url  : " + request.getRequestURL() +
						"\n\t   parameters   : " + String.format("【%s】", CommonUtils.resquestParameter2Map(request)) +
						"\n------------------------------------------------------------------------------------------");
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknow".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
				//根据网卡获取本机配置的IP地址
				InetAddress inetAddress = null;
				try {
					inetAddress = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ipAddress = inetAddress.getHostAddress();
			}
		}

		//对于通过多个代理的情况，第一个IP为客户端真实的IP地址，多个IP按照','分割
		if (null != ipAddress && ipAddress.length() > 15) {
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}

		return ipAddress;
	}


}
