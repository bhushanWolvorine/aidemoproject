package com.metrics;


import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import java.io.IOException;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;

public class MetricsTracker {

    private static final Gauge JUDGE_CALLS = Gauge.build()
            .name("openai_judge_calls_total")
            .help("Total OpenAI judge calls")
            .register();

    private static final Gauge JUDGE_TOKENS = Gauge.build()
            .name("openai_judge_tokens_total")
            .help("Total tokens used")
            .register();

    private static final Gauge ESTIMATED_COST = Gauge.build()
            .name("openai_judge_estimated_cost_usd")
            .help("Estimated cost in USD")
            .register();

    private static final PushGateway pg = new PushGateway("localhost:9091");
    private static final String JOB_NAME = "java-automation-tests";

    public static void recordJudgeCall(long tokens) {
        JUDGE_CALLS.inc();
        JUDGE_TOKENS.inc(tokens);
        ESTIMATED_COST.inc(tokens * 0.000010); // gpt-4o price

        // Push every time (or every 10 calls if you want)
        try {
            pg.pushAdd(CollectorRegistry.defaultRegistry, JOB_NAME);
        } catch (Exception e) {
            System.err.println("Failed to push metrics: " + e.getMessage());
        }
    }


    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                pg.pushAdd(CollectorRegistry.defaultRegistry, JOB_NAME);
                System.out.println("Final metrics pushed to PushGateway");
            } catch (Exception e) {
                System.err.println("Shutdown push failed: " + e.getMessage());
            }
        }));
    }
}