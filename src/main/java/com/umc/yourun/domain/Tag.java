package com.umc.yourun.domain;

import com.umc.yourun.domain.enums.TagContent;
import jakarta.persistence.*;

public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TagContent content;
}
