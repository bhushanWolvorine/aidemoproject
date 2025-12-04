package com.aidemo.test.healthcheck;

import com.aidemo.integration.pojo.HealthResponse;
import com.aidemo.integration.pojo.StatsResponse;
import com.aidemoproject.restclient.RestClientChatServer;
import org.testng.annotations.Test;
import org.testng.Assert;


import java.util.Map;

public class TestHealthCheckCases {


    private final RestClientChatServer chatClient = new RestClientChatServer();

    @Test
    public void verifyHealthEndpointReturnsCorrectPojo() {
        HealthResponse health = chatClient.getHealth()
                .then()
                .statusCode(200)
                .extract()
                .as(HealthResponse.class);  // Magic line

        Assert.assertEquals(health.getStatus(), "healthy");
        Assert.assertEquals(health.getService(), "UPI Mock Agent");
        Assert.assertTrue(health.getActiveConnections() >= 0);
    }

    @Test
    public void verifyFakePaymentIncreasesCounter() {
        chatClient.postExecutePayment(Map.of("amount", 3000, "payee", "Mom"));

        StatsResponse stats = chatClient.getStats()
                .then()
                .statusCode(200)
                .extract()
                .as(StatsResponse.class);

        Assert.assertEquals(stats.getFailures().getFakeSuccess(), 1);
        Assert.assertTrue(stats.getTotalProcessed() >= 1);
    }
}
