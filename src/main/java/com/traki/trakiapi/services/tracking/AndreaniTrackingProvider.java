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
                        TrackingResult.EventRecord.builder().status("EN REPARTO").description("El paquete está en manos del jijolin").date(LocalDateTime.now()).location("Planta Córdoba").build(),
                        TrackingResult.EventRecord.builder().status("INGRESADO").description("El paquete ingresó al centro de distribución").date(LocalDateTime.now().minusDays(1)).location("Planta Buenos Aires").build()
                ))
                .build();
    }
}
