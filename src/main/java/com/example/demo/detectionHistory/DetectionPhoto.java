package com.example.demo.detectionHistory;



import com.example.demo.entity.Diary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "DetectionImageData")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetectionPhoto
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String type;
    @Lob
    @Column(name = "imagedata")
    private byte[] imageData;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detection_id")
    private Detection detection;

}