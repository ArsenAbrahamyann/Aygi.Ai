package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(fluent = true )
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
//    @Column
//    private String photoName;
    @Column(nullable = false)
    private Integer senderUserId;
    @Column(columnDefinition = "text")
    private String message;
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;
    @Column
    private boolean deleted;
    @Builder.Default
    private Integer likes = 0;

}
