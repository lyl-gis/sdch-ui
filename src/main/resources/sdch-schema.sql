CREATE TABLE IF NOT EXISTS `tb_category`
(
  `ID`          varchar(36),
  `P_ID`        varchar(36)  DEFAULT '',
  `FUNC`        varchar(255) DEFAULT '',
  `DESCRIPTION` varchar(255) DEFAULT '',
  PRIMARY KEY (`ID`)
);
CREATE TABLE IF NOT EXISTS `tb_index`
(
  `INDICE`      varchar(50),
  `SHARDS`      integer,
  `REPLICAS`    integer,
  `DESCRIPTION` varchar(255) DEFAULT '',
  `CATEGORY`    varchar(50)  DEFAULT ''
    COMMENT '数据类别描述，主要用于区分框架数据和专题数据',
  `CREATE_TIME` timestamp,
  PRIMARY KEY (`INDICE`)
);
CREATE TABLE IF NOT EXISTS `tb_index_type`
(
  `ID`          varchar(36),
  `INDICE`      varchar(50),
  `DTYPE`       varchar(50),
  `GEO_TYPE`    varchar(50),
  `DESCRIPTION` varchar(255) DEFAULT '',
  `CATEGORY`    varchar(50)  DEFAULT ''
    COMMENT '数据类别描述，用于区分统一索引下的多类数据，如框架数据中的POI、道路、政区等',
  `UPDATE_TIME` timestamp,
  PRIMARY KEY (`ID`),
  FOREIGN KEY (`INDICE`) REFERENCES `tb_index` (`INDICE`)
);
CREATE TABLE IF NOT EXISTS `tb_index_mapping`
(
  `ID`          varchar(36),
  `INDICE`      varchar(50),
  `FIELD_NAME`  varchar(50) NOT NULL,
  `FIELD_TYPE`  varchar(50) NOT NULL DEFAULT '',
  `BOOST`       int                  DEFAULT 1,
  `ANALYZABLE`  bool,
  `DESCRIPTION` varchar(255)         DEFAULT '',
  `CREATE_TIME` timestamp,
  PRIMARY KEY (`ID`),
  FOREIGN KEY (`INDICE`) REFERENCES `tb_index` (`INDICE`)
);
CREATE TABLE IF NOT EXISTS `tb_admin_area`
(
  `CODE`         varchar(12),
  `P_CODE`       varchar(12)  NOT NULL DEFAULT '',
  `NAME`         varchar(100) NOT NULL,
  `ABBREVIATION` varchar(100),
  `FULL_NAME`    varchar(255),
  `LON`          decimal(12, 9),
  `LAT`          decimal(12, 9),
  `WKT`          longtext,
  PRIMARY KEY (`CODE`)
);
CREATE TABLE IF NOT EXISTS `tb_entity_type`
(
  `CODE`   varchar(6),
  `P_CODE` varchar(6)  NOT NULL DEFAULT '',
  `NAME`   varchar(50) NOT NULL,
  PRIMARY KEY (`CODE`)
);
CREATE TABLE IF NOT EXISTS `tb_poi_type`
(
  `CODE`   varchar(6),
  `P_CODE` varchar(6)  NOT NULL DEFAULT '',
  `CODE4`  varchar(4),
  `NAME`   varchar(50) NOT NULL,
  PRIMARY KEY (`CODE`)
);