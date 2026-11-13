package com.webcodein.workshop.realtime.protocol;

import com.webcodein.workshop.realtime.model.DeliveryGuarantee;
import com.webcodein.workshop.realtime.model.Directionality;
import com.webcodein.workshop.realtime.model.FrameResult;
import com.webcodein.workshop.realtime.model.MessagePayload;
import com.webcodein.workshop.realtime.model.TransportLayer;

public interface RealTimeProtocol {
    String name();
    TransportLayer transport();
    Directionality directionality();
    boolean supportsMultiplexing();
    boolean supportsDatagrams();
    FrameResult transmit(MessagePayload payload, DeliveryGuarantee guarantee);
}
