package com.project.openrun.member.entity;

public enum MemberRoleEnum {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private String authority;

    MemberRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
