package com.alex;


import com.alex.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


@SpringBootApplication
public class Application implements CommandLineRunner {
    public static final String LOGGED_MSG_LOGGER = "LOGGED_MSG_LOGGER";

    private static final int COUNT_OF_MSG = 1000;

    public static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(COUNT_OF_MSG);

    @Autowired
    private LogService logService;


    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(Application.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        Stream.generate(new AtomicInteger()::getAndIncrement).limit(COUNT_OF_MSG).forEach(i -> logService.log("test", "test: " + i));
        COUNT_DOWN_LATCH.await();
    }
}