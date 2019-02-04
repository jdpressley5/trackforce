package com.revature.test.pom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

//IMPORTANT: A lot of code for this POM already exists in the general POM Package.
public class AssociateHome {
	
	private static String getTableValueSelector(int n) {
		return ".table > tbody > tr:nth-child("+n+") > td:nth-child(2)";
	}
	public static WebElement getLogout(WebDriver driver) {
		return driver.findElement(By.id("logout"));
	}
	
	public static WebElement getSaveButton(WebDriver driver) {
		return driver.findElement(By.id("submitButton"));
	}
	public static WebElement getResetButton(WebDriver driver) {
		return driver.findElement(By.id("resetButton"));
	}
	public static WebElement getAssociateTable(WebDriver driver) {
		return driver.findElement(By.className("table"));
	}
	//Warning: It exists but expect the value to be empty.  Needs to be implemented or eliminated.
	public static WebElement getCurrentAssociateId(WebDriver driver) {
		return driver.findElement(By.cssSelector(getTableValueSelector(1)));
	}
	public static WebElement getCurrentFirstName(WebDriver driver) {
		return driver.findElement(By.cssSelector(getTableValueSelector(2)));
	}
	public static WebElement getCurrentLastName(WebDriver driver) {
		return driver.findElement(By.cssSelector(getTableValueSelector(3)));
	}
	public static WebElement getCurrentMarketingStatus(WebDriver driver) {
		return driver.findElement(By.cssSelector(getTableValueSelector(4)));
	}
	public static WebElement getCurrentBatchName(WebDriver driver) {
		return driver.findElement(By.cssSelector(getTableValueSelector(5)));
	}
	public static WebElement getCurrentClientName(WebDriver driver) {
		return driver.findElement(By.cssSelector(getTableValueSelector(6)));
	}
	public static WebElement getCurrentEndClientName(WebDriver driver) {
		return driver.findElement(By.cssSelector(getTableValueSelector(7)));
	}

	public static WebElement getFirstNameInputField(WebDriver driver) {
		return driver.findElement(By.name("newFirstName"));
	}
	public static WebElement getLastNameInputField(WebDriver driver) {
		return driver.findElement(By.name("newLastName"));
	}
	public static WebElement getCurrentPassword(WebDriver driver) {
		return driver.findElement(By.id("currentPassword"));
	}
	public static WebElement getNewPassword(WebDriver driver) {
		return driver.findElement(By.id("newPassword"));
	}
	public static WebElement getConfirmPassword(WebDriver driver) {
		return driver.findElement(By.id("confirmPassword"));
	}
	
	public static WebElement newFirstName(WebDriver driver) {
		return driver.findElement(By.name("newFirstName"));
	}
	
	public static WebElement newLastName(WebDriver driver) {
		return driver.findElement(By.name("newLastName"));
	}
	
	public static WebElement submitName(WebDriver driver) {
		return driver.findElement(By.id("submitButton"));
	}
}
