package com.memory.nexigntasks;

import com.memory.nexigntasks.Entities.CDRRecord;
import com.memory.nexigntasks.Entities.CallType;
import com.memory.nexigntasks.Entities.Subscriber;
import com.memory.nexigntasks.Repositories.CDRRecordRepository;
import com.memory.nexigntasks.Repositories.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.memory.nexigntasks.Entities.CDRRecord.prettyDateTime;

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

    // заполнение рандомными данными
    @Override
    public void run(String... args) throws Exception {
        List<Subscriber> subscribers = new ArrayList<>();
        List<CDRRecord> records = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 12; i++) { // итерация по кол-ву абонентов
            subscribers.add(registerRandomSubscriber(subscriberRepository));
        }

        for (Subscriber ignored : subscribers) {
            for (int month = 0; month < 12; month++) {
                int callsThisMonth = 1 + random.nextInt(5);

                for (int j = 0; j < callsThisMonth; j++) {
                    CDRRecord[] pair = generateRandomPairCDRRecords(subscribers, month);
                    Collections.addAll(records, pair);
                }
            }
        }

        records.sort(Comparator.comparing(CDRRecord::getCallStartTime));

        System.out.println("*** Отсортированный вывод дат ***");                  // Логи
        for (CDRRecord cdrRecord : records) {                                     //
            System.out.println(prettyDateTime(cdrRecord.getCallStartTime()));     //
        }                                                                         //

        recordRepository.saveAll(records);
    }

    // Все методы снизу служебные и нужны для создания тестовых данных

    Subscriber registerRandomSubscriber(SubscriberRepository repository) {
        Subscriber subscriber = generateRandomSubscriber();
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
        return now.plusMonths(i).plusSeconds(r.nextInt(2592000));
    }

    /**
     * Выбирает случайный номер из списка абонентов, исключая номер вызывающего.
     * <p>
     * Метод просматривает список {@code subList}, исключает из него абонента с номером {@code callingNumber},
     * и случайным образом возвращает номер одного из оставшихся. Если после фильтрации не остаётся ни одного номера,
     * будет выброшено исключение.
     * </p>
     *
     * @param subList список абонентов, среди которых нужно выбрать номер
     * @param callingNumber номер вызывающего абонента (его исключаем из выбора)
     * @return случайный номер, отличный от {@code callingNumber}
     * @throws IllegalArgumentException если в списке нет подходящих номеров для выбора
     */
    static String getRandomNumber(List<Subscriber> subList, String callingNumber) {
        if (subList == null || subList.isEmpty()) {
            throw new IllegalArgumentException("Список абонентов не должен быть пустым");
        }
        List<String> availableNumbers = subList.stream()
                .map(Subscriber::getNumber)
                .filter(number -> !number.equals(callingNumber))
                .toList();

        if (availableNumbers.isEmpty()) {
            throw new IllegalArgumentException("Нет доступных номеров для выбора");
        }

        Random rand = new Random();
        return availableNumbers.get(rand.nextInt(availableNumbers.size()));
    }

    Subscriber generateRandomSubscriber() {
        return new Subscriber(generateNumber());
    }

    CDRRecord generateRandomCDRRecord(List<Subscriber> subList, int abstractTimeFromNow) {
        Random random = new Random();

        CallType callType;
        String callingNumber = subList.get(random.nextInt(subList.size())).getNumber();
        String receivingNumber = getRandomNumber(subList, callingNumber);
        LocalDateTime callStartTime = randomTimeFromNow(abstractTimeFromNow);
        LocalDateTime callEndTime = callStartTime.plusSeconds(random.nextInt(200) + 10);

        if (random.nextBoolean()) {
            callType = CallType.INCOMING;
        } else {
            callType = CallType.OUTGOING;
        }

        return new CDRRecord(
                callType,
                callingNumber,
                receivingNumber,
                callStartTime,
                callEndTime
        );
    }

    // так как звонок кому-то - это исходящий и входящий звонок, нужен метод создающий почти "зеркальные" записи
    CDRRecord[] generateRandomPairCDRRecords(List<Subscriber> subList, int abstractTimeFromNow) {
        CDRRecord[] cdrRecords = new CDRRecord[2];
        CDRRecord cdrRecord = generateRandomCDRRecord(subList, abstractTimeFromNow);

        cdrRecords[0] = cdrRecord;
        CallType callType = cdrRecord.getCallType();

        if (callType == CallType.INCOMING) {
            callType = CallType.OUTGOING;
        } else {
            callType = CallType.INCOMING;
        }

        cdrRecords[1] = new CDRRecord(
                callType,
                cdrRecord.getReceivingMsId(),
                cdrRecord.getCallingMsId(),
                cdrRecord.getCallStartTime(),
                cdrRecord.getCallEndTime()
        );
        return cdrRecords;
    }
}
