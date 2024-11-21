package com.example.demo.repository;

import com.example.demo.entity.ActiveWorks;
import com.example.demo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface ActiveWorksRepository extends JpaRepository<ActiveWorks , Integer> {
    List<ActiveWorks> findByIdIn(@NotNull List<Integer> activeWorksIds);

}
