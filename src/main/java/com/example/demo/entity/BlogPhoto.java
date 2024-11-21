package com.example.demo.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "BlogImageData")
@Data
@ToString(exclude = {"blog"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String type;
    @Lob
    @Column(name = "imagedata")
    private byte[] imageData;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;
}
