package wechat.pay.service.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wechat.pay.service.listener.Listener;

import java.sql.Connection;

public class DBUtil {
	private static final Logger logger = LogManager.getLogger();
	public static Connection getConnection(){
		Connection conn = Listener.getDbConn();
		return conn;
	}

}
