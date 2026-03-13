package com.zeroq.back.service.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminConsoleSettingsResponse {
    private int overcapacityLimit;
    private int warningBufferPercent;
    private String sensorRawDataRetention;
    private String systemErrorRetention;
    private String alertHistoryRetention;
    private boolean allNotificationsEnabled;
    private boolean occupancyNotificationsEnabled;
    private boolean batteryNotificationsEnabled;
    private boolean emailNotificationsEnabled;
    private boolean pushNotificationsEnabled;
    private boolean smsNotificationsEnabled;
    private Long managedByProfileId;
    private String role;
}
