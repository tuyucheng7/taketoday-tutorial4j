package cn.tuyucheng.taketoday.service.impl;

import cn.tuyucheng.taketoday.service.CustomerIdGenerator;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CustomerIdGeneratorImpl implements CustomerIdGenerator {
    private static final AtomicInteger SEQUENCE = new AtomicInteger();

    @Override
    public int generateNextId() {
        return SEQUENCE.incrementAndGet();
    }
}