package com.dc3.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dc3.common.context.TenantContext;
import com.dc3.common.enums.EventType;
import com.dc3.domain.dto.WarehouseInboundMessage;
import com.dc3.domain.dto.WarehouseOutboundMessage;
import com.dc3.domain.dto.WarehouseStocktakeMessage;
import com.dc3.domain.entity.InventoryTransaction;
import com.dc3.domain.entity.StocktakeRecord;
import com.dc3.repository.mapper.InventoryTransactionMapper;
import com.dc3.repository.mapper.StocktakeRecordMapper;
import com.dc3.service.WarehouseEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class WarehouseEventServiceImpl implements WarehouseEventService {

    private static final Logger log = LoggerFactory.getLogger(WarehouseEventServiceImpl.class);

    @Autowired
    private InventoryTransactionMapper transactionMapper;

    @Autowired
    private StocktakeRecordMapper stocktakeRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleInbound(WarehouseInboundMessage message) {
        try {
            TenantContext.setTenantId(message.getTenantId());
            LocalDateTime eventTime = message.getInboundTime() != null ? message.getInboundTime() : LocalDateTime.now();

            LambdaQueryWrapper<InventoryTransaction> existWrapper = new LambdaQueryWrapper<>();
            existWrapper.eq(InventoryTransaction::getBizNo, message.getInboundNo());
            if (transactionMapper.selectCount(existWrapper) > 0) {
                log.warn("入库单已处理，跳过: {}", message.getInboundNo());
                return;
            }

            for (WarehouseInboundMessage.InboundItem item : message.getItems()) {
                InventoryTransaction tx = new InventoryTransaction();
                tx.setTransactionNo("IN" + IdUtil.getSnowflakeNextIdStr());
                tx.setWarehouseCode(message.getWarehouseCode());
                tx.setSkuCode(item.getSkuCode());
                tx.setEventType(EventType.INBOUND.getCode());
                tx.setBizType(message.getInboundType());
                tx.setBizNo(message.getInboundNo());
                tx.setQuantity(item.getQuantity());
                tx.setBeforeQuantity(0);
                tx.setAfterQuantity(item.getQuantity());
                tx.setReferenceNo(message.getReferenceNo());
                tx.setOperator(message.getOperator());
                tx.setEventTime(eventTime);
                tx.setRemark(message.getRemark());
                transactionMapper.insert(tx);
            }
            log.info("入库消息处理成功: tenantId={}, inboundNo={}", message.getTenantId(), message.getInboundNo());
        } finally {
            TenantContext.clearAll();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleOutbound(WarehouseOutboundMessage message) {
        try {
            TenantContext.setTenantId(message.getTenantId());
            LocalDateTime eventTime = message.getOutboundTime() != null ? message.getOutboundTime() : LocalDateTime.now();

            LambdaQueryWrapper<InventoryTransaction> existWrapper = new LambdaQueryWrapper<>();
            existWrapper.eq(InventoryTransaction::getBizNo, message.getOutboundNo());
            if (transactionMapper.selectCount(existWrapper) > 0) {
                log.warn("出库单已处理，跳过: {}", message.getOutboundNo());
                return;
            }

            for (WarehouseOutboundMessage.OutboundItem item : message.getItems()) {
                InventoryTransaction tx = new InventoryTransaction();
                tx.setTransactionNo("OUT" + IdUtil.getSnowflakeNextIdStr());
                tx.setWarehouseCode(message.getWarehouseCode());
                tx.setSkuCode(item.getSkuCode());
                tx.setEventType(EventType.OUTBOUND.getCode());
                tx.setBizType(message.getOutboundType());
                tx.setBizNo(message.getOutboundNo());
                tx.setQuantity(item.getQuantity());
                tx.setBeforeQuantity(0);
                tx.setAfterQuantity(-item.getQuantity());
                tx.setReferenceNo(message.getReferenceNo());
                tx.setOperator(message.getOperator());
                tx.setEventTime(eventTime);
                tx.setRemark(message.getRemark());
                transactionMapper.insert(tx);
            }
            log.info("出库消息处理成功: tenantId={}, outboundNo={}", message.getTenantId(), message.getOutboundNo());
        } finally {
            TenantContext.clearAll();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleStocktake(WarehouseStocktakeMessage message) {
        try {
            TenantContext.setTenantId(message.getTenantId());
            LocalDateTime eventTime = message.getStocktakeTime() != null ? message.getStocktakeTime() : LocalDateTime.now();

            LambdaQueryWrapper<StocktakeRecord> existWrapper = new LambdaQueryWrapper<>();
            existWrapper.eq(StocktakeRecord::getStocktakeNo, message.getStocktakeNo());
            if (stocktakeRecordMapper.selectCount(existWrapper) > 0) {
                log.warn("盘点单已处理，跳过: {}", message.getStocktakeNo());
                return;
            }

            for (WarehouseStocktakeMessage.StocktakeItem item : message.getItems()) {
                StocktakeRecord record = new StocktakeRecord();
                record.setStocktakeNo(message.getStocktakeNo());
                record.setWarehouseCode(message.getWarehouseCode());
                record.setSkuCode(item.getSkuCode());
                record.setSystemQuantity(item.getSystemQuantity());
                record.setPhysicalQuantity(item.getPhysicalQuantity());
                record.setDifferenceQuantity(item.getDifferenceQuantity() != null
                        ? item.getDifferenceQuantity()
                        : item.getPhysicalQuantity() - item.getSystemQuantity());
                record.setReason(item.getReason());
                record.setOperator(message.getOperator());
                record.setStocktakeTime(eventTime);
                record.setRemark(message.getRemark());
                stocktakeRecordMapper.insert(record);

                InventoryTransaction tx = new InventoryTransaction();
                tx.setTransactionNo("ST" + IdUtil.getSnowflakeNextIdStr());
                tx.setWarehouseCode(message.getWarehouseCode());
                tx.setSkuCode(item.getSkuCode());
                tx.setEventType(EventType.STOCKTAKE.getCode());
                tx.setBizType(1);
                tx.setBizNo(message.getStocktakeNo());
                tx.setQuantity(record.getDifferenceQuantity());
                tx.setBeforeQuantity(item.getSystemQuantity());
                tx.setAfterQuantity(item.getPhysicalQuantity());
                tx.setReferenceNo(message.getStocktakeNo());
                tx.setOperator(message.getOperator());
                tx.setEventTime(eventTime);
                tx.setRemark(item.getReason());
                transactionMapper.insert(tx);
            }
            log.info("盘点消息处理成功: tenantId={}, stocktakeNo={}", message.getTenantId(), message.getStocktakeNo());
        } finally {
            TenantContext.clearAll();
        }
    }
}
