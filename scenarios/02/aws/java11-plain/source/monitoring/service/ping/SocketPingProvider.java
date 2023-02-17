package com.monitoring.service.ping;

import java.io.IOException;
import java.net.InetAddress;

public class SocketPingProvider implements PingProvider {
    @Override
    public String getStatus(String ip, int timeOutMS) {
        try {
            byte[] ipBytes = new byte[4];
            String[] ipParts = ip.split("\\.");
            for (int i = 0; i < 4; i++) {
                ipBytes[i] = (byte) Integer.parseInt(ipParts[i]);
            }
            InetAddress inetAddress = InetAddress.getByAddress(ipBytes);
            return inetAddress.isReachable(timeOutMS) ? "UP" : "DOWN";
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "DOWN" + e.getMessage();
        }
    }
}
