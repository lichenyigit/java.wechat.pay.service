package wechat.pay.service.util;

import wechat.pay.service.exception.ConvertFailedException;
import wechat.pay.service.exception.ParameterConvertException;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Vector;


public class ConvertUtil {
	/**
	 * <h1>将字符串转换为整数</h1>
	 * <p>将字符串以十进制转换为整数，如果转换出错，则返回0</p>
	 * @param in 输入字符串
	 * @return 转换后的整数
	 */
	public static Integer getIntFromString(String in){
		return getIntFromString(in,0);
	}
	/**
	 * <h1>将字符串转换为整数</h1>
	 * <p>将字符串以十进制转换为整数，如果转换出错，则返回defVal</p>
	 * @param in 输入字符串
	 * @param defVal 转换出错时返回的值
	 * @return 转换结果
	 */
	public static Integer getIntFromString(String in,Integer defVal){
		try{
			return Integer.parseInt(in, 10);
		}catch(Exception e){
			return defVal;
		}
	}
	/**
	 * <h1>将字符串转换为整数</h1>
	 * <p>将字符串以十进制转换为整数，如果转换出错，则抛出异常</p>
	 * @param in 输入字符串
	 * @return 转换后的整数
	 * @throws ConvertFailedException 转换出错时抛出此异常
	 */
	public static int getIntFromStringWithException(String in) throws ConvertFailedException {
		try{
			return Integer.parseInt(in, 10);
		}catch(Exception e){
			throw new ConvertFailedException(e);
		}
	}
	/**
	 * <h1>将对象转换为字符串，并把首尾空格去掉</h1>
	 * <p>将对象转换为字符串，并把首尾空格去掉，若过程中出错，则抛出异常。</p>
	 * @param in 待转换对象
	 * @return 去掉首尾空格的字符串
	 * @throws ConvertFailedException 转换出错时抛出此异常
	 */
	public static String getTrimStrFromObjWithException(Object in) throws ConvertFailedException{
		try{
			return in.toString().trim();
		}catch(Exception e){
			throw new ConvertFailedException(e);
		}
	}
	
	/**
	 * <h1>将对象转换为字符串，并把首尾空格去掉</h1>
	 * <p>将对象转换为字符串，并把首尾空格去掉，若过程中出错或转换结果为空，则抛出异常。</p>
	 * @param in 待转换对象
	 * @return 去掉首尾空格的字符串
	 * @throws ConvertFailedException 转换出错时抛出此异常
	 */
	public static String getNonEmptyTrimStrFromObjWithException(Object in) throws ConvertFailedException{
		String ret = getTrimStrFromObjWithException(in);
		if(ret.length()==0){
			throw new ConvertFailedException();
		}
		return ret;
	}
	
	/**
	 * <h1>检测非空字符串</h1>
	 * @param in 字符串
	 * @return 字符串
	 * @throws ConvertFailedException 若字符串为null或空串，则抛出异常
	 */
	public static String getNonEmptyStr(String in) throws ConvertFailedException{
		if(in==null||in.length()==0){
			throw new ConvertFailedException();
		}
		return in;
	}
	
	/**
	 * <h1>通过字符串获取BigDecimal</h1>
	 * @param in 字符串
	 * @return BigDecimal
	 * @throws ConvertFailedException 若转换出错，则抛出此异常
	 */
	public static BigDecimal getBigDecimalFromStr(String in) throws ConvertFailedException{
		try {
			return new BigDecimal(in);
		} catch (Exception e) {
			throw new ConvertFailedException(e);
		}
	}
	
	/**
	 * <h1>从reuqest对象中获取非空字符串</h1>
	 * @param request
	 * @param paramName 参数名称
	 * @return 获取的字符串
	 * @throws ParameterConvertException 若在获取过程中出错，则抛出此异常
	 */
	public static String getNonEmptyStringFromRequestParam(HttpServletRequest request,String paramName) throws ParameterConvertException {
		String val = request.getParameter(paramName);
		if(val==null||val.length()==0){
			throw new ParameterConvertException(paramName, val);
		}
		return val;
	}
	
	/**
	 * <h1>从request对象中获取去首尾空格的非空字符串</h1>
	 * @param request
	 * @param paramName 参数名称
	 * @return 获取的字符串
	 * @throws ParameterConvertException 若在获取过程中出错，则抛出此异常
	 */
	public static String getNonEmptyTrimStringFromRequestParam(HttpServletRequest request,String paramName) throws ParameterConvertException {
		String val = request.getParameter(paramName);
		if(val==null){
			throw new ParameterConvertException(paramName, val);
		}
		val = val.trim();
		if(val.length()==0){
			throw new ParameterConvertException(paramName, val);
		}
		return val;
	}
	
	/**
	 * <h1>从request对象中获取去首尾空格的非空字符串</h1>
	 * <p>从request对象中获取指定的参数，并且去掉前后空格，若无对应参数，则返回默认值。</p>
	 * @param request
	 * @param paramName 参数名称
	 * @param defaultVal 默认值
	 * @return 获取的字符串
	 */
	public static String getTrimStringFromRequestParam(HttpServletRequest request,String paramName,String defaultVal) {
		String val = request.getParameter(paramName);
		if(val==null){
			return val;
		}
		return val.trim();
	}
	
