package com.monitoring.service.ping;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProcessBuilderPingProvider implements PingProvider {


    @Override
    public String getStatus(String ip, int timeOutMS) {
        try {
            boolean isReachable = ping(ip, timeOutMS);
            System.out.println(
                    "ProcessBuilderPingProvider: IP " + ip + " isReachable=" + isReachable);
            return isReachable ? "UP" : "DOWN";
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            return "DOWN";
        }
    }

    private boolean ping(String host, int timeoutMS) throws IOException, InterruptedException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

        ProcessBuilder processBuilder = new ProcessBuilder("ping", isWindows ? "-n" : "-c", "1", host);
        Process proc = processBuilder.start();

        return proc.waitFor(timeoutMS, TimeUnit.MILLISECONDS);
    }
}
