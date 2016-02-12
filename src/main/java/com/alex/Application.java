package com.alex;


import com.alex.service.ElasticsearchBulkService;
import com.alex.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.stream.IntStream;


@SpringBootApplication
public class Application implements CommandLineRunner {
    public static final String LOGGED_MSG_LOGGER = "LOGGED_MSG_LOGGER";

    private static final int DEF_COUNT_OF_MSG = 1000;

    @Autowired
    private LogService logService;

    @Autowired
    private ElasticsearchBulkService bulkService;


    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        int count = countOfMsg(args);
        System.out.println("Start app, with count of msg: " + count);
        IntStream.range(0, count).forEach(this::log);
        while (bulkService.counter.get() >= count) ;
        System.out.println("End of processing.");
    }

    private int countOfMsg(String[] args) {
        return args.length > 0 ? Integer.valueOf(args[0]) : DEF_COUNT_OF_MSG;
    }

    private void log(int number) {
        logService.log(category(number), "number: " + number);
    }

    private String category(int number) {
        return number % 2 == 0 ? "even" : "odd";
    }
}