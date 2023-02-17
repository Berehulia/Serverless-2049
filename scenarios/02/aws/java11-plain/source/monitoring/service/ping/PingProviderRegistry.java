package com.monitoring.service.ping;

import java.util.Objects;

public class PingProviderRegistry {

    public PingProvider getByName(String name) {
        if (Objects.nonNull(name)) {
            switch (name) {
                case "InetAddress":
                    return new InetAddressPingProvider();
                case "ProcessBuilder":
                    return new ProcessBuilderPingProvider();
                default:
            }
        }
        return new InetAddressPingProvider();
    }
}
