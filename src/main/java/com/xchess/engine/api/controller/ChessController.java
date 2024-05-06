package com.xchess.engine.api.controller;

import com.xchess.engine.api.service.ChessService;
import com.xchess.evaluation.ChessEngineEvaluation;
import com.xchess.evaluation.parameter.EvaluationParameters;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class ChessController {

    private final ChessService chessService;

    @GetMapping(value = "/engineVersion")
    public Float getEngineVersion() throws Exception {
        return this.chessService.getEngineVersion();
    }

    @GetMapping(value = "/possibleMoves")
    public List<String> getPossibleMoves(@RequestParam(required = false) String fen,
                                         @RequestParam(required = false) String square) throws Exception {
        return this.chessService.getPossibleMoves(fen, square);
    }

    @GetMapping(value = "/bestMove")
    public String findBestMove(@RequestParam(required = false) String fen,
                               EvaluationParameters evaluationParameters) throws Exception {
        return this.chessService.findBestMove(fen, evaluationParameters);
    }

    @GetMapping(value = "/positionEvaluation")
    public ChessEngineEvaluation getPositionEvaluation(@RequestParam(required = false) String fen, EvaluationParameters evaluationParameters) throws Exception {
        return this.chessService.getPositionEvaluation(fen,
                evaluationParameters);
    }
}
