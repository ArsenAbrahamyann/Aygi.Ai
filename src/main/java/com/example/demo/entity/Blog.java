package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String text;
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne
    private Category category;
    @OneToOne(mappedBy = "blog", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private BlogPhoto blogPhoto;
}