package com.aidemo.ui.constants;



import com.aidemoproject.utils.LoggerUtil;

import static com.aidemo.ui.constants.DriverConstants.ANDROID;


public class CommonConstants {




    private final static String DEV = "dev";

    private final static String GAME = "rummy";
    public static final String MOBILE_PLATFORM_NAME = getPlatformName();
    public static final String EXECUTION_ENV_NAME = getEnvironmentName();

    public static final String GAME_ENV_NAME = getGameName();

    private static String getEnvironmentName() {
        String environmentNameFromPomXml = System.getProperty("environment");
        String envName;

        if (environmentNameFromPomXml != null)
            envName = environmentNameFromPomXml;
        else {
            LoggerUtil.warn("The Maven Profile is missing the environment configuration.");
            LoggerUtil.warn("The default environment '{}' will be enabled for this run.");
            envName = DEV;
        }

        return envName.toLowerCase();
    }


    private static String getGameName() {
        String environmentNameFromPomXml = System.getProperty("app");
        String gameName;

        if (environmentNameFromPomXml != null)
            gameName = environmentNameFromPomXml;
        else {
            LoggerUtil.warn("The Maven Profile is missing the environment configuration.");
            LoggerUtil.warn("The default environment '{}' will be enabled for this run.");
            gameName = GAME;
        }

        return gameName.toLowerCase();
    }

    private static String getPlatformName() {
        String platformNameFromPomXml = System.getProperty("platform");
        String platformName;

        if (platformNameFromPomXml != null)
            platformName = platformNameFromPomXml;
        else {
            LoggerUtil.warn("The Maven Profile is missing the platform configuration.");
            LoggerUtil.warn("The default platform '{}' will be enabled for this run.");
            platformName = ANDROID;
        }

        return platformName.toLowerCase();
    }

}
