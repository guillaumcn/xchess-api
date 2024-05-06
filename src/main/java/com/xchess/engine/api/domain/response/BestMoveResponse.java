package com.xchess.engine.api.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class BestMoveResponse {
    private String bestMove;
}
