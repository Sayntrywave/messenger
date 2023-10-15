package com.korotkov.messenger.repository;

import com.korotkov.messenger.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {

    List<Friend> getFriendsByFirstUserIdAndSecondUserId(int firstUserId, int secondUserId);

    List<Friend> getFriendsByFirstUserId(int firstUserId);

    List<Friend> getFriendsBySecondUserId(int secondUserId);
}
