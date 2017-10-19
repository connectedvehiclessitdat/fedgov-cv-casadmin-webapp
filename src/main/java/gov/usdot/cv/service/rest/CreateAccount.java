package gov.usdot.cv.service.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Path("/createAccount")
public class CreateAccount {

	private static Logger logger = Logger.getLogger(CreateAccount.class);

	@GET
	@Produces("application/text")
	public String createAccount(
			@QueryParam("username") String username,
			@QueryParam("password") String password,
			@QueryParam("adminname") String adminname,
			@QueryParam("adminpwd") String adminpwd	) {
		try {
			String decryptedPassword = CryptoHelper.decrypt(password);
			return create(username, decryptedPassword, adminname, adminpwd);
		} catch (Exception ex) {
			logger.error(String.format("Couldn't add user %s to the database. Reason: %s", username, ex.getMessage()), ex);
			return "Couldn't authorize account " + username + "!";
		}
	}

	private String create(String username, String password, String adminname, String adminpwd) throws ClassNotFoundException, SQLException {
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		try {
			Class.forName("com.mysql.jdbc.Driver"); // this will load the MySQL driver, each DB has its own driver
			connect = DriverManager.getConnection("jdbc:mysql://localhost/cas?user=casad&password=casadPassword#");
			final String stmt = "insert into users (username, password) select ?, MD5(?) from users where username = ? and password = MD5(?) and exists (select * from admins where username = ?)";
			preparedStatement = connect.prepareStatement(stmt);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, adminname);
			preparedStatement.setString(4, adminpwd);
			preparedStatement.setString(5, adminname);
			int rowCount = preparedStatement.executeUpdate();
			if ( rowCount == 1 ) {
				logger.info("Successfully added user: " + username);
				return "Account " + username + " is now authorized!";
			} else {
				logger.info("Couldn't add user: " + username + ". Reason: Approver is not a valid user or not an administrator.");
				throw new SQLException("401 Unauthorized ");
			}
		} finally {
			try {
				if ( connect != null && !connect.isClosed())
				connect.close();
			} catch (Exception ex) {
				logger.warn("CreateAccount: Cleanup failed", ex);
			}
		}
	}
}
