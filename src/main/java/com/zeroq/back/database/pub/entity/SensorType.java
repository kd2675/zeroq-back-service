package com.zeroq.back.database.pub.entity;

/**
 * 센서 타입 Enum
 */
public enum SensorType {
    OCCUPANCY_DETECTION("점유 감지", "사람 수를 감지하는 센서"),
    TEMPERATURE("온도 센서", "온도를 측정하는 센서"),
    HUMIDITY("습도 센서", "습도를 측정하는 센서"),
    CO2("CO2 센서", "이산화탄소 농도를 측정하는 센서"),
    LIGHT("조도 센서", "밝기를 측정하는 센서"),
    MOTION("동작 감지", "움직임을 감지하는 센서"),
    NOISE("소음 센서", "소음 수준을 측정하는 센서"),
    AIR_QUALITY("공기질 센서", "공기질을 측정하는 센서");

    private final String name;
    private final String description;

    SensorType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
