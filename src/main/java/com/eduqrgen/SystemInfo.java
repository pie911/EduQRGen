package com.eduqrgen;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

public class SystemInfo {
    
    public static String getOSDetails() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        return String.format("OS: %s, Version: %s, Arch: %s", 
                             osBean.getName(), osBean.getVersion(), osBean.getArch());
    }
    
    public static long getJVMUptime() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        return runtimeBean.getUptime();
    }
    
    public static void printSystemInfo() {
        System.out.println(getOSDetails());
        System.out.println("JVM Uptime: " + getJVMUptime() + " ms");
    }
    
    public static void main(String[] args) {
        printSystemInfo();
    }
}