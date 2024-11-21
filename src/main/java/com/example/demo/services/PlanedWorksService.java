package com.example.demo.services;

import com.example.demo.entity.Diary;
import com.example.demo.entity.PlannedWorks;
import com.example.demo.exceptions.errors.InvalidIdException;
import com.example.demo.payload.request.AddPlanedWorkRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanedWorksService {
    private final DiaryService diaryService;
    private final ModelMapper mapper;
    @Transactional
    public Diary addWorkInUser(int diaryId, AddPlanedWorkRequest plannedWorks) {
        Diary byId = diaryService.getById(diaryId);
        PlannedWorks map = mapper.map(plannedWorks, PlannedWorks.class);
        map.setDiary(byId);
        byId.getPlannedWorks().add(map);
        return byId;
    }

    @Transactional
    public Diary removePlanedWork(int diaryId, int planedWorId) {
        Diary byId = diaryService.getById(diaryId);
        PlannedWorks plannedWorks = byId.getPlannedWorks().stream()
                .filter(x -> x.getId() == planedWorId)
                .findAny().orElseThrow(InvalidIdException::new);
        byId.getPlannedWorks().remove(plannedWorks);
        return byId;
    }
}
