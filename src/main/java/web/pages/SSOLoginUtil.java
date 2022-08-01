package web.pages;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import web.utils.CommonUtils;
import web.utils.Driver;

import java.util.Calendar;
import java.util.TimeZone;

import static io.restassured.RestAssured.post;
import static io.restassured.RestAssured.with;

public class SSOLoginUtil {
    public SSOLoginUtil(String username,String password,String url){
        RequestSpecification rspec;
        Cookie rbsGSessionCookie;
        Cookies authCookies;
        Cookies adminCookies;
        Response response;
        int statusCode;

        RestAssured.useRelaxedHTTPSValidation();
        rspec = new RequestSpecBuilder()
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter(LogDetail.HEADERS))
                .build();
        String[] arr = username.split("\\\\");
        String bankDomain = arr[0];
        String uname=arr[arr.length-1];
        System.out.println("Domain and Username::::"+bankDomain+"---"+uname);
        String cwsLoginUrl=post(url).getHeader("Location");
        response = with().spec(rspec)
                .auth().ntlm(uname,password,"",bankDomain)
                .post(cwsLoginUrl);
        rbsGSessionCookie = response.getDetailedCookie("RBSGSESSION");
        authCookies = with().spec(rspec)
                .cookie(rbsGSessionCookie)
                .get(url)
                .getDetailedCookies();
        adminCookies = new Cookies(rbsGSessionCookie,authCookies.get("JSESSIONID"),authCookies.get("LtpaToken2"));
        response = with().spec(rspec)
                .cookies(adminCookies)
                .get(url);
        statusCode = response.getStatusCode();
        Cookie newRbsGSessionCookie = response.getDetailedCookie("RBSGSESSION");
        if(newRbsGSessionCookie !=null){
            rbsGSessionCookie = newRbsGSessionCookie;
        }
        Assert.assertEquals("Request did not go through",200,statusCode);
        Driver.getDriver().manage().deleteAllCookies();
        Driver.getDriver().navigate().to("https://www.google.com");
        CommonUtils.waitForPageLoaded(Driver.getDriver());
        Driver.getDriver().get(System.getenv("Temp_Support_Url"));
        Driver.getDriver().manage().deleteAllCookies();
        System.out.println("Cookie Added Start");
        Driver.getDriver().manage().addCookie(convertToSelenium(rbsGSessionCookie));
        Driver.getDriver().manage().addCookie(convertToSelenium(authCookies.get("JSESSIONID")));
        Driver.getDriver().manage().addCookie(convertToSelenium(authCookies.get("LtpaToken2")));
        Driver.getDriver().navigate().to(url);
        CommonUtils.waitForLoad(Driver.getDriver());
    }
    public org.openqa.selenium.Cookie convertToSelenium(Cookie cookie){
        org.openqa.selenium.Cookie.Builder newCookie=new org.openqa.selenium.Cookie.Builder(cookie.getName(), cookie.getValue());
        if(cookie.getComment()!=null){
            System.out.println("NOTE: The Cookie taken from Restassured has a comment attribute of '"+cookie.getComment());
        }
        if(cookie.getDomain()!=null){
            newCookie.domain(cookie.getDomain());
        }
        if(cookie.getExpiryDate()!=null){
            newCookie.expiresOn(cookie.getExpiryDate());
        }
        return newCookie.build();
    }
}
