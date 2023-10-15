package com.korotkov.messenger.repository;

import com.korotkov.messenger.model.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {
    Optional<FriendRequest> findFriendRequestByUserFromIdAndUserToId(int userFromId, int userToId);

    List<FriendRequest> findFriendRequestsByUserToId(int userTo_id);
}
