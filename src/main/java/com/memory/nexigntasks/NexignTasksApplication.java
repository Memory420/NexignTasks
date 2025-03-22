package com.memory.nexigntasks;

import com.memory.nexigntasks.Entities.CDRRecord;
import com.memory.nexigntasks.Entities.Subscriber;
import com.memory.nexigntasks.Repositories.CDRRecordRepository;
import com.memory.nexigntasks.Repositories.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class NexignTasksApplication implements CommandLineRunner {

    final private CDRRecordRepository recordRepository;
    final private SubscriberRepository subscriberRepository;

    @Autowired
    public NexignTasksApplication(CDRRecordRepository repository, SubscriberRepository subscriberRepository) {
        this.recordRepository = repository;
        this.subscriberRepository = subscriberRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(NexignTasksApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<Subscriber> subscribers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            subscribers.add(registerRandSubscriber(subscriberRepository));
        }

        for (int j = 0; j < subscribers.size(); j++) {
            registerRandCDRRecord(recordRepository, subscribers, j);

        }
    }

    @RestController
    public static class TestController {
        @GetMapping("/test")
        public String test() {
            return "test";
        }
    }
    public void registerRandCDRRecord(CDRRecordRepository repository, List<Subscriber> subList, int abstractTimeFromNow) {
        Random random = new Random();

        String callType;
        String callingNumber = subList.get(random.nextInt(subList.size())).getNumber();
        String receivingNumber = getRandomNumber(subList, callingNumber);
        LocalDateTime callStartTime = randomTimeFromNow(abstractTimeFromNow);
        LocalDateTime callEndTime = callStartTime.plusSeconds(random.nextInt(200) + 10);

        if (random.nextBoolean()) {
            callType = "01";
        } else {
            callType = "02";
        }

        CDRRecord record = new CDRRecord(
                callType,
                callingNumber,
                receivingNumber,
                callStartTime,
                callEndTime
        );
        repository.save(record);
    }

    Subscriber registerRandSubscriber(SubscriberRepository repository) {
        Subscriber subscriber = new Subscriber(generateNumber());
        repository.save(subscriber);
        return subscriber;
    }

    static String generateNumber() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder("79");
        for (int i = 0; i < 9; i++) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }
    static LocalDateTime randomTimeFromNow(int i){
        Random r = new Random();
        LocalDateTime now = LocalDateTime.now();
        return now.plusMonths(i).
                plusDays(r.nextInt(30)).
                plusHours(r.nextInt(24)).
                plusMinutes(r.nextInt(60)).
                plusSeconds(r.nextInt(60));
    }
    static String getRandomNumber(List<Subscriber> subList, String callingNumber) {
        Random rand = new Random();
        String selectedNumber;

        do {
            int randomIndex = rand.nextInt(subList.size());
            selectedNumber = subList.get(randomIndex).getNumber();
        } while (selectedNumber.equals(callingNumber));

        return selectedNumber;
    }
}
