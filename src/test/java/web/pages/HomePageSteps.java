package web.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import web.utils.Driver;

public class HomePageSteps {
    public HomePageSteps() {
        PageFactory.initElements(Driver.getDriver(), this);
    }

    @FindBy(xpath = "//a[@href='https://docs.gauge.org/getting_started/installing-gauge.html']")
    public WebElement  getGauge;

    @FindBy(xpath = "//a[@href='/writing-specifications.html']")
    public WebElement installationInstructions;





}
