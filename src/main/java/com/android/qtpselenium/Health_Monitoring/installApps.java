package com.android.qtpselenium.Health_Monitoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.android.AndroidDriver;

public class installApps {
	//String device_id = "e8f3cc03";
	public static boolean install(String device_id) {
		AndroidDriver receiverDriver = null;
		try {
			System.out.println("before app initialisation");
			DesiredCapabilities receiverCapabilities = new DesiredCapabilities();
			receiverCapabilities.setCapability("platformName", "android");
			receiverCapabilities.setCapability("deviceName", "Samsung"); // Mandatory
			receiverCapabilities.setCapability("appPackage", "com.reliance.jio.jioswitch");
			receiverCapabilities.setCapability("appActivity", "com.reliance.jio.jioswitch.ui.SplashActivity");

			receiverCapabilities.setCapability("fullReset", true);
			receiverCapabilities.setCapability("noReset", true);
			receiverCapabilities.setCapability("--udid", device_id); // /added
			receiverDriver = new AndroidDriver(new URL("http://127.0.0.1:" + "4723" + "/wd/hub"), receiverCapabilities);
			receiverDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			System.out.println("App launched on reciever");
			receiverDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

			Thread.sleep(2000);
			receiverDriver.findElement(By.id("com.reliance.jio.jioswitch:id/installRecommendedAppsButton")).click();
			receiverDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			System.out.println("click Successful");

			receiverDriver.findElement(By.id("com.reliance.jio.jioswitch:id/genderFemale")).click();
			receiverDriver.findElement(By.id("com.reliance.jio.jioswitch:id/ageGroup2")).click();
			receiverDriver.findElement(By.id("com.reliance.jio.jioswitch:id/continueButton")).click();
			System.out.println("after continue");
			Thread.sleep(5000);

			receiverDriver.findElement(By.id("com.reliance.jio.jioswitch:id/installButton")).click();
			System.out.println("after install apps");
			
			Thread.sleep(200000);
			System.out.println("before pulling apps");

			Process pr = null;
			String str = "adb -s " + device_id
					+ " pull /sdcard/switchNwalk/transfer /Users/rakshita/Documents/switchNwalk";
			String[] commands = { "/bin/sh", "-c", "/Users/rakshita/Library/Android/sdk/platform-tools/" + str };
			Runtime run = Runtime.getRuntime();
			pr = run.exec(commands);
			BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			System.out.println("apks pulled " + buf.readLine());
			// Thread.sleep(5000);

			File appfile = new File("/Users/rakshita/Documents/switchNwalk");
			File[] files = appfile.listFiles();
			int count = 0;
			for (File file1 : files) {
				if (file1.getName().endsWith(".apk")) {
					str = "adb -s " + device_id + " install /Users/rakshita/Documents/switchNwalk/" + file1.getName();
					String comm[] = { "/bin/sh", "-c", "/Users/rakshita/Library/Android/sdk/platform-tools/" + str };
					pr = run.exec(comm);
					buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
					System.out.println(file1.getName() + " apk installed " + buf.readLine());
					count++;
					Thread.sleep(5000);
				}
			}
			System.out.println("done install apps");
			return true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		} finally {
			receiverDriver.close();
			receiverDriver.quit();
		}
	}
}
