package cn.tuyucheng.taketoday.util;

import org.testcontainers.containers.PostgreSQLContainer;

public class TuyuchengPostgresqlContainer extends PostgreSQLContainer<TuyuchengPostgresqlContainer> {

    private static final String IMAGE_VERSION = "postgres:11.1";

    private static TuyuchengPostgresqlContainer container;


    private TuyuchengPostgresqlContainer() {
        super(IMAGE_VERSION);
    }

    public static TuyuchengPostgresqlContainer getInstance() {
        if (container == null) {
            container = new TuyuchengPostgresqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        // do nothing, JVM handles shut down
    }
}