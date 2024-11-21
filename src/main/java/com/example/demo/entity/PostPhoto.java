package com.example.demo.entity;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;


@Entity
@Table(name = "PostImageData")
@Data
@ToString(exclude = {"blog"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostPhoto {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String type;
    @Lob
    @Column(name = "imagedata")
    private byte[] imageData;
    @JsonIgnore
    private Long userId;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

}
