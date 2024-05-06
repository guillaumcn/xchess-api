package com.xchess.engine.api.mapper;

import com.xchess.engine.api.domain.enumeration.ChessColor;
import com.xchess.engine.api.domain.response.PositionEvaluationResponse;
import com.xchess.evaluation.ChessEngineEvaluation;

public class PositionEvaluationResponseMapper {
    public static PositionEvaluationResponse toPositionEvaluationResponse(ChessEngineEvaluation chessEngineEvaluation) {
        return PositionEvaluationResponse
                .builder()
                .type(chessEngineEvaluation.getType())
                .value(Math.abs(chessEngineEvaluation.getValue()))
                .color(chessEngineEvaluation.getValue() >= 0 ?
                        ChessColor.WHITE : ChessColor.BLACK)
                .build();
    }
}
