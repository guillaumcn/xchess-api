package com.xchess.engine.api.service;

import com.xchess.ChessEngine;
import com.xchess.engine.api.exceptions.ChessEngineWorkerExecutionException;
import com.xchess.engine.api.pool.PoolWrapper;
import com.xchess.evaluation.ChessEngineEvaluation;
import com.xchess.evaluation.parameter.EvaluationParameters;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
public class ChessService {
    private final PoolWrapper poolWrapper;

    public Float getEngineVersion() throws Exception {
        return this.poolWrapper.queueAction(ChessEngine::getEngineVersion);
    }

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
