package wechat.pay.service.listener;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.*;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

@WebListener
public class Listener implements ServletContextListener {
    private static Logger logger = LogManager.getLogger(Listener.class);

    private static Context rootCtx = null;
    private static BasicDataSource bds = null;
    private static final String JNDI_PROPERTIES = "jdbc/shihou/service/pay/properties";

    public void contextInitialized(ServletContextEvent sce){
        Context ctx;
        try {
            ctx = new InitialContext();
            rootCtx = (Context) ctx.lookup("java:/comp/env");
            logger.info("【微信支付服务 初始化成功!】");
        } catch (NamingException e) {
            logger.info("【微信支付服务 初始化失败!】");
            return;
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if(bds!=null){
            try {
                bds.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // This manually deregisters JDBC driver, which prevents Tomcat 7 from complaining about memory leaks wrto this class
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == cl) {
                try {
                    DriverManager.deregisterDriver(driver);
                    logger.info(String.format("deregistering jdbc driver: %s", driver));
                } catch (SQLException e) {
                    logger.info(String.format("Error deregistering driver %s", driver), e);
                }
            }
        }
        LogManager.shutdown();
    }

    private static BasicDataSource getDataSource() {
        if (bds == null) {
            Properties properties = new Properties();
            try {
                Context ctx = (Context) rootCtx.lookup(JNDI_PROPERTIES);
                NamingEnumeration<NameClassPair> pairs = rootCtx.list(JNDI_PROPERTIES);
                NameClassPair pair = null;
                while(true){
                    try{
                        pair = pairs.nextElement();
                    }catch(java.util.NoSuchElementException e){
                        break;
                    }
                    properties.setProperty(pair.getName(),(String) getJndi(ctx, pair.getName(), null));
                }
                try {
                    bds = BasicDataSourceFactory.createDataSource(properties);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (NamingException e1) {
                e1.printStackTrace();
            }
        }
        return bds;
    }

    public static Connection getDbConn(){
        return getDbConn(false);
    }

    public static Connection getDbConn(boolean autoCommit){
        Connection conn = null;
        try {
            conn = getDataSource().getConnection();
            conn.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            e.printStackTrace();
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return conn;
    }

    @SuppressWarnings("all")
    private static <T> T getJndi(String jndiName,T defVal){
        return getJndi(rootCtx, jndiName, defVal);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getJndi(Context ctx,String jndiName,T defVal){
        if(ctx != null){
            try {
                return (T) ctx.lookup(jndiName);
            } catch (NamingException e) {
                logger.warn(e.getMessage());
            }
        }
        return defVal;
    }

    public static String getJndiString(String jndi,String defaultVal){
        try{
            return (String) rootCtx.lookup(jndi);
        }catch(Exception e){
            return defaultVal;
        }
    }

    public static String getJndiString(String jndi){
        return getJndiString(jndi, null);
    }

}
