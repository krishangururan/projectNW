package web.pages;

import com.thoughtworks.gauge.Step;
import web.utils.CommonStepsUI;
import web.utils.CommonUtils;
import web.utils.ExtentTestManager;

public class LoginPageSteps {
    @Step("Login to the <UserApplication> application with <UserType> profile")
    public void userApplicationWithProfile(String userApplication, String userType){
        CommonUtils.setScenarioStoreVal("userApplication",userApplication);
        CommonUtils.setScenarioStoreVal("userType",userType);
        if(userApplication.equalsIgnoreCase("Bank User")){
            userTypeLogonBU(userType);
        }
        else if(userApplication.equalsIgnoreCase("Customer User")){
            ExtentTestManager.reportInfo("Starting login verification for user: "+System.getenv(userType));
            //userTypeLogonCU(userType,"CARDPIN",userApplication);
        }
    }
    @Step("Starting logon to bank user as <user>")
    public void userTypeLogonBU(String userRole){
        LoginPage.settingUrlPropAtSpecLevelBU();
        new CommonStepsUI().openL_link_For_BU_SSO(CommonUtils.getSpecStoreVal("URL"),userRole);
    }
}
