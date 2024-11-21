package com.example.demo.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table
public class UserRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JsonBackReference
    private User user1;

    @ManyToOne
    @JsonBackReference
    private User user2;

    private Integer relationshipType;

}
