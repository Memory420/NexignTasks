package com.memory.nexigntasks.Repositories;

import com.memory.nexigntasks.Entities.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для CRUD-операций над абонентами.
 */
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
}
