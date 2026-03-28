package com.traki.trakiapi.services.tracking;

import com.traki.trakiapi.models.dtos.TrackingResult;
import com.traki.trakiapi.models.enums.CarrierType;
import com.traki.trakiapi.models.interfaces.ICarrierTrackingProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AndreaniTrackingProvider implements ICarrierTrackingProvider {

    @Override
    public CarrierType supports() {
        return CarrierType.ANDREANI;
    }

    @Override
    public TrackingResult getTrackingInfo(String trackingNumber) {

        // TODO: Implementar la logica de tracking para Andreani

        return TrackingResult.builder()
                .trackingNumber(trackingNumber)
                .carrier(CarrierType.ANDREANI)
                .currentStatus("despachao el delga")
                .estimatedDelivery(LocalDateTime.now().plusDays(1))
                .events(List.of(
                        new TrackingResult.EventRecord("EN REPARTO", "El paquete está en manos del jijolin", LocalDateTime.now(), "Planta Córdoba"),
                        new TrackingResult.EventRecord("INGRESADO", "El paquete ingresó al centro de distribución", LocalDateTime.now().minusDays(1), "Planta Buenos Aires")
                ))
                .build();
    }
}
