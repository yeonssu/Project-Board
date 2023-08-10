package com.preonboarding.comment;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class CommentDto {

    @Getter
    public static class Post {

        @NotBlank
        private String content;
    }

    @Getter
    public static class Patch extends Post {
    }

    @Getter
    public static class Response {

        private final Long id;

        private final String content;

        private final String nickname;

        private final LocalDateTime createdAt;

        private final LocalDateTime updatedAt;

        @Builder
        public Response(Long id, String content, String nickname, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.content = content;
            this.nickname = nickname;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
    }
}