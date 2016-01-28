package com.android.qtpselenium.Health_Monitoring;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.xml.LaunchSuite;

import com.github.genium_framework.appium.support.server.AppiumServer;
import com.github.genium_framework.server.ServerArguments;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import junit.framework.Assert;

public class downloadApp {
	/*public static boolean launchAppium(int portNo) {
		ServerArguments serverArguments = new ServerArguments();
		serverArguments.setArgument("--address", "127.0.0.1");
		// serverArguments.setArgument("--chromedriver-port", 9516);
		// serverArguments.setArgument("--bootstrap-port", 4725);
		serverArguments.setArgument("--port", portNo);
		serverArguments.setArgument("--no-reset", true);
		serverArguments.setArgument("--local-timezone", true);
		// File apkDirectory = new File("D:/Appium/)");
		// AppiumServer appiumServer = new AppiumServer(serverArguments);
		AppiumServer appiumServer = new AppiumServer(serverArguments);
		System.out.println("launching appium server");
		appiumServer.startServer();
		System.out.println("appium started");
		System.out.println("is appium server running - " + appiumServer.isServerRunning());
		return appiumServer.isServerRunning();
	}*/

	public static boolean download(String device_id) {
		//boolean appium_launched = launchAppium(4723);
		//if (appium_launched) {
			AppiumDriver mobWebDriver = null;
			DesiredCapabilities capabilities = DesiredCapabilities.android();
			capabilities.setCapability("deviceName", "Samsung");
			capabilities.setCapability("platformName", "android");
			capabilities.setCapability("app", "Chrome");
			capabilities.setCapability("udid", device_id);
			capabilities.setCapability("autoAcceptAlerts", true);
			try {
				System.out.println("before initialization");
				mobWebDriver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
				System.out.println("initialised driver");
				mobWebDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				mobWebDriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);

				mobWebDriver.context("WEBVIEW_1");
				mobWebDriver.get("http://google.com");
				mobWebDriver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
				mobWebDriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
				Thread.sleep(5000);

				mobWebDriver.findElement(By.xpath("//button[text()='Download switchNwalk app']")).click();
				System.out.println("clicked download");
				mobWebDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

				mobWebDriver.context("NATIVE_APP");
				mobWebDriver.findElement(By.name("OK")).click();
				System.out.println("clicked OK");
				
				mobWebDriver.findElement(By.name("Replace file")).click();
				System.out.println("downloading file");

				mobWebDriver.context("WEBVIEW_1");
				mobWebDriver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
				return true;
			} catch (Exception e) {
				System.out.println("in error-------------" + e);
				e.printStackTrace();
				return false;
			} finally {
				mobWebDriver.close();
				mobWebDriver.quit();
			}
		//}
		//else
		//	return false;
	}
}
