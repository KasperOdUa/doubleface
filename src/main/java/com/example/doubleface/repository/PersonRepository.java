package com.example.doubleface.repository;

import com.example.doubleface.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    Person findByEmail(String email);

    Person findByName(String name );

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO friend_request(from_id,to_id) VALUES(:personId,:friendId)", nativeQuery = true)
    void saveFriendRequest(@Param("personId") int personId, @Param("friendId") int friendId);

    @Query(value = "SELECT `from_id` FROM friend_request WHERE `to_id` = :friendId", nativeQuery = true)
    List<Integer> findAllFriendRequests(@Param("friendId") int friendId);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO person_friend(person_id,friend_id) VALUES(:personId,:friend_id)", nativeQuery = true)
    void addFriend(@Param("personId") int personId, @Param("friend_id") int friend_id);

    @Query(value = "SELECT `friend_id` FROM person_friend WHERE `person_id` = :personId", nativeQuery = true)
    List<Integer> findAllFriends(@Param("personId") int personId);

    @Query(value = "SELECT `person_id` FROM person_friend WHERE `friend_id` = :personId", nativeQuery = true)
    List<Integer> findAllFriendsSecond(@Param("personId") int personId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM friend_request WHERE from_id = :personId AND to_id = :friendId", nativeQuery = true)
    void removeRequest(@Param("personId") int personId, @Param("friendId") int friendId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM person_friend WHERE person_id = :personId AND friend_id = :fromId", nativeQuery = true)
    void deleteFriendById(@Param("personId") int id, @Param("fromId") int fromId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM person_friend WHERE friend_id = :personId AND person_id = :fromId", nativeQuery = true)
    void deletePersonFriendById(@Param("personId") int id, @Param("fromId") int fromId);

    Person findByActivationCode(String code);
}