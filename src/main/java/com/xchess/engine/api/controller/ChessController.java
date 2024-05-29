package com.xchess.engine.api.controller;

import com.xchess.engine.api.domain.request.MoveRequest;
import com.xchess.engine.api.domain.response.*;
import com.xchess.engine.api.service.ChessService;
import com.xchess.evaluation.parameter.EvaluationParameters;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ChessController {

    private final ChessService chessService;

    @GetMapping(value = "/engineVersion")
    public EngineVersionResponse getEngineVersion() throws Exception {
        return chessService.getEngineVersion();
    }

    @GetMapping(value = "/possibleMoves")
    public PossibleMovesResponse getPossibleMoves(@RequestParam(required =
            false) String fen, @RequestParam(required = false) String square) throws Exception {
        return chessService.getPossibleMoves(fen, square);
    }

    @GetMapping(value = "/bestMove")
    public BestMoveResponse findBestMove(@RequestParam(required = false) String fen,
                                         EvaluationParameters evaluationParameters) throws Exception {
        return chessService.findBestMove(fen, evaluationParameters);
    }

    @GetMapping(value = "/positionEvaluation")
    public PositionEvaluationResponse getPositionEvaluation(@RequestParam(required = false) String fen, EvaluationParameters evaluationParameters) throws Exception {
        return chessService.getPositionEvaluation(fen,
                evaluationParameters);
    }

    @PostMapping(value = "/move")
    public MoveResponse move(@RequestBody(required = false) MoveRequest moveRequest) throws Exception {
        return chessService.move(moveRequest.getFen(), moveRequest.getMoves());
    }
}
