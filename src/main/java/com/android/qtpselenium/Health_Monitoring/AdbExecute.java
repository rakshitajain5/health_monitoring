package com.android.qtpselenium.Health_Monitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.exec.OS;

public class AdbExecute {
	@SuppressWarnings("finally")
	public static BufferedReader executeAdbCommand(String cmd) throws IOException {
		BufferedReader buf = null;
		try {
			//System.out.println("Executing command :" + cmd);
			Runtime run = Runtime.getRuntime();
			Process pr = null;
			if (OS.isFamilyMac()) {
				String[] commands = { "/bin/sh", "-c", "/Users/rakshita/Library/Android/sdk/platform-tools/" + cmd };
				pr = run.exec(commands);
			} else if (OS.isFamilyWindows()) {
				pr = run.exec(cmd);
			}
			pr.waitFor();
			buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));

		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			//System.out.println("Getting out of callAdb");
			return buf;
		}
	}

}
