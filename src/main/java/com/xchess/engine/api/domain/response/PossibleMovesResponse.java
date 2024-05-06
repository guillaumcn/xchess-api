package com.xchess.engine.api.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class PossibleMovesResponse {
    private List<String> possibleMoves;
}
