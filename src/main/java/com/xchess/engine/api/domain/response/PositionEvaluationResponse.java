package com.xchess.engine.api.domain.response;

import com.xchess.engine.api.domain.enumeration.ChessColor;
import com.xchess.evaluation.ChessEngineEvaluationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class PositionEvaluationResponse {
    private ChessEngineEvaluationType type;
    private ChessColor color;
    private int value;
}
