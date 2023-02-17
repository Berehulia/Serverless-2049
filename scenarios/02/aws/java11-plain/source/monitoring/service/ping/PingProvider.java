package com.monitoring.service.ping;

public interface PingProvider {
    String getStatus(String ip, int timeOutMS);
}
