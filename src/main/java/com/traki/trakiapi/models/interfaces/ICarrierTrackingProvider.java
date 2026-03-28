package com.traki.trakiapi.models.interfaces;

import com.traki.trakiapi.models.dtos.TrackingResult;
import com.traki.trakiapi.models.enums.CarrierType;

public interface ICarrierTrackingProvider {
    CarrierType supports();
    TrackingResult getTrackingInfo(String trackingNumber);
}
