package com.huex.bamboohub.dao;


import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="user")
public class User {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy=IDENTITY)
    private Long id;

    @Column(name="username",nullable=false,unique=true)
    private String username;

    @Column(name="password",nullable=false)
    private String password;

    @Column(name="nickname")
    private String nickname;

    @OneToMany(mappedBy="user",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<Role> roles=new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    

}
