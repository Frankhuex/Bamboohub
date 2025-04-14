package com.huex.bamboohub.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.IDENTITY;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

@Getter @Setter
@Entity
@Table(name="role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;  // 主键

    @Column(name="create_time")
    @CreatedDate
    private Date createTime;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
    private Book book;  // 关联到 Book 实体

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;  // 关联到 User 实体

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private RoleType roleType;  // 角色类型

    public enum RoleType {
        VIEWER,
        EDITOR,
        ADMIN,
        OWNER
    }

    public Role() {}
    public Role(User user, Book book, RoleType roleType) {
        this.user = user;
        this.book = book;
        this.roleType = roleType;
    }
}
