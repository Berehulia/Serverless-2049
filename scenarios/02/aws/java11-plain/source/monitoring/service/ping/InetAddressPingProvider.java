package com.monitoring.service.ping;

import java.io.IOException;
import java.net.InetAddress;

public class InetAddressPingProvider implements PingProvider {


    @Override
    public String getStatus(String ip, int timeOutMS) {
        try {
            byte[] ipBytes = new byte[4];
            String[] ipParts = ip.split("\\.");
            for (int i = 0; i < 4; i++) {
                ipBytes[i] = (byte) Integer.parseInt(ipParts[i]);
            }
            InetAddress inetAddress = InetAddress.getByAddress(ipBytes);
            boolean isReachable = inetAddress.isReachable(timeOutMS);
            System.out.println("InetAddressPingProvider: IP " + ip + " isReachable=" + isReachable + " " + getBinary(ipBytes));
            return isReachable ? "UP" : "DOWN";
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "DOWN";
        }
    }

    private String getBinary(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            result += String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0') + " ";
        }
        return result;
    }

}
