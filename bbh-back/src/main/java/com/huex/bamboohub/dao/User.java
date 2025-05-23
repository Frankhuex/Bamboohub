package com.huex.bamboohub.dao;


import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

@Getter @Setter
@Entity
@Table(name="user")
public class User {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy=IDENTITY)
    private Long id;

    @Column(name="create_time")
    @CreationTimestamp
    private Date createTime;

    @Column(name="username",nullable=false,unique=true)
    private String username;

    @Column(name="password",nullable=false)
    private String password;

    @Column(name="nickname")
    private String nickname;

    @OneToMany(mappedBy="user",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<Role> roles=new ArrayList<>();

    @OneToMany(mappedBy="user",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<ParaRole> paraRoles=new ArrayList<>();

    @OneToMany(mappedBy="target",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<Follow> follows=new ArrayList<>();

    @OneToMany(mappedBy="source",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<Follow> sources=new ArrayList<>();


    public User() {}
    public User(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }


}
