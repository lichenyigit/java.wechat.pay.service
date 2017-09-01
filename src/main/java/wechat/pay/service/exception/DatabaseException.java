package wechat.pay.service.exception;

import java.sql.SQLException;

public class DatabaseException extends BaseException {
	private static final long serialVersionUID = -6647346276884887858L;
	public DatabaseException(SQLException e) {
		super(e);
	}
	
	@Override
	public String getErrorCode() {
		return "200";
	}

}
