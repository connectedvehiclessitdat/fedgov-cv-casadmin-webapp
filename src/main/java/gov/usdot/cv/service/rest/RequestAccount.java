package gov.usdot.cv.service.rest;

import gov.usdot.cv.resources.PrivateResourceLoader;
import gov.usdot.cv.service.servlet.InitServlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;

import java.net.URI;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;

@Path("/requestAccount")
public class RequestAccount {

	private static final String FROM = PrivateResourceLoader.getProperty("@casadmin-webapp/request.account.from@");
	
	private static final String TO = PrivateResourceLoader.getProperty("@casadmin-webapp/request.account.to@");
	private static final String TO2 = PrivateResourceLoader.getProperty("@casadmin-webapp/request.account.to2@");
	private static final String TO3 = PrivateResourceLoader.getProperty("@casadmin-webapp/request.account.to3@");
	private static final String TO4 = PrivateResourceLoader.getProperty("@casadmin-webapp/request.account.to4@");

	private static final String SUBJECT = "USDOT UI Account Request";

	private static final String SMTP_USERNAME =
							PrivateResourceLoader.getProperty("@casadmin-webapp/request.account.smtp.username@");
	private static final String SMTP_PASSWORD = 
							PrivateResourceLoader.getProperty("@casadmin-webapp/request.account.smtp.password@");

	private static final String HOST = "email-smtp.us-east-1.amazonaws.com";
	private static final int PORT = 25;

	private static Logger logger = Logger.getLogger(RequestAccount.class);

	@GET
	@Produces("application/text")
	public String requestAccount(
			@QueryParam("username") String username,
			@QueryParam("password") String password,
			@QueryParam("firstname") String firstname,
			@QueryParam("lastname") String lastname,
			@QueryParam("companyname") String companyname,
			@QueryParam("contract") String contract) {

		try {
			String encryptedPassword = CryptoHelper.encrypt(password);
			return notifyMail(username, encryptedPassword, firstname, lastname, companyname, contract);
		} catch (Exception ex) {
			final String status = "Couldn't submit authorization request for account " + username;
			logger.error(status + ". Reason: " + ex.getMessage(), ex);
			return status;
		}
	}

	static private String buildHyperLink(String email, String password,
			String firstname, String lastname, String companyname, String contract) {
		//String hostname = InitServlet.publicHostname;
		String hostname = "cas.connectedvcs.com";
		UriBuilder builder = UriBuilder
				.fromPath(hostname)
				.scheme("https")
				.path(InitServlet.webAppName + "/auP.html")
				.queryParam("email", email)
				.queryParam("password", password)
				.queryParam("firstname", firstname)
				.queryParam("lastname", lastname)
				.queryParam("companyname", companyname)
				.queryParam("contract", contract);
		URI uri = builder.build();
		return uri.toString();
	}

	private static String notifyMail(String user, String password, String firstname, String lastname, String companyname, String contract) throws AddressException, MessagingException {
		Properties props = System.getProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.port", PORT);

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");
		Session session = Session.getDefaultInstance(props);

		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(FROM));
		msg.addRecipients(Message.RecipientType.TO, new InternetAddress[] {
				new InternetAddress(TO),
				new InternetAddress(TO2),
				new InternetAddress(TO3),
				new InternetAddress(TO4)
		});
		msg.setSubject(SUBJECT);

		String hyperlink = buildHyperLink(user, password, firstname, lastname, companyname, contract);
		String emailBODY = String.format("Please follow the hyperlink below to approve the account request for:\n\n\tName:\t\t%s %s\n\tCompany:\t%s\n\tLogin/Email:\t%s\n\tUSDOT Contract\n\tor Agreement:\t%s\n\nClick this link to go to the approval page: %s\n",
						firstname, lastname, companyname, user, contract, hyperlink);
		msg.setContent(emailBODY, "text/plain");

		Transport transport = session.getTransport();
		logger.debug("Attempting to send an email through the Amazon SES SMTP interface...");
		transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
		transport.sendMessage(msg, msg.getAllRecipients());
		final String status = String.format("Request to authorize account %s has been successfully submitted", user);
		logger.info(status);
		return status;
	}

}
