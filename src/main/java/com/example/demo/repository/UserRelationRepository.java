package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRelationRepository extends JpaRepository<UserRelation, Integer> {
    Optional<Set<UserRelation>> findByUser1Id(int user1Id);
    Optional<Set<UserRelation>> findByRelationshipTypeAndUser1Id(int relationshipType, int user1Id);
}