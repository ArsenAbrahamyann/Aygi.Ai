package com.example.demo.services;

import com.example.demo.entity.ActiveWorks;
import com.example.demo.repository.ActiveWorksRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ActiveWorksService {
    private final ActiveWorksRepository activeWorksRepository;

    public void save(ActiveWorks activeWorks) {
        activeWorksRepository.save(activeWorks);
    }
}
