CREATE TABLE IF NOT EXISTS `api_source`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `gmt_create`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `host`         VARCHAR(512) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `opt_user`
(
    `id`                  BIGINT                                  NOT NULL AUTO_INCREMENT,
    `gmt_create`          DATETIME                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`        DATETIME                                NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `username`            VARCHAR(255) COLLATE utf8mb4_general_ci NOT NULL,
    `password`            VARCHAR(512) COLLATE utf8mb4_general_ci NOT NULL,
    `last_try_login_time` DATETIME                                NOT NULL DEFAULT '1970-01-01 12:00:00',
    `login_fail_count`    INT                                     NOT NULL DEFAULT '0',
    `login_locked`        TINYINT(1)                              NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_username` (`username`(64))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

INSERT INTO `opt_user`(`id`, `gmt_create`, `gmt_modified`, `username`, `password`, `last_try_login_time`,
                       `login_fail_count`, `login_locked`)
VALUES (1, now(), now(), 'admin', '$2a$10$73v3GVJGdiKquX3XDVxOqe2yw.XWHp1QutrKyjtRyV/tW.z.9caXS',
        '1970-01-01 12:00:00', 0, 0);

CREATE TABLE `async_job`
(
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `gmt_create`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `job_name`         VARCHAR(255) NOT NULL,
    `job_desc`         VARCHAR(512) NOT NULL DEFAULT '',
    `job_status`       VARCHAR(64)  NOT NULL,
    `life_cycle_state` VARCHAR(64)  NOT NULL,
    `start_time`       DATETIME              DEFAULT NULL,
    `finish_time`      DATETIME              DEFAULT NULL,
    `next_start_time`  DATETIME              DEFAULT NULL,
    `is_timing`        BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE `async_task`
(
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `gmt_create`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `task_name`        VARCHAR(255) NOT NULL,
    `task_desc`        VARCHAR(255)          DEFAULT NULL,
    `task_status`      VARCHAR(512) NOT NULL,
    `life_cycle_state` VARCHAR(64)  NOT NULL,
    `job_id`           BIGINT       NOT NULL,
    `exec_order`       BIGINT       NOT NULL,
    `error_msg`        LONGTEXT              DEFAULT NULL,
    `task_result`      LONGTEXT              DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE `kv_base_config`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `gmt_create`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `task_id`       BIGINT       NOT NULL,
    `config_name`   VARCHAR(64)  NOT NULL,
    `config_value`  LONGTEXT              DEFAULT NULL,
    `default_value` LONGTEXT              DEFAULT NULL,
    `value_range`   LONGTEXT              DEFAULT NULL,
    `desc_key`      VARCHAR(512) NOT NULL COMMENT 'config description i18n key',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE `api_kv_base_config`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `gmt_create`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `api_source_id` BIGINT       NOT NULL,
    `config_name`   VARCHAR(64)  NOT NULL,
    `config_value`  LONGTEXT              DEFAULT NULL,
    `default_value` LONGTEXT              DEFAULT NULL,
    `value_range`   LONGTEXT              DEFAULT NULL,
    `desc_key`      VARCHAR(512) NOT NULL COMMENT 'config description i18n key',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;
