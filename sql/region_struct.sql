CREATE TABLE IF NOT EXISTS `region_mca`
(
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '唯一编号',
    code        VARCHAR(6)                         NOT NULL COMMENT '区划代码',
    name        VARCHAR(20)                        NOT NULL COMMENT '区划名称',
    level       ENUM ('省级', '地级', '县级')      NOT NULL COMMENT '区划级别，枚举值：省级、地级、县级',
    parent_code VARCHAR(6)                         NULL COMMENT '父级区划代码',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time DATETIME                           NULL COMMENT '更新时间',
    delete_time DATETIME                           NULL COMMENT '删除时间',
    CONSTRAINT region_mca_code_uindex UNIQUE (code)
)
    COMMENT '区域（国家民政部版本），参阅国家标准号GB/T2260-2007' CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `region_stats`
(
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '唯一编号',
    code        VARCHAR(12)                                   NOT NULL COMMENT '区划代码，只是精简的，不包括补齐的零',
    full_code   VARCHAR(12)                                   NULL COMMENT '完整的区划代码，省级代码+地级代码+县级代码+乡级代码+村级代码，不足则在末尾补零',
    name        VARCHAR(20)                                   NOT NULL COMMENT '区划名称',
    level       ENUM ('省级', '地级', '县级', '乡级', '村级') NOT NULL COMMENT '区划级别，枚举值：省级、地级、县级、乡级、村级',
    parent_code VARCHAR(12)                                   NULL COMMENT '父级区划代码，只是精简的，不包括补齐的零',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP            NOT NULL COMMENT '创建时间',
    update_time DATETIME                                      NULL COMMENT '更新时间',
    delete_time DATETIME                                      NULL COMMENT '删除时间',
    CONSTRAINT region_stats_code_uindex UNIQUE (code),
    CONSTRAINT region_stats_full_code_uindex UNIQUE (full_code)
)
    COMMENT '区域（国家统计局版本），参阅国家标准号GB/T2260-2007' CHARSET = utf8;
