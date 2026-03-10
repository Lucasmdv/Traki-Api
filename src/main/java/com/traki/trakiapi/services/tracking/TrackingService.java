package com.traki.trakiapi.services.tracking;

import com.traki.trakiapi.models.dtos.TrackingResult;
import com.traki.trakiapi.models.enums.CarrierType;
import com.traki.trakiapi.models.exceptions.CourierNotFoundException;
import com.traki.trakiapi.models.interfaces.ICarrierTrackingProvider;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TrackingService {

    private final Map<CarrierType, ICarrierTrackingProvider> providers;

    public TrackingService(List<ICarrierTrackingProvider> providerList) {
    
        this.providers = providerList.stream()
                .collect(Collectors.toMap(ICarrierTrackingProvider::supports, Function.identity()));
    }

    /**
     * Executes the tracking request using the appropriate provider.
     * @param carrierType The selected carrier
     * @param trackingNumber The package tracking number
     * @return TrackingResult with normalized status and history
     */
    public TrackingResult track(CarrierType carrierType, String trackingNumber) {
        ICarrierTrackingProvider provider = providers.get(carrierType);
        
        if (provider == null) {
            throw new CourierNotFoundException("Courier not supported or not implemented: " + carrierType);
        }
        
        return provider.getTrackingInfo(trackingNumber);
    }
}
