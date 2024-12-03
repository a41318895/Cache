package com.akichou.cache.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;

    @Column(name = "username", unique = true, nullable = false)
    private String username ;

    @Column(name = "age", nullable = false)
    private Integer age ;

    @Column(name = "is_vip", nullable = false)
    private Boolean isVip ;

    protected User() {}

    public static class Builder {

        private String username ;
        private Integer age ;
        private Boolean isVip ;

        public Builder username(String username) {

            this.username = username ;

            return this ;
        }

        public Builder age(Integer age) {

            this.age = age ;

            return this ;
        }

        public Builder isVip(Boolean isVip) {

            this.isVip = isVip ;

            return this ;
        }

        public User build() {

            User user = new User() ;

            user.username = this.username ;
            user.age = this.age ;
            user.isVip = this.isVip ;

            return user ;
        }
    }

    public static Builder builder() {

        return new Builder() ;
    }
}
