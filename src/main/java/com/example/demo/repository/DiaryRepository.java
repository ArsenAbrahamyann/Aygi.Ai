package com.example.demo.repository;

import com.example.demo.dto.DiaryDTO;
import com.example.demo.entity.Diary;
import com.example.demo.entity.DiaryPhoto;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {
    @Override
    Diary save(Diary s);

    @Override
//    @EntityGraph(value = "Diary.posts",type = EntityGraph.EntityGraphType.FETCH)
    Optional<Diary> findById(Integer integer);

    Optional<Diary> findByName(String name);

    @Override
    boolean existsById(Integer integer);
    List<Diary> findAllByUser(User user);
    List<DiaryPhoto> findAllByUser(Principal principal);

    @Transactional
    @Modifying
    @Query("UPDATE Diary d SET d.name = :newName, d.about = :newAbout  WHERE d.id = :id")
    void updateDiaryById(Integer id, String newName, String newAbout);

    @Transactional
    @Modifying
    @Query("UPDATE Diary d SET d.name = :newName WHERE d.id = :id")
    void updateDiaryNameById(Integer id, String newName);

    @Transactional
    @Modifying
    @Query("UPDATE Diary d SET d.about = :newAbout  WHERE d.id = :id")
    void updateDiaryAboutById(Integer id, String newAbout);



    @Transactional
    @Modifying
    @Query("UPDATE Diary d SET d.isPublic = :newIsPublic  WHERE d.id = :id")
    void updateDiaryIsPublicById(Integer id, Boolean newIsPublic);



}
