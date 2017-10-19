package gov.usdot.cv.service.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.log4j.Logger;

public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = -3862016781721390835L;
	
	public static String webAppName = "casadmin";
	public static String publicHostname = "ec2-54-211-88-131.compute-1.amazonaws.com";
	
	// HTTP GET on the URL below is the AWS blessed way of determining public IPv4 address from within AWS instance
	private static final String getPublicIPv4 = "http://169.254.169.254/latest/meta-data/public-ipv4";

	private static Logger logger = Logger.getLogger(InitServlet.class);
	
	@Override
	public void init() throws ServletException {
		publicHostname = getPublicHostName();
		webAppName = getServletContext().getContextPath();
		logger.info(String.format("Account Authorization Server Started on %s as webApp %s", publicHostname, webAppName));
	}
	
	private static String getPublicHostName() {
		try {
			URL url = new URL(getPublicIPv4);
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(url.openStream()));
				return in.readLine();
			} finally {
				if ( in != null )
					in.close();
			}
		} catch (Exception ex ) {
			logger.warn("Coulnd't get public IPv4 address. Reason: " + ex.getMessage(), ex);
			return publicHostname;
		}
	}
	
}