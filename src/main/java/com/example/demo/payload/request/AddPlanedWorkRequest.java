package com.example.demo.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPlanedWorkRequest {
        private String name;
        private String description;
        private LocalDateTime makeTime;
        private LocalDateTime createdDate;
        @NonNull
        private Integer diaryId;
}
