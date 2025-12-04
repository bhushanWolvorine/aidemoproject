package com.aidemo.ui.driver.util;

import java.net.MalformedURLException;
import java.time.Duration;


import com.aidemo.ui.constants.DriverConstants;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;

public class AndroidDriverServiceImpl implements MobileDriverService {

	private AndroidDriver androidDriver;

	@Override
	public void spinUpDriver(AppiumDriverLocalService appiumService) throws MalformedURLException {

		UiAutomator2Options options = new UiAutomator2Options();
		options.setUdid(DriverConstants.ANDROID_DEVICE_NAME).setAppActivity(DriverConstants.ANDROID_APP_ACTIVITY)
				.setAppPackage(DriverConstants.ANDROID_APP_PACKAGE).setNewCommandTimeout(Duration.ofSeconds(180))
				.setNoReset(Boolean.parseBoolean(DriverConstants.ANDROID_NO_RESET))
				.setFullReset(Boolean.parseBoolean(DriverConstants.ANDROID_FULL_RESET)).setAppWaitForLaunch(true)
				.autoGrantPermissions();

		androidDriver = new AndroidDriver(appiumService.getUrl(), options);

		androidDriver.manage().timeouts().implicitlyWait(DriverConstants.APPIUM_DRIVER_TIMEOUT);

	}

	@Override
	public void closeDriver() {
		androidDriver.terminateApp(DriverConstants.ANDROID_APP_PACKAGE);
		androidDriver.quit();

	}

	@Override
	public AppiumDriver getDriver() {

		return androidDriver;
	}

}
