package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private PostPhoto postPhoto;
    @Column(length = 3000)
    private String about;
    @Builder.Default
    private Integer likes = 0;
    @Column(name = "deleted")
    private boolean deleted;


    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Diary diary;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;
    @OneToMany(fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ActiveWorks> activeWorks;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.MERGE
            })
    @JoinTable(
            name = "post_likedUser",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> likedUser ;

    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "post", orphanRemoval = true)
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    private boolean isPublic;

}
