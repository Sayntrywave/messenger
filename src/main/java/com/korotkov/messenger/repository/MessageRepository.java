package com.korotkov.messenger.repository;

import com.korotkov.messenger.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Integer> {
    List<Message> getMessagesByUserToIdAndUserFromId(int userToId,int userFromId);
}
