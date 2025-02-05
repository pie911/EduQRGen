// Placeholder for src/main/java/com/eduqrgen/SystemInfo.java
package com.eduqrgen;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.text.DecimalFormat;
import java.util.Properties;

/**
 * Provides system information for debugging and optimization.
 */
public class SystemInfo {

    /**
     * Gets detailed system information.
     *
     * @return Formatted system details
     */
    public static String getSystemDetails() {
        StringBuilder sb = new StringBuilder();
        Properties properties = System.getProperties();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

        sb.append("===== SYSTEM INFORMATION =====\n");
        sb.append("OS: ").append(properties.getProperty("os.name"))
          .append(" (").append(properties.getProperty("os.version")).append(")\n");
        sb.append("Architecture: ").append(properties.getProperty("os.arch")).append("\n");
        sb.append("Available Processors: ").append(osBean.getAvailableProcessors()).append("\n");
        sb.append("JVM Name: ").append(runtimeBean.getVmName()).append("\n");
        sb.append("JVM Version: ").append(runtimeBean.getVmVersion()).append("\n");
        sb.append("Java Home: ").append(properties.getProperty("java.home")).append("\n");

        sb.append(getMemoryDetails());
        sb.append(getStorageDetails());

        return sb.toString();
    }

    /**
     * Retrieves memory details (RAM usage).
     */
    private static String getMemoryDetails() {
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        DecimalFormat df = new DecimalFormat("#.##");

        return "\n===== MEMORY USAGE =====\n"
                + "Free Memory: " + df.format(freeMemory / (1024.0 * 1024)) + " MB\n"
                + "Total Memory: " + df.format(totalMemory / (1024.0 * 1024)) + " MB\n"
                + "Max Memory: " + df.format(maxMemory / (1024.0 * 1024)) + " MB\n";
    }

    /**
     * Retrieves disk storage details.
     */
    private static String getStorageDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n===== STORAGE DETAILS =====\n");

        try {
            for (FileStore store : FileSystems.getDefault().getFileStores()) {
                long totalSpace = store.getTotalSpace();
                long usableSpace = store.getUsableSpace();
                DecimalFormat df = new DecimalFormat("#.##");

                sb.append("Disk: ").append(store.toString()).append("\n")
                  .append("Total Space: ").append(df.format(totalSpace / (1024.0 * 1024 * 1024))).append(" GB\n")
                  .append("Usable Space: ").append(df.format(usableSpace / (1024.0 * 1024 * 1024))).append(" GB\n\n");
            }
        } catch (Exception e) {
            sb.append("❌ Error retrieving storage details: ").append(e.getMessage()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Prints system info to the console.
     */
    public static void printSystemInfo() {
        System.out.println(getSystemDetails());
    }

    public static void main(String[] args) {
        printSystemInfo();  // Run to display system info
    }
}
