package com.traki.trakiapi.models.dtos;

import com.traki.trakiapi.models.enums.CarrierType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TrackingResult {

    private String trackingNumber;
    private CarrierType carrier;
    private String currentStatus;
    private LocalDateTime estimatedDelivery;
    private List<EventRecord> events;

    public record EventRecord(
            String status,
            String description,
            LocalDateTime timestamp,
            String location
    ) {}
}
