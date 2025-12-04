package com.aidemo.ui.driver.util;

import static com.aidemo.ui.constants.DriverConstants.APPIUM_SERVER_IP;
import static com.aidemo.ui.constants.DriverConstants.APPIUM_SERVER_PORT;


import java.io.File;
import java.net.MalformedURLException;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public interface MobileDriverService {
	

    default AppiumDriverLocalService startAppiumService() {
        AppiumDriverLocalService appiumService = AppiumDriverLocalService.buildService(
                new AppiumServiceBuilder()
                        .withIPAddress(APPIUM_SERVER_IP)
                        .usingPort(APPIUM_SERVER_PORT)
                        // Specify plugins to use
                       // .withArgument(() -> "--use-plugins", "ai-appium-lens")
                        .withArgument(() -> "--allow-cors")
                        // Optional: Specify the path to the Appium executable if needed
                        .withAppiumJS(new File("/Users/bushan-patil/.nvm/versions/node/v20.14.0/bin/appium"))
                        .usingDriverExecutable(new File("/Users/bushan-patil/.nvm/versions/node/v20.14.0/bin/npm"))
        );
        appiumService.start();
        System.out.println("Appium server started with plugins: relaxed-caps, appium-device-farm");
        return appiumService;
    }

    default void stopAppiumService(AppiumDriverLocalService appiumService) {
        appiumService.stop();
    }

    void spinUpDriver(AppiumDriverLocalService appiumService) throws MalformedURLException;

    void closeDriver();

    AppiumDriver getDriver();


}
