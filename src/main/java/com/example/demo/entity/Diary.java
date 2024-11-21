package com.example.demo.entity;



import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    @Column(length = 500)
    private String about;
    private boolean isPublic;

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "diary", orphanRemoval = true)
    @ToString.Exclude
    private List<PlannedWorks> plannedWorks;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "diary", orphanRemoval = true)
    @ToString.Exclude
    private List<Post> posts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToOne(mappedBy = "diary", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private DiaryPhoto diaryPhoto;

    public Diary(Diary d) {
        this.id = d.id;
        this.name = d.name;
        this.about = d.about;
        this.isPublic = d.isPublic;
        this.createdDate = d.createdDate;
        this.plannedWorks = d.plannedWorks;
        this.posts = d.posts;
        this.user = d.user;
    }

}
