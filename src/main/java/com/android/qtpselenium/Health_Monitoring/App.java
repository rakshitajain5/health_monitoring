package com.android.qtpselenium.Health_Monitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.android.qtpselenium.Health_Monitoring.AdbExecute;

import org.apache.commons.exec.OS;

/**
 * Hello world!
 *
 */
public class App {
	

	public static void main(String[] args) {
		String sender_device_id = "4df7e18f440c30c9";
		String receiver_device_id = "";
		try {
			System.out.println("Hello World!");
			String str = "adb -s " +sender_device_id+" uninstall com.reliance.jio.jioswitch";
			AdbExecute.executeAdbCommand(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
