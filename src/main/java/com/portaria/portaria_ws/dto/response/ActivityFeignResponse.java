package com.portaria.portaria_ws.dto.response;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ActivityFeignResponse {

    private String type;
    private Timestamp activityTime;

}