create TABLE IF NOT EXISTS `TBL_TODO`(
    `id`        INTEGER PRIMARY KEY,
    `title`     VARCHAR(100) NOT NULL,
    `body`      VARCHAR(2000) NOT NULL
);