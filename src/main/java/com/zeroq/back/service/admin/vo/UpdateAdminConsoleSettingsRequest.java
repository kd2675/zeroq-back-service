package com.zeroq.back.service.admin.vo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdminConsoleSettingsRequest {
    @Min(1)
    private int overcapacityLimit;

    @Min(1)
    @Max(100)
    private int warningBufferPercent;

    @NotBlank
    private String sensorRawDataRetention;

    @NotBlank
    private String systemErrorRetention;

    @NotBlank
    private String alertHistoryRetention;

    private boolean allNotificationsEnabled;
    private boolean occupancyNotificationsEnabled;
    private boolean batteryNotificationsEnabled;
    private boolean emailNotificationsEnabled;
    private boolean pushNotificationsEnabled;
    private boolean smsNotificationsEnabled;
}
