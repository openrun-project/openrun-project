package com.project.openrun.member.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.extern.apachecommons.CommonsLog;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(unique = true , nullable = false)
    private String memberEmail;

    @Column(unique = false , nullable = false)
    private String memberPassword;

    @Column
    private MemberRoleEnum memberRole;

}