	/**
	 * <h1>从request对象中获取整数</h1>
	 * <p>从request对象中获取指定参数，并以十进制的形式转换为整数。</p>
	 * <p>如果对应的参数不存在，则返回null，若转换失败，则抛出异常。</p>
	 * @param request
	 * @param paramName 参数名称
	 * @return 获取的整数
	 * @throws ParameterConvertException 若在获取过程中出错，则抛出此异常
	 */
	public static Integer getIntegerFromRequestParam(HttpServletRequest request,String paramName) throws ParameterConvertException {
		String val = request.getParameter(paramName);
		if(val==null){
			return null;
		}
		try{
			return Integer.parseInt(val,10);
		}catch(Exception e){
			throw new ParameterConvertException(e, paramName, val);
		}
	}
	
	/**
	 * <h1>从request对象中获取长整数</h1>
	 * <p>从request对象中获取指定参数，并以十进制的形式转换为整数。</p>
	 * <p>如果对应的参数不存在，则返回null，若转换失败，则抛出异常。</p>
	 * @param request
	 * @param paramName 参数名称
	 * @return 获取的整数
	 * @throws ParameterConvertException 若在获取过程中出错，则抛出此异常
	 */
	public static Long getLongFromRequestParam(HttpServletRequest request,String paramName) throws ParameterConvertException {
		String val = request.getParameter(paramName);
		if(val==null){
			return null;
		}
		try{
			return Long.parseLong(val,10);
		}catch(Exception e){
			throw new ParameterConvertException(e, paramName, val);
		}
	}
	
	/**
	 * <h1>从request对象中获取整数</h1>
	 * <p>从request对象中获取指定参数，并以十进制的形式转换为整数。</p>
	 * <p>如果对应的参数不存在，或转换失败，返回默认值</p>
	 * @param request
	 * @param paramName 参数名称
	 * @param defaultVal 默认值
	 * @return 获取的整数
	 */
	public static int getIntFromRequestParam(HttpServletRequest request,String paramName,int defaultVal){
		String val = request.getParameter(paramName);
		try{
			return Integer.parseInt(val,10);
		}catch(Exception e){
			return defaultVal;
		}
	}
	
	/**
	 * <h1>从request对象中获取整数</h1>
	 * <p>从request对象中获取指定参数，并以十进制的形式转换为整数。</p>
	 * <p>如果对应的参数不存在，或转换失败，则抛出异常</p>
	 * @param request
	 * @param paramName 参数名称
	 * @return 获取的整数
	 * @throws ParameterConvertException 若在获取过程中出错，则抛出此异常
	 */
	public static int getIntFromRequestParam(HttpServletRequest request,String paramName) throws ParameterConvertException{
		String val = request.getParameter(paramName);
		try{
			return Integer.parseInt(val,10);
		}catch(Exception e){
			throw new ParameterConvertException(e, paramName, val);
		}
	}
	
	/**
	 * <h1>从request对象中获取BigDecimal</h1>
	 * <p>从request对象中获取指定参数，并转换为BigDecimal。</p>
	 * <p>如果对应的参数不存在，则返回null，若转换失败，则抛出异常。</p>
	 * @param request
	 * @param paramName 参数名称
	 * @return 获取的BigDecimal
	 * @throws ParameterConvertException 若在获取过程中出错，则抛出此异常
	 */	
	public static BigDecimal getBigDecimalFromRequestParam(HttpServletRequest request,String paramName) throws ParameterConvertException {
		String val = request.getParameter(paramName);
		if(val==null){
			return null;
		}
		try{
			return new BigDecimal(val);
		}catch(Exception e){
			throw new ParameterConvertException(e, paramName, val);
		}
	}
	
	/**
	 * <h1>从request对象中获取一组值</h1>
	 * <p>从request对象中获取指定参数，并使用Convertor转换。</p>
	 * @param request
	 * @param paramName
	 * @param converter
	 * @return
	 */
	public static <T> List<T> getListFromRequestParam(HttpServletRequest request,String paramName,Converter<T,String> converter){
		String[] values = request.getParameterValues(paramName);
		if(values==null){
			return Collections.emptyList();
		}
		List<T> list = new Vector<>(values.length);
		for(String val:values){
			list.add(converter.convert(val));
		}
		return list;
	}
	
	/**
	 * <h1>从request对象中获取一组值</h1>
	 * <p>从request对象中获取指定参数，转换为String列表。</p>
	 * @param request
	 * @param paramName
	 * @return
	 */
	public static List<String> getListFromRequestParam(HttpServletRequest request,String paramName){
		return getListFromRequestParam(request, paramName, new Converter<String, String>() {
			@Override
			public String convert(String in) {
				return in;
			}
		});
	}
	
	public static interface Converter<T,P> {
		public T convert(P in);
	}
	
	
	public static String getFixLengthNumberStr(int len,long in){
		return String.format("%0"+len+"d", in);
	}
}
