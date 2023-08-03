package com.project.openrun.member.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String memberName;

    @Column(unique = true , nullable = false)
    private String memberEmail;

    @Column(unique = false , nullable = false)
    private String memberPassword;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRoleEnum memberRole;

}
