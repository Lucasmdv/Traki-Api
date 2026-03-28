package com.traki.trakiapi.models.interfaces;

import com.traki.trakiapi.models.dtos.TrackingResult;
import com.traki.trakiapi.models.enums.CarrierType;

public interface ICarrierTrackingProvider {
    
    /**
     * Identifies which carrier this provider implements.
     * @return the carrier type
     */
    CarrierType supports();

    /**
     * Fetches tracking information for a given number.
     * @param trackingNumber the carrier's tracking number
     * @return a unified TrackingResult with the package status and history
     */
    TrackingResult getTrackingInfo(String trackingNumber);
}
