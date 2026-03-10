package com.traki.trakiapi.models.dtos;

import com.traki.trakiapi.models.enums.CarrierType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingResult {

    private String trackingNumber;
    private CarrierType carrier;
    private String currentStatus;
    private LocalDateTime estimatedDelivery;
    private List<EventRecord> events;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventRecord {
        private String status;
        private String description;
        private LocalDateTime date;
        private String location;
    }
}
