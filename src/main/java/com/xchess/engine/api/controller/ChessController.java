package com.xchess.engine.api.controller;

import com.xchess.ChessEngine;
import com.xchess.engine.api.exceptions.ChessEngineWorkerExecutionException;
import com.xchess.engine.api.pool.PoolWrapper;
import com.xchess.evaluation.ChessEngineEvaluation;
import com.xchess.evaluation.parameter.EvaluationParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@RestController
public class ChessController {

    private final PoolWrapper poolWrapper;

    @Autowired
    public ChessController(PoolWrapper poolWrapper) {
        this.poolWrapper = poolWrapper;
    }

    @GetMapping(value = "/engineVersion")
    public Float getEngineVersion() throws Exception {
        return this.poolWrapper.queueAction(ChessEngine::getEngineVersion);
    }

    @GetMapping(value = "/possibleMoves")
    public List<String> getPossibleMoves(@RequestParam(required = false) String fen,
                                         @RequestParam(required = false) String square) throws Exception {
        return this.poolWrapper.queueAction(engineWorker -> {
            try {
                moveToFenPositionIfDefined(engineWorker, fen);
                if (Objects.isNull(square)) {
                    return engineWorker.getPossibleMoves();
                } else {
                    return engineWorker.getPossibleMoves(square);
                }
            } catch (IOException | TimeoutException e) {
                throw new ChessEngineWorkerExecutionException(e);
            }

        });
    }

    @GetMapping(value = "/bestMove")
    public String findBestMove(@RequestParam(required = false) String fen,
                               EvaluationParameters evaluationParameters) throws Exception {
        return this.poolWrapper.queueAction(engineWorker -> {
            try {
                moveToFenPositionIfDefined(engineWorker, fen);
                return engineWorker.findBestMove(evaluationParameters);
            } catch (IOException | TimeoutException e) {
                throw new ChessEngineWorkerExecutionException(e);
            }

        });
    }

    @GetMapping(value = "/positionEvaluation")
    public ChessEngineEvaluation getPositionEvaluation(@RequestParam(required = false) String fen, EvaluationParameters evaluationParameters) throws Exception {
        return this.poolWrapper.queueAction(engineWorker -> {
            try {
                moveToFenPositionIfDefined(engineWorker, fen);
                return engineWorker.getPositionEvaluation(evaluationParameters);
            } catch (IOException | TimeoutException e) {
                throw new ChessEngineWorkerExecutionException(e);
            }

        });
    }

    private static void moveToFenPositionIfDefined(ChessEngine engineWorker,
                                                   String fen) throws IOException, TimeoutException {
        if (!Objects.isNull(fen)) {
            engineWorker.moveToFenPosition(fen, true);
        } else {
            engineWorker.moveToStartPosition(true);
        }
    }
}
