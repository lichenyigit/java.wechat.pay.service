package wechat.pay.service.service.config;

import wechat.pay.service.util.WXPay.WXPayConstants;

import java.io.*;

/**
 * @author Lichenyi
 * @date 2017-8-28 0028
 */
public class WXPayConfigImpl extends WXPayConfig {
    private byte[] certData;
    private static WXPayConfigImpl INSTANCE;

    /* 微信原码
    private WXPayConfigImpl() throws Exception{
        String certPath = "D://CERT/common/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }
    */

    private WXPayConfigImpl(){

    }
    public byte[] getCertData() {
        return certData;
    }

    public void setCertData() throws IOException {
        String certPath = "D://CERT/common/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    public static WXPayConfigImpl getInstance() throws Exception {
        if (INSTANCE == null) {
            synchronized (WXPayConfigImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WXPayConfigImpl();
                }
            }
        }
        return INSTANCE;
    }

    public String getAppID() {
        return WXPayConstants.APP_ID;
    }

    public String getMchID() {
        return WXPayConstants.PAY_STORE_ID;
    }

    public String getKey() {
        return WXPayConstants.PAY_STORE_KEY;
    }

    public InputStream getCertStream() {
        ByteArrayInputStream certBis;
        certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }


    public int getHttpConnectTimeoutMs() {
        return 2000;
    }

    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    public WXPayDomain getWXPayDomain() {
        return WXPayDomainImpl.instance();
    }

    public String getPrimaryDomain() {
        return "api.mch.weixin.qq.com";
    }

    public String getAlternateDomain() {
        return "api2.mch.weixin.qq.com";
    }

    @Override
    public int getReportWorkerNum() {
        return 1;
    }

    @Override
    public int getReportBatchSize() {
        return 2;
    }

}
