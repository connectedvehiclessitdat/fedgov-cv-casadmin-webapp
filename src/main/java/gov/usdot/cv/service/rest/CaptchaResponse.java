package gov.usdot.cv.service.rest;

import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/validateCaptcha")
public class CaptchaResponse
{
  @GET
  @Produces({"application/text"})
  public String getCaptchaResponse(@QueryParam("gRecaptchaResponse") String gRecaptchaResponse)
  {
    boolean verify;
    try
    {
      verify = CaptchaValidation.verify(gRecaptchaResponse);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      verify = false;
    }
    return String.valueOf(verify);
  }
}
