package com.preonboarding.member;

import lombok.Builder;
import lombok.Getter;

public class MemberDto {

    @Getter
    public static class Signup {

        private String email;

        private String password;

        private String nickname;
    }

    @Getter
    public static class Response {

        private final Long id;

        private final String email;

        private final String nickname;

        @Builder
        public Response(Long id, String email, String nickname) {
            this.id = id;
            this.email = email;
            this.nickname = nickname;
        }

        public Response(Member member) {
            this.id = member.getId();
            this.email = member.getEmail();
            this.nickname = member.getNickname();
        }
    }

    @Getter
    public static class LoginDto {

        private String email;

        private String password;
    }
}
