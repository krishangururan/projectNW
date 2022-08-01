package web.utils;

import com.thoughtworks.gauge.Step;
import com.thoughtworks.gauge.datastore.DataStoreFactory;
import web.pages.LoginPage;

public class CommonStepsUI extends Driver {
    @Step("Open link <url>")
    public void open_link(String url){
        LoginPage loginPage = new LoginPage();
        url="CU";
        loginPage.openUrl(System.getenv(url));
    }
    @Step("Setting env in property for execution")
    public void prestep_before_login(){
        String url= CommonUtils.getSpecStoreVal("Environment");
        DataStoreFactory.getSpecDataStore().put("URL",url);
    }
    @Step("Open link <url> for BU SSO with <userType>")
    public void openL_link_For_BU_SSO(String url,String userType){
        LoginPage loginPage = new LoginPage();
        String username;
        String password;
        getDriver().manage().deleteAllCookies();
        url=CommonUtils.getSpecStoreVal("URL");
        if(userType.equalsIgnoreCase("FirstUser") && url.contains("BU")){
            username = System.getenv("FIRST_USERNAME_BU_SSO");
            password = System.getenv("FIRST_PASSWORD_BU_SSO");
        }
        else {
            username = System.getenv(userType);
            password=System.getenv(userType+"_PASSWORD");
        }
        DataStoreFactory.getScenarioDataStore().put("username",username);
        DataStoreFactory.getScenarioDataStore().put("password",password);
        ExtentTestManager.reportInfo("Setting login verification for user: "+username);
        try{
            loginPage.openUrl_For_BU_SSO(System.getenv(url),username,password);
        }
        catch (Exception e){
            System.out.println("GOT EXCEPTION");
        }
    }
}
