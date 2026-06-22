package com.dc3.service;

import com.dc3.domain.dto.WarehouseInboundMessage;
import com.dc3.domain.dto.WarehouseOutboundMessage;
import com.dc3.domain.dto.WarehouseStocktakeMessage;

public interface WarehouseEventService {

    void handleInbound(WarehouseInboundMessage message);

    void handleOutbound(WarehouseOutboundMessage message);

    void handleStocktake(WarehouseStocktakeMessage message);
}
