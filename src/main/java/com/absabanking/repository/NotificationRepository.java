package com.absabanking.repository;

import com.absabanking.model.Account;
import com.absabanking.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Notification findNotificationById(long id);
}
