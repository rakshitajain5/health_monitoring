package com.android.qtpselenium.Health_Monitoring;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import junit.framework.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.exec.OS;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import com.android.qtpselenium.Health_Monitoring.downloadApp;

@Test
public class AdbTests implements IRetryAnalyzer {

	private int retryCount = 0;
	private int maxRetryCount = 2;

	public boolean retry(ITestResult result) {

		if (retryCount < maxRetryCount) {
			retryCount++;
			return true;
		}
		return false;
	}

	public String callGet(String url, String uri, String header) throws ClientProtocolException, IOException {

		String responseString = null;
		HttpGet http = new HttpGet(url + uri);
		ArrayList<String> List;
		List = new ArrayList<String>(Arrays.asList(header.split(",")));
		// System.out.println(uri);
		for (String headerList : List) {
			String[] header1 = headerList.split(":");
			// System.out.println(header1[0] + ":" + header1[1]);
			http.addHeader(header1[0], header1[1]);

			// Execute the request using httpclient and save the response

			HttpClient httpClient = HttpClients.createDefault();
			HttpResponse response = null;
			response = httpClient.execute(http);

			// Display the response

			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");

		}

		return responseString;

	}

	@Parameters({ "sender_device_id", "receiver_device_id" })
	@Test(retryAnalyzer = AdbTests.class)
	public void uninstall(String sender_device_id, String receiver_device_id) {
		try {
			String line = "";
			String result = "";
			String str = "";
			if (OS.isFamilyMac()) {
				str = "grep";
			} else if (OS.isFamilyWindows()) {
				str = "findstr";
			}
			String sender_str = "adb -s " + sender_device_id + " shell pm list packages | " + str
					+ " com.reliance.jio.jioswitch";
			String receiver_str = "adb -s " + receiver_device_id + " shell pm list packages | " + str
					+ " com.reliance.jio.jioswitch";
			BufferedReader sender_buf = AdbExecute.executeAdbCommand(sender_str);
			while ((line = sender_buf.readLine()) != null) {
				// System.out.println("adb out-->" + line);
				result = line;
			}
			if (result.isEmpty()) {
				System.out.println("sender phone app not present");
			} else {
				sender_str = "adb -s " + sender_device_id + " uninstall com.reliance.jio.jioswitch";
				sender_buf = AdbExecute.executeAdbCommand(sender_str);
				while ((line = sender_buf.readLine()) != null) {
					// System.out.println("adb out-->" + line);
					result = line;
				}
				Assert.assertEquals("Success", result);
			}
			result = "";
			BufferedReader receiver_buf = AdbExecute.executeAdbCommand(receiver_str);
			while ((line = receiver_buf.readLine()) != null) {
				// System.out.println("adb out-->" + line);
				result = line;
			}
			if (result.isEmpty()) {
				System.out.println("receiver phone app not present");
			} else {
				receiver_str = "adb -s " + receiver_device_id + " uninstall com.reliance.jio.jioswitch";
				receiver_buf = AdbExecute.executeAdbCommand(receiver_str);
				while ((line = receiver_buf.readLine()) != null) {
					System.out.println("adb out-->" + line);
					result = line;
				}
				Assert.assertEquals("Success", result);
			}
			System.out.println("uninstall test passsed");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Parameters({ "box_wifi", "password" })
	@Test(retryAnalyzer = AdbTests.class, dependsOnMethods = { "uninstall" })
	public void connect_box(String box_wifi, String password) {
		try {
			if (OS.isFamilyMac()) {
				String str = "networksetup -setairportnetwork en0 " + box_wifi + " " + password;
				String[] commands = { "/bin/sh", "-c", str };
				Process pr = Runtime.getRuntime().exec(commands);
				BufferedReader err = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
				Thread.sleep(20000);
				Assert.assertNull(err.readLine());
				System.out.println("connect_box test passed");
			}

			else if (OS.isFamilyWindows()) {
				String str = "netsh wlan connect name= " + box_wifi;
				Process pr = Runtime.getRuntime().exec(str);
				BufferedReader err = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
				Thread.sleep(20000);
				Assert.assertNull(err.readLine());
				System.out.println("connect_box test passed");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test(retryAnalyzer = AdbTests.class, dependsOnMethods = { "connect_box" })
	public void sync_box() {
		try {
			String line = "";
			String result = "";
			String str = "adb connect 192.168.43.1";
			BufferedReader buf = AdbExecute.executeAdbCommand(str);
			result = buf.readLine();
			if (result.equals("already connected to 192.168.43.1:5555")) {
				result = "connected to 192.168.43.1:5555";
			}
			Assert.assertEquals("connected to 192.168.43.1:5555", result);

			str = "adb -s 192.168.43.1:5555 remount";
			buf = AdbExecute.executeAdbCommand(str);
			result = buf.readLine();
			Assert.assertEquals("remount succeeded", result);

			str = "adb -s 192.168.43.1:5555 shell am startservice -a com.switchnwalk.SYNC -n com.reliance.jio.snwbox/.JioSyncService";
			buf = AdbExecute.executeAdbCommand(str);
			result = buf.readLine();
			Assert.assertEquals(
					"Starting service: Intent { act=com.switchnwalk.SYNC cmp=com.reliance.jio.snwbox/.JioSyncService }",
					result);
			System.out.println("sync_box test passed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Parameters({ "url", "uri", "header", "brand" })
	@Test(retryAnalyzer = AdbTests.class, dependsOnMethods = { "sync_box" })
	public void campaign_test(String url, String uri, String header, String brand) {
		try {
			String line = "";
			String result = "";
			String str = "adb connect 192.168.43.1";
			BufferedReader buf = AdbExecute.executeAdbCommand(str);
			result = buf.readLine();
			if (result.equals("already connected to 192.168.43.1:5555")) {
				result = "connected to 192.168.43.1:5555";
			}
			Assert.assertEquals("connected to 192.168.43.1:5555", result);

			str = "adb -s 192.168.43.1:5555 remount";
			buf = AdbExecute.executeAdbCommand(str);
			result = buf.readLine();
			Assert.assertEquals("remount succeeded", result);

			String final_uri = uri + "&brand=" + brand;
			System.out.println("uri = " + final_uri + " header = " + header);
			str = callGet(url, final_uri, header);
			// System.out.println(str);
			JSONArray json = new JSONArray(str);
			JSONObject jsonobject = json.getJSONObject(0);
			// System.out.println("jsonobject from jsonarray=" + jsonobject);
			JSONArray jsonarray = jsonobject.getJSONArray("applications");
			HashMap<String, ArrayList<String>> appDetails = new HashMap();
			int countOfApps = jsonarray.length();
			for (int i = 0; i < countOfApps; i++) {
				ArrayList<String> appDetailsArray = new ArrayList<String>();
				appDetailsArray.add(jsonarray.getJSONObject(i).getString("appPackage"));
				appDetailsArray.add(jsonarray.getJSONObject(i).getString("appSHA256"));
				appDetailsArray.add(jsonarray.getJSONObject(i).getString("filename"));
				appDetails.put(jsonarray.getJSONObject(i).getString("appName"), appDetailsArray);
			}
			// Is any assertions required?
			System.out.println("campaign test passed. Count of apps are" + countOfApps);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Parameters({ "sender_device_id", "receiver_device_id" })
	@Test(retryAnalyzer = AdbTests.class, dependsOnMethods = { "campaign_test" })
	public void connect_phone_to_box(String sender_device_id, String receiver_device_id) {
		try {
			String line = "";
			String result = "";
			String sender_str = "adb -s " + sender_device_id
					+ " shell am start -n com.reliance.automationhelper/.MainActivity -e automation wifi -e state true";
			BufferedReader sender_buf = AdbExecute.executeAdbCommand(sender_str);
			result = sender_buf.readLine();
			System.out.println(result);
			Assert.assertEquals("Starting: Intent { cmp=com.reliance.automationhelper/.MainActivity (has extras) }",
					result);

			sender_str = "adb -s " + sender_device_id
					+ " shell am start -n com.reliance.automationhelper/.MainActivity -e automation wifiConnect -e state true -e ssid JioSwitch_C406 -e pwd 12345678 -e nwtype WPA2";
			sender_buf = AdbExecute.executeAdbCommand(sender_str);
			result = sender_buf.readLine();
			System.out.println(result);
			Assert.assertEquals("Starting: Intent { cmp=com.reliance.automationhelper/.MainActivity (has extras) }",
					result);

			String receiver_str = "adb -s " + receiver_device_id
					+ " shell am start -n com.reliance.automationhelper/.MainActivity -e automation wifi -e state true";
			BufferedReader receiver_buf = AdbExecute.executeAdbCommand(receiver_str);

			result = receiver_buf.readLine();
			System.out.println(result);
			Assert.assertEquals("Starting: Intent { cmp=com.reliance.automationhelper/.MainActivity (has extras) }",
					result);

			receiver_str = "adb -s " + receiver_device_id
					+ " shell am start -n com.reliance.automationhelper/.MainActivity -e automation wifiConnect -e state true -e ssid JioSwitch_C406 -e pwd 12345678 -e nwtype WPA2";
			receiver_buf = AdbExecute.executeAdbCommand(receiver_str);
			result = receiver_buf.readLine();
			Assert.assertEquals("Starting: Intent { cmp=com.reliance.automationhelper/.MainActivity (has extras) }",
					result);

			System.out.println("connect phone to box wifi test passed");
		} catch (

		IOException e)

		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Parameters({ "sender_device_id" })
	@Test(retryAnalyzer = AdbTests.class, dependsOnMethods = { "connect_phone_to_box" })
	public void download_app_on_sender_phone(String sender_device_id) {
		try {
			boolean result;
			result = downloadApp.download(sender_device_id);
			Thread.sleep(30000);
			Assert.assertEquals(true, result);
			System.out.println("download app on sender phone test passed");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Parameters({ "receiver_device_id" })
	@Test(retryAnalyzer = AdbTests.class, dependsOnMethods = { "connect_phone_to_box" })
	public void download_app_on_receiver_phone(String receiver_device_id) {
		try {
			boolean result;
			result = downloadApp.download(receiver_device_id);
			Thread.sleep(30000);
			Assert.assertEquals(true, result);
			System.out.println("download app on receiver phone test passed");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Parameters({ "sender_device_id" })
	@Test(retryAnalyzer = AdbTests.class, dependsOnMethods = { "download_app_on_sender_phone" })
	public void install_app_sender_phone(String sender_device_id) {
		try {
			String line = "";
			String result = "";
			String sender_str = "adb -s " + sender_device_id
					+ " pull /sdcard/download/JioSwitch_196.apk /Users/rakshita/Documents";
			BufferedReader sender_buf = AdbExecute.executeAdbCommand(sender_str);
			result=sender_buf.readLine();
			Assert.assertNull(result);
			System.out.println("apk pulled");

			sender_str = "adb -s " + sender_device_id + " install /Users/rakshita/Documents/JioSwitch_196.apk";
			sender_buf = AdbExecute.executeAdbCommand(sender_str);
			while ((line = sender_buf.readLine()) != null) {
				// System.out.println("adb out-->" +line);
				result = line;
			}
			Assert.assertEquals("Success", result);
			System.out.println("apk installed");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Parameters({ "receiver_device_id" })
	@Test(retryAnalyzer = AdbTests.class, dependsOnMethods = { "download_app_on_receiver_phone" })
	public void install_app_receiver_phone(String receiver_device_id) {
		try {
			String line = "";
			String result = "";
			String receiver_str = "adb -s " + receiver_device_id
					+ " pull /sdcard/download/JioSwitch_196.apk /Users/rakshita/Documents";
			BufferedReader receiver_buf = AdbExecute.executeAdbCommand(receiver_str);
			result=receiver_buf.readLine();
			Assert.assertNotNull(result);
			System.out.println("apk pulled");

			receiver_str = "adb -s " + receiver_device_id + " install /Users/rakshita/Documents/JioSwitch_196.apk";
			receiver_buf = AdbExecute.executeAdbCommand(receiver_str);
			while ((line = receiver_buf.readLine()) != null) {
				// System.out.println("adb out-->" + line);
				result = line;
			}
			Assert.assertEquals("Success", result);
			System.out.println("apk installed");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Parameters({ "receiver_device_id" })
	@Test(retryAnalyzer = AdbTests.class, dependsOnMethods = { "install_app_receiver_phone" })
	public void download_install_apps_receiver_phone(String receiver_device_id) {
		boolean result = installApps.install(receiver_device_id);
		Assert.assertEquals(true, result);
		System.out.println("installed all apps. Test passed");
	}
}
