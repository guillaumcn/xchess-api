package com.xchess.engine.api.service;

import com.xchess.ChessEngine;
import com.xchess.engine.api.domain.response.*;
import com.xchess.engine.api.exceptions.ChessEngineWorkerExecutionException;
import com.xchess.engine.api.exceptions.InvalidMoveException;
import com.xchess.engine.api.exceptions.InvalidSyntaxException;
import com.xchess.engine.api.mapper.PositionEvaluationResponseMapper;
import com.xchess.engine.api.pool.PoolWrapper;
import com.xchess.evaluation.parameter.EvaluationParameters;
import com.xchess.exceptions.IllegalMoveException;
import com.xchess.exceptions.InvalidFenPositionException;
import com.xchess.exceptions.InvalidMoveSyntaxException;
import com.xchess.exceptions.InvalidSquareSyntaxException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
public class ChessService {
    private final PoolWrapper poolWrapper;

    public EngineVersionResponse getEngineVersion() throws Exception {
        return EngineVersionResponse
                .builder()
                .version(poolWrapper.queueAction(ChessEngine::getEngineVersion))
                .build();
    }

    public PossibleMovesResponse getPossibleMoves(String fen, String square) throws Exception {
        return poolWrapper.queueAction(engineWorker -> {
            try {
                moveToFenPositionIfDefined(engineWorker, fen);
                List<String> possibleMoves;
                if (Objects.isNull(square)) {
                    possibleMoves = engineWorker.getPossibleMoves();
                } else {
                    possibleMoves = engineWorker.getPossibleMoves(square);
                }
                return PossibleMovesResponse
                        .builder()
                        .possibleMoves(possibleMoves)
                        .build();
            } catch (IOException | TimeoutException exception) {
                throw new ChessEngineWorkerExecutionException(exception);
            } catch (
                    InvalidSquareSyntaxException invalidSquareSyntaxException) {
                throw new InvalidSyntaxException(invalidSquareSyntaxException);
            }
        });
    }

    public BestMoveResponse findBestMove(String fen,
                                         EvaluationParameters evaluationParameters) throws Exception {
        return poolWrapper.queueAction(engineWorker -> {
            try {
                moveToFenPositionIfDefined(engineWorker, fen);
                return BestMoveResponse
                        .builder()
                        .bestMove(engineWorker.findBestMove(evaluationParameters))
                        .build();
            } catch (IOException | TimeoutException e) {
                throw new ChessEngineWorkerExecutionException(e);
            }

        });
    }

    public PositionEvaluationResponse getPositionEvaluation(String fen,
                                                            EvaluationParameters evaluationParameters) throws Exception {
        return poolWrapper.queueAction(engineWorker -> {
            try {
                moveToFenPositionIfDefined(engineWorker, fen);
                return PositionEvaluationResponseMapper.toPositionEvaluationResponse(engineWorker.getPositionEvaluation(evaluationParameters));
            } catch (IOException | TimeoutException e) {
                throw new ChessEngineWorkerExecutionException(e);
            }

        });
    }

    public MoveResponse move(String fen, List<String> moves) throws Exception {
        return poolWrapper.queueAction(engineWorker -> {
            try {
                moveToFenPositionIfDefined(engineWorker, fen);
                engineWorker.move(moves);
                return MoveResponse.builder()
                        .resultFen(engineWorker.getFenPosition())
                        .build();
            } catch (IOException | TimeoutException e) {
                throw new ChessEngineWorkerExecutionException(e);
            } catch (IllegalMoveException | InvalidFenPositionException e) {
                throw new InvalidMoveException(e);
            } catch (InvalidMoveSyntaxException e) {
                throw new InvalidSyntaxException(e);
            }
        });
    }

    private static void moveToFenPositionIfDefined(ChessEngine engineWorker,
                                                   String fen) throws IOException, TimeoutException {
        try {
            if (!Objects.isNull(fen)) {
                engineWorker.moveToFenPosition(fen, true);
            } else {
                engineWorker.moveToStartPosition(true);
            }
        } catch (InvalidFenPositionException e) {
            throw new InvalidMoveException(e);
        }
    }
}
