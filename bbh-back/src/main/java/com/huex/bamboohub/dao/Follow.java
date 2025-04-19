package com.huex.bamboohub.dao;

import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.IDENTITY;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

@Getter @Setter
@Entity
@Table(name="follow")
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;  // 主键

    @Column(name="create_time")
    @CreationTimestamp
    private Date createTime;

    @ManyToOne
    @JoinColumn(name = "source_id", referencedColumnName = "id", nullable = false)
    private User source;  // 关联到 Book 实体

    @ManyToOne
    @JoinColumn(name = "target_id", referencedColumnName = "id", nullable = false)
    private User target;  // 关联到 User 实体


    public Follow() {}
    public Follow(User source, User target) {
        this.source = source;
        this.target = target;
    }
}
