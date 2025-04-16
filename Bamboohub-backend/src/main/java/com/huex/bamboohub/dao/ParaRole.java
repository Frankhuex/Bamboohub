package com.huex.bamboohub.dao;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="para_role")
public class ParaRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;  // 主键

    @Column(name="create_time")
    @CreationTimestamp
    private Date createTime;

    @ManyToOne
    @JoinColumn(name = "para_id", referencedColumnName = "id", nullable = false)
    private Paragraph paragraph;  // 关联到 Paragraph 实体

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;  // 关联到 User 实体

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private RoleType roleType;  // 角色类型

    public enum RoleType {
        CREATOR,
        CONTRIBUTOR
    }

    public ParaRole() {}
    public ParaRole(User user, Paragraph paragraph, RoleType roleType) {
        this.user = user;
        this.paragraph = paragraph;
        this.roleType = roleType;
    }
}
