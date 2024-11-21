package com.example.demo.repository;

//
//import com.example.demo.entity.UserProfilePhoto;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//
//import javax.transaction.Transactional;
//import java.util.Optional;
//
//public interface UserProfilePhotoRepository extends JpaRepository<UserProfilePhoto, Integer> {
//    @Override
//    UserProfilePhoto save(UserProfilePhoto u);
//
//    @Override
//    Optional<UserProfilePhoto> findById(Integer integer);
//    Optional<UserProfilePhoto> findByUserId(int userId);
//
//    Optional<UserProfilePhoto> findByPhotoPath(String photoPath);
//    @Transactional
//    @Modifying
//    @Query("UPDATE UserProfilePhoto up SET up.photoPath = :newPhotoPath WHERE up.id = :id")
//    void updatePhotoPathById(Integer id, String newPhotoPath);
//
//}
//


import com.example.demo.entity.UserProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import javax.transaction.Transactional;
import java.util.Optional;


public interface UserProfilePhotoRepository extends JpaRepository<UserProfilePhoto, Integer>
{
    Optional<UserProfilePhoto> findByName(String fileName);
    Optional<UserProfilePhoto> findById(Integer id);
    @Modifying
    @Transactional
    @Query("UPDATE UserProfilePhoto up SET up.imageData = :imageData WHERE up.id = :id")
    void updateImageDataById(Integer id, byte[] imageData);


    @Modifying
    @Transactional
    @Query("DELETE FROM UserProfilePhoto up WHERE up.user.id = :userId")
    void deleteByUserId(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserProfilePhoto up SET up.imageData = :imageData WHERE up.user.id = :userId")
    void updateByUserId(Integer userId, byte[] imageData);


}