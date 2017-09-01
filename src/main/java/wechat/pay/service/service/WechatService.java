package wechat.pay.service.service;


import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wechat.pay.service.exception.DatabaseException;
import wechat.pay.service.exception.HttpRequestFailedException;
import wechat.pay.service.exception.MD5Exception;
import wechat.pay.service.util.CommonUtils;
import wechat.pay.service.util.DBUtil;
import wechat.pay.service.util.HttpUtil;
import wechat.pay.service.util.WXPay.WXPayConstants;
import wechat.pay.service.util.WXPay.WXPayRequest;
import wechat.pay.service.util.WXPay.WXPayUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author lichenyi
 * @date 2017-8-23-0023.
 */
public class WechatService {
    private static Logger logger = LogManager.getLogger(WechatService.class);

    /**
     * map 获得签名
     *@return
     *@param
     *@auther Lichenyi
     *@date 2017-8-24-0024 21:44
     */
    public static String getPaySign(Map<String, Object> parameters) throws MD5Exception {

        try {
            return WXPayUtil.generateSignedStr(parameters);
        } catch (Exception e) {
            logger.error(" getPaySign error 【parameters】 -- 【%s】", JSON.toJSONString(parameters));
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 统一支付（获取预支付id）
     * @param
     * @return
     * @author lichenyi
     * @date 2017-8-25 0025 10:44
     */
    public static Map<String, Object>  prepayId(Map<String, Object> parametersMap) throws HttpRequestFailedException, MD5Exception {

        String resultString = null;
        try {
            String parameterXML = WXPayUtil.generateSignedXml(parametersMap);
            //wechat origin code
            resultString = WXPayRequest.initialize().requestWithoutCert(WXPayConstants.UNIFIEDORDER_URL_SUFFIX, WXPayUtil.generateNonceStr(), parameterXML, false);
            logger.info("【预支付订单信息】 --> "+WXPayUtil.xmlToMap(resultString));
            return WXPayUtil.xmlToMap(resultString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param
     * @return
     * @author lichenyi
     * @date 2017-8-31 0031 13:57
     */
    public static void callback(Map<String, Object> data) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> attach = JSON.parseObject(data.get("attach").toString(), Map.class);//回掉url
                String url = attach.get("url").toString();
                Map<String, Object> resultData = CommonUtils.createMap("transaction_id", data.get("transaction_id").toString());
                CommonUtils.generateMap(resultData, "out_trade_no", data.get("out_trade_no").toString());
                CommonUtils.generateMap(resultData, "total_fee", data.get("total_fee").toString());
                CommonUtils.generateMap(resultData, "return_code", data.get("return_code").toString());
                if(data.get("return_code") != null && "FAIL".equals(data.get("return_code").toString())){
                    CommonUtils.generateMap(resultData, "return_msg", data.get("return_msg").toString());
                }
                try {
                    WXPayUtil.generateSignature(resultData, WXPayConstants.PAY_STORE_KEY);
                } catch (Exception e) {
                    logger.error("callback 生成签名 异常");
                    e.printStackTrace();
                }
                String result = null;
                try {
                    result = HttpUtil.postString(url, mapToListParameter(resultData));
                } catch (HttpRequestFailedException e) {
                    e.printStackTrace();
                }
                logger.trace("callback url --> 【{}】, parameter --> 【{}】, result --> 【{}】", url, JSON.toJSONString(resultData), result);
            }
        }, "callback 线程");
        thread.start();
    }

    public static void insertLog(Map<String, Object> data){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> resultData = CommonUtils.createMap("third_order_id", data.get("transaction_id").toString());
                CommonUtils.generateMap(resultData, "third_platform", "");
                CommonUtils.generateMap(resultData, "description", "");
                CommonUtils.generateMap(resultData, "order_id", data.get("out_trade_no").toString());
                CommonUtils.generateMap(resultData, "total_fee", data.get("total_fee").toString());
                CommonUtils.generateMap(resultData, "openid", data.get("openid").toString());
                CommonUtils.generateMap(resultData, "third_pre_pay_data", JSON.toJSONString(data));
                if(data.get("attach") != null){
                    logger.trace("attach is null.");
                    Map<String, Object> attachMap = JSON.parseObject(data.get("attach").toString(), Map.class);
                    CommonUtils.generateMap(resultData, "third_platform", attachMap.get("platform"));
                    CommonUtils.generateMap(resultData, "description", attachMap.get("descrition"));
                }
                try {
                    boolean result = addServerLog(resultData);
                    logger.trace("支付日志写入结果 --> {}.", result);
                } catch (DatabaseException e) {
                    logger.trace("支付日志写入失败.");
                    e.printStackTrace();
                }
            }
        }, "insertLog 线程");
        thread.start();
    }

    private static List<HttpUtil.Parameter> mapToListParameter(Map<String, Object> data){
        List<HttpUtil.Parameter> result = new ArrayList<>();
        for(Map.Entry<String, Object> entry : data.entrySet()){
            result.add(new HttpUtil.Parameter(entry.getKey(), entry.getValue().toString()));
        }
        return result;
    }

    //*************************************************************************************************************************************************************
    //*******************************************************   数据库操作
    //*************************************************************************************************************************************************************

    /**
     * 添加日志
     * @param
     * @return
     * @author lichenyi
     * @date 2017-8-31 0031 14:40
     */
    private static boolean addServerLog(Map<String, Object> data) throws DatabaseException {
        try(Connection conn = DBUtil.getConnection()){
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO pay_server_log (id, order_id, third_order_id, third_platform, total_fee, description, openid, third_pre_pay_data) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            int idx = 1;
            pstmt.setString(idx++, WXPayUtil.generateOrderId());
            pstmt.setString(idx++, data.get("order_id").toString());
            pstmt.setString(idx++, data.get("third_order_id").toString());
            pstmt.setString(idx++, data.get("third_platform").toString());
            pstmt.setString(idx++, data.get("total_fee").toString());
            pstmt.setString(idx++, data.get("description").toString());
            pstmt.setString(idx++, data.get("openid").toString());
            pstmt.setString(idx++, data.get("third_pre_pay_data").toString());
            int result = pstmt.executeUpdate();
            conn.commit();
            return result > 0;
        }catch(SQLException e){
            throw new DatabaseException(e);
        }

    }


    /**
     * 异步添加请求日志
     * @param
     * @return
     * @author lichenyi
     * @date 2017-9-1 0001 15:43
     */
    public static boolean sysncAddReqeustLog(Map<String, Object> data){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean result = addReqeustLog(data);
                    logger.trace("添加请求日志结果 -- {}", result);
                } catch (DatabaseException e) {
                    logger.error("添加请求日志异常");
                    e.printStackTrace();
                }
            }
        }, "添加请求日志线程");
        thread.start();
        return false;
    }

    /**
     * 添加请求日志
     * @param
     * @return
     * @author lichenyi
     * @date 2017-9-1 0001 15:43
     */
    public static boolean addReqeustLog(Map<String, Object> data) throws DatabaseException {
        try(Connection conn = DBUtil.getConnection()){
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO request_log (order_id, client_ip, request_type, request_url, request_parameters) VALUES (?, ?, ?, ?, ?)");
            int idx = 1;
            pstmt.setString(idx++, data.get("order_id").toString());
            pstmt.setString(idx++, data.get("client_ip").toString());
            pstmt.setString(idx++, data.get("request_type").toString());
            pstmt.setString(idx++, data.get("request_url").toString());
            pstmt.setString(idx++, data.get("request_parameters").toString());
            int result = pstmt.executeUpdate();
            conn.commit();
            return result > 0;
        }catch(SQLException e){
            throw new DatabaseException(e);
        }
    }

}
