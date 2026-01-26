package com.zeroq.back.database.pub.entity;

/**
 * 혼잡도 레벨
 */
public enum CrowdLevel {
    EMPTY(0, 10, "거의 비어있음"),
    LOW(11, 35, "한산함"),
    MEDIUM(36, 65, "보통"),
    HIGH(66, 85, "붐비는중"),
    FULL(86, 100, "가득참");

    private final int minOccupancy;
    private final int maxOccupancy;
    private final String description;

    CrowdLevel(int minOccupancy, int maxOccupancy, String description) {
        this.minOccupancy = minOccupancy;
        this.maxOccupancy = maxOccupancy;
        this.description = description;
    }

    public static CrowdLevel fromOccupancyPercentage(int percentage) {
        if (percentage <= 10) return EMPTY;
        if (percentage <= 35) return LOW;
        if (percentage <= 65) return MEDIUM;
        if (percentage <= 85) return HIGH;
        return FULL;
    }

    public int getMinOccupancy() {
        return minOccupancy;
    }

    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    public String getDescription() {
        return description;
    }
}
