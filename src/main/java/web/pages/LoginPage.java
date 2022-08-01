package web.pages;

import com.thoughtworks.gauge.datastore.DataStoreFactory;
import org.openqa.selenium.support.PageFactory;
import web.utils.Driver;

public class LoginPage extends Driver {
    public LoginPage(){
        System.out.println("--------------Constructor "+this.getClass()+"---------------");
        PageFactory.initElements(Driver.getDriver(),this);
    }
    public LoginPage openUrl(String url){
        Driver.getDriver().get(url);
        Driver.getDriver().manage().window().maximize();
        return (new LoginPage());
    }
    public void openUrl_For_BU_SSO(String url,String username,String password){
        new SSOLoginUtil(username,password,url);

    }
    public static void settingUrlPropAtSpecLevelBU(){
        try{
            DataStoreFactory.getSpecDataStore().put("URL","BU");
        }
        catch (Exception e){
            DataStoreFactory.getSpecDataStore().put("URL",System.getenv("LOGIN_JOURNEY_BU"));
        }
    }
}
