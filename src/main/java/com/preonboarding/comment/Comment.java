package com.preonboarding.comment;

import com.preonboarding.global.audit.Timestamp;
import com.preonboarding.board.Board;
import com.preonboarding.member.Member;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Comment extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}