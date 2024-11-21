package com.example.demo.detectionHistory;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

//import java.awt.print.Pageable;
import java.util.List;

public interface DetectionHistoryRepository extends JpaRepository<Detection, Integer> {
    List<Detection> findByUserId(int id);
//    List<DetectionHistory> findByUser(User user);
//
//    List<DetectionHistory> findByUserId(Integer userId);


//    Page<Detection> findByUserId(Integer userId, Pageable pageable);

}
