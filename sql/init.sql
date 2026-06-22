-- ============================================================
-- DC3 供应链库存对账系统 - 数据库初始化脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS dc3_reconciliation
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE dc3_reconciliation;

-- -----------------------------------------------------------
-- 租户表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `tenant`;
CREATE TABLE `tenant` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`     VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '租户ID',
    `tenant_code`   VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '租户编码',
    `tenant_name`   VARCHAR(128)    NOT NULL DEFAULT '' COMMENT '租户名称',
    `contact_name`  VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '联系人',
    `contact_phone` VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '联系电话',
    `contact_email` VARCHAR(128)    NOT NULL DEFAULT '' COMMENT '联系邮箱',
    `status`        TINYINT         NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    `remark`        VARCHAR(512)    NOT NULL DEFAULT '' COMMENT '备注',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`    TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除 0否 1是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_id` (`tenant_id`),
    UNIQUE KEY `uk_tenant_code` (`tenant_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户表';

-- -----------------------------------------------------------
-- 仓库表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `warehouse`;
CREATE TABLE `warehouse` (
    `id`             BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`      VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '租户ID',
    `warehouse_code` VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '仓库编码',
    `warehouse_name` VARCHAR(128)    NOT NULL DEFAULT '' COMMENT '仓库名称',
    `country`        VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '国家',
    `province`       VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '省份',
    `city`           VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '城市',
    `address`        VARCHAR(256)    NOT NULL DEFAULT '' COMMENT '详细地址',
    `status`         TINYINT         NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    `remark`         VARCHAR(512)    NOT NULL DEFAULT '' COMMENT '备注',
    `create_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`     TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除 0否 1是',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    UNIQUE KEY `uk_tenant_warehouse` (`tenant_id`, `warehouse_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仓库表';

-- -----------------------------------------------------------
-- 商品表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id`           BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`    VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '租户ID',
    `sku_code`     VARCHAR(64)     NOT NULL DEFAULT '' COMMENT 'SKU编码',
    `product_name` VARCHAR(256)    NOT NULL DEFAULT '' COMMENT '商品名称',
    `category`     VARCHAR(128)    NOT NULL DEFAULT '' COMMENT '类目',
    `brand`        VARCHAR(128)    NOT NULL DEFAULT '' COMMENT '品牌',
    `unit`         VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '单位',
    `cost_price`   DECIMAL(18,4)   NOT NULL DEFAULT 0.0000 COMMENT '成本价',
    `sale_price`   DECIMAL(18,4)   NOT NULL DEFAULT 0.0000 COMMENT '销售价',
    `barcode`      VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '条形码',
    `remark`       VARCHAR(512)    NOT NULL DEFAULT '' COMMENT '备注',
    `create_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`   TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除 0否 1是',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    UNIQUE KEY `uk_tenant_sku` (`tenant_id`, `sku_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- -----------------------------------------------------------
-- 库存流水表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `inventory_transaction`;
CREATE TABLE `inventory_transaction` (
    `id`               BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`        VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '租户ID',
    `transaction_no`   VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '流水号',
    `warehouse_code`   VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '仓库编码',
    `sku_code`         VARCHAR(64)     NOT NULL DEFAULT '' COMMENT 'SKU编码',
    `event_type`       TINYINT         NOT NULL DEFAULT 0 COMMENT '事件类型 1入库 2出库 3盘点',
    `biz_type`         TINYINT         NOT NULL DEFAULT 0 COMMENT '业务子类型',
    `biz_no`           VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '业务单号(入库单号/出库单号/盘点单号)',
    `quantity`         INT             NOT NULL DEFAULT 0 COMMENT '变动数量(正数增加,负数减少)',
    `before_quantity`  INT             NOT NULL DEFAULT 0 COMMENT '变动前数量',
    `after_quantity`   INT             NOT NULL DEFAULT 0 COMMENT '变动后数量',
    `reference_no`     VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '关联单号',
    `operator`         VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '操作人',
    `event_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '事件发生时间',
    `remark`           VARCHAR(512)    NOT NULL DEFAULT '' COMMENT '备注',
    `create_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`       TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除 0否 1是',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_tenant_event_time` (`tenant_id`, `event_time`),
    KEY `idx_tenant_sku` (`tenant_id`, `sku_code`),
    KEY `idx_biz_no` (`biz_no`),
    KEY `idx_transaction_no` (`transaction_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存流水表';

-- -----------------------------------------------------------
-- 库存快照表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `inventory_snapshot`;
CREATE TABLE `inventory_snapshot` (
    `id`                 BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`          VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '租户ID',
    `warehouse_code`     VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '仓库编码',
    `sku_code`           VARCHAR(64)     NOT NULL DEFAULT '' COMMENT 'SKU编码',
    `book_quantity`      INT             NOT NULL DEFAULT 0 COMMENT '账面库存',
    `physical_quantity`  INT             NOT NULL DEFAULT 0 COMMENT '实物库存',
    `snapshot_date`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '快照时间',
    `remark`             VARCHAR(512)    NOT NULL DEFAULT '' COMMENT '备注',
    `create_time`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`         TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除 0否 1是',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_tenant_snapshot_date` (`tenant_id`, `snapshot_date`),
    UNIQUE KEY `uk_tenant_sku_date` (`tenant_id`, `warehouse_code`, `sku_code`, `snapshot_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存快照表';

-- -----------------------------------------------------------
-- 盘点记录表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `stocktake_record`;
CREATE TABLE `stocktake_record` (
    `id`                  BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`           VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '租户ID',
    `stocktake_no`        VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '盘点单号',
    `warehouse_code`      VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '仓库编码',
    `sku_code`            VARCHAR(64)     NOT NULL DEFAULT '' COMMENT 'SKU编码',
    `system_quantity`     INT             NOT NULL DEFAULT 0 COMMENT '系统数量',
    `physical_quantity`   INT             NOT NULL DEFAULT 0 COMMENT '实盘数量',
    `difference_quantity` INT             NOT NULL DEFAULT 0 COMMENT '差异数量',
    `reason`              VARCHAR(256)    NOT NULL DEFAULT '' COMMENT '差异原因',
    `operator`            VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '盘点人',
    `stocktake_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '盘点时间',
    `remark`              VARCHAR(512)    NOT NULL DEFAULT '' COMMENT '备注',
    `create_time`         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`          TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除 0否 1是',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_tenant_stocktake_time` (`tenant_id`, `stocktake_time`),
    KEY `idx_stocktake_no` (`stocktake_no`),
    KEY `idx_tenant_sku` (`tenant_id`, `sku_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='盘点记录表';

-- -----------------------------------------------------------
-- 对账差异记录表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `reconciliation_diff`;
CREATE TABLE `reconciliation_diff` (
    `id`                     BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`              VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '租户ID',
    `reconciliation_no`      VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '对账批次号',
    `warehouse_code`         VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '仓库编码',
    `sku_code`               VARCHAR(64)     NOT NULL DEFAULT '' COMMENT 'SKU编码',
    `book_quantity`          INT             NOT NULL DEFAULT 0 COMMENT '账面库存',
    `physical_quantity`      INT             NOT NULL DEFAULT 0 COMMENT '实物库存',
    `difference_quantity`    INT             NOT NULL DEFAULT 0 COMMENT '差异数量',
    `difference_type`        TINYINT         NOT NULL DEFAULT 0 COMMENT '差异类型 1漏发 2错发 3损耗 4盈余',
    `related_transaction_no` VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '关联流水号',
    `description`            VARCHAR(512)    NOT NULL DEFAULT '' COMMENT '差异说明',
    `remark`                 VARCHAR(512)    NOT NULL DEFAULT '' COMMENT '备注',
    `create_time`            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`             TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除 0否 1是',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_reconciliation_no` (`reconciliation_no`),
    KEY `idx_tenant_sku` (`tenant_id`, `sku_code`),
    KEY `idx_tenant_create_time` (`tenant_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对账差异记录表';

-- -----------------------------------------------------------
-- 仓库对账规则配置表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `warehouse_reconciliation_rule`;
CREATE TABLE `warehouse_reconciliation_rule` (
    `id`                     BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`              VARCHAR(32)     NOT NULL DEFAULT '' COMMENT '租户ID',
    `warehouse_code`         VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '仓库编码',
    `currency`               VARCHAR(16)     NOT NULL DEFAULT 'CNY' COMMENT '币种',
    `exchange_rate`          DECIMAL(18,6)   NOT NULL DEFAULT 1.000000 COMMENT '对人民币汇率',
    `loss_exempt_quantity`   INT             NOT NULL DEFAULT 0 COMMENT '每SKU损耗豁免件数',
    `loss_exempt_amount`     DECIMAL(18,4)   NOT NULL DEFAULT 0.0000 COMMENT '每SKU损耗豁免金额(原币)',
    `is_exempt_enabled`      TINYINT         NOT NULL DEFAULT 1 COMMENT '是否启用豁免 1启用 0禁用',
    `remark`                 VARCHAR(512)    NOT NULL DEFAULT '' COMMENT '备注',
    `create_time`            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`             TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除 0否 1是',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    UNIQUE KEY `uk_tenant_warehouse` (`tenant_id`, `warehouse_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仓库对账规则配置表';

-- ============================================================
-- 初始化测试数据
-- ============================================================

-- 初始化租户
INSERT INTO `tenant` (`tenant_id`, `tenant_code`, `tenant_name`, `contact_name`, `contact_phone`, `status`)
VALUES
    ('T001', 'SELLER_001', '深圳跨境电商有限公司', '张三', '13800138001', 1),
    ('T002', 'SELLER_002', '广州全球贸易有限公司', '李四', '13800138002', 1);

-- 初始化仓库
INSERT INTO `warehouse` (`tenant_id`, `warehouse_code`, `warehouse_name`, `country`, `province`, `city`, `address`, `status`)
VALUES
    ('T001', 'WH_US_WEST', '美西仓', '美国', 'California', 'Los Angeles', '123 Main St, LA', 1),
    ('T001', 'WH_US_EAST', '美东仓', '美国', 'New York', 'New York', '456 Park Ave, NY', 1),
    ('T001', 'WH_UK', '英国仓', '英国', 'England', 'London', '789 Oxford St, London', 1),
    ('T002', 'WH_DE', '德国仓', '德国', 'Nordrhein-Westfalen', 'Dusseldorf', '101 Berlin Str, DE', 1);

-- 初始化商品
INSERT INTO `product` (`tenant_id`, `sku_code`, `product_name`, `category`, `brand`, `unit`, `cost_price`, `sale_price`, `barcode`)
VALUES
    ('T001', 'SKU001', '无线蓝牙耳机', '电子产品', 'SoundMax', '件', 50.00, 129.99, '690000000001'),
    ('T001', 'SKU002', '智能手表', '电子产品', 'TechWatch', '件', 200.00, 499.99, '690000000002'),
    ('T001', 'SKU003', '便携充电宝20000mAh', '电子产品', 'PowerPro', '件', 30.00, 79.99, '690000000003'),
    ('T002', 'SKU101', '保温杯500ml', '家居用品', 'HotCup', '件', 15.00, 39.99, '690000000101'),
    ('T002', 'SKU102', '瑜伽垫', '运动户外', 'FitPro', '件', 10.00, 29.99, '690000000102');

-- 初始化仓库对账规则
INSERT INTO `warehouse_reconciliation_rule` (`tenant_id`, `warehouse_code`, `currency`, `exchange_rate`, `loss_exempt_quantity`, `loss_exempt_amount`, `is_exempt_enabled`, `remark`)
VALUES
    ('T001', 'WH_US_WEST', 'USD', 7.250000, 5, 50.0000, 1, '美西仓-美元结算，每SKU豁免5件或50美元'),
    ('T001', 'WH_US_EAST', 'USD', 7.250000, 3, 30.0000, 1, '美东仓-美元结算，每SKU豁免3件或30美元'),
    ('T001', 'WH_UK', 'GBP', 9.150000, 2, 20.0000, 1, '英国仓-英镑结算，每SKU豁免2件或20英镑'),
    ('T002', 'WH_DE', 'EUR', 7.850000, 4, 40.0000, 1, '德国仓-欧元结算，每SKU豁免4件或40欧元');
