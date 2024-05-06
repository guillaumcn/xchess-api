package com.xchess.engine.api.controller;

import com.xchess.engine.api.domain.response.BestMoveResponse;
import com.xchess.engine.api.domain.response.EngineVersionResponse;
import com.xchess.engine.api.domain.response.PositionEvaluationResponse;
import com.xchess.engine.api.domain.response.PossibleMovesResponse;
import com.xchess.engine.api.service.ChessService;
import com.xchess.evaluation.parameter.EvaluationParameters;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ChessController {

    private final ChessService chessService;

    @GetMapping(value = "/engineVersion")
    public EngineVersionResponse getEngineVersion() throws Exception {
        return this.chessService.getEngineVersion();
    }

    @GetMapping(value = "/possibleMoves")
    public PossibleMovesResponse getPossibleMoves(@RequestParam(required =
            false) String fen, @RequestParam(required = false) String square) throws Exception {
        return this.chessService.getPossibleMoves(fen, square);
    }

    @GetMapping(value = "/bestMove")
    public BestMoveResponse findBestMove(@RequestParam(required = false) String fen,
                                         EvaluationParameters evaluationParameters) throws Exception {
        return this.chessService.findBestMove(fen, evaluationParameters);
    }

    @GetMapping(value = "/positionEvaluation")
    public PositionEvaluationResponse getPositionEvaluation(@RequestParam(required = false) String fen, EvaluationParameters evaluationParameters) throws Exception {
        return this.chessService.getPositionEvaluation(fen,
                evaluationParameters);
    }
}
