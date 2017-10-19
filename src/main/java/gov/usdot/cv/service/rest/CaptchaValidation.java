package gov.usdot.cv.service.rest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;
import org.apache.log4j.Logger;

import gov.usdot.cv.resources.PrivateResourceLoader;

public class CaptchaValidation
{
  public static final String url = "https://www.google.com/recaptcha/api/siteverify";
  public static final String secret = PrivateResourceLoader.getProperty("@casadmin-webapp/captcha.validation.secret@");
  private static final String USER_AGENT = "Mozilla/5.0";
  private static Logger logger = Logger.getLogger(RequestAccount.class);
  
  public static boolean verify(String gRecaptchaResponse)
    throws IOException
  {
    if ((gRecaptchaResponse == null) || ("".equals(gRecaptchaResponse))) {
      return false;
    }
    try
    {
      URL obj = new URL("https://www.google.com/recaptcha/api/siteverify");
      HttpsURLConnection con = (HttpsURLConnection)obj.openConnection();
      
      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "Mozilla/5.0");
      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      
      String postParams = "secret=" + secret + "&response=" + gRecaptchaResponse;
      
      con.setDoOutput(true);
      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
      wr.writeBytes(postParams);
      wr.flush();
      wr.close();
      
      int responseCode = con.getResponseCode();
      logger.info("\nSending 'POST' request to URL : https://www.google.com/recaptcha/api/siteverify");
      logger.info("Post parameters : " + postParams);
      logger.info("Response Code : " + responseCode);
      
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      
      StringBuffer response = new StringBuffer();
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      
      logger.info(response.toString());
      
      JsonReader jsonReader = Json.createReader(new StringReader(response.toString()));
      JsonObject jsonObject = jsonReader.readObject();
      jsonReader.close();
      
      return jsonObject.getBoolean("success");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return false;
  }
}
