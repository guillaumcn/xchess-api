package com.xchess.engine.api.controller;

import com.xchess.ChessEngine;
import com.xchess.engine.api.domain.enumeration.ChessColor;
import com.xchess.engine.api.domain.response.BestMoveResponse;
import com.xchess.engine.api.domain.response.PositionEvaluationResponse;
import com.xchess.engine.api.domain.response.PossibleMovesResponse;
import com.xchess.engine.api.exceptions.ChessEngineWorkerExecutionException;
import com.xchess.engine.api.exceptions.InvalidSyntaxException;
import com.xchess.engine.api.service.ChessService;
import com.xchess.evaluation.ChessEngineEvaluation;
import com.xchess.evaluation.ChessEngineEvaluationType;
import com.xchess.evaluation.parameter.EvaluationParameters;
import com.xchess.exceptions.InvalidSquareSyntaxException;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ChessServiceTest {

    private ChessEngine engine;
    private ChessService chessService;

    @Before
    public void setUp() throws Exception {
        GenericObjectPool<ChessEngine> pool = mock(GenericObjectPool.class);
        this.engine = mock(ChessEngine.class);
        when(pool.borrowObject()).thenReturn(engine);
        this.chessService =
                new ChessService(new PoolWrapperTestImplementation(pool));
    }

    @Test
    public void shouldCallEngineGetVersionMethod() throws Exception {
        doReturn(12.4f).when(this.engine).getEngineVersion();

        assertEquals(12.4f, this.chessService.getEngineVersion().getVersion());
    }

    @Test
    public void shouldMoveToFenPositionWhenGettingPossibleMoves() throws Exception {
        String fen = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8" +
                "/PPPP1PPP/RNBQKBNR w KQkq d6 0 2";
        this.chessService.getPossibleMoves(fen, null);

        verify(this.engine, times(1)).moveToFenPosition(fen, true);
    }

    @Test
    public void shouldMoveToStartPositionWhenGettingPossibleMoves() throws Exception {
        this.chessService.getPossibleMoves(null, null);

        verify(this.engine, times(1)).moveToStartPosition(true);
    }

    @Test
    public void shouldGetPossibleMoves() throws Exception {
        List<String> expected = Collections.singletonList("a2a4");
        doReturn(expected).when(this.engine).getPossibleMoves();
        PossibleMovesResponse result =
                this.chessService.getPossibleMoves(null, null);
        assertEquals(expected, result.getPossibleMoves());
    }

    @Test
    public void shouldGetPossibleMovesForSquare() throws Exception {
        List<String> expected = Collections.singletonList("a2a4");
        doReturn(expected).when(this.engine).getPossibleMoves("a2");
        PossibleMovesResponse result =
                this.chessService.getPossibleMoves(null, "a2");
        assertEquals(expected, result.getPossibleMoves());
    }

    @Test
    public void shouldThrowExceptionWhenGetPossibleMoves() throws Exception {
        doThrow(IOException.class).when(this.engine).getPossibleMoves();
        assertThrows(ChessEngineWorkerExecutionException.class,
                () -> this.chessService.getPossibleMoves(null, null));
    }

    @Test
    public void shouldThrowExceptionWhenGetPossibleMovesWithBadSyntax() throws Exception {
        doThrow(InvalidSquareSyntaxException.class).when(this.engine).getPossibleMoves(anyString());
        assertThrows(InvalidSyntaxException.class,
                () -> this.chessService.getPossibleMoves(null, "notvalid"));
    }

    @Test
    public void shouldMoveToFenPositionWhenFindingBestMove() throws Exception {
        String fen = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8" +
                "/PPPP1PPP/RNBQKBNR w KQkq d6 0 2";
        this.chessService.findBestMove(fen,
                EvaluationParameters.builder().depth(10).build());

        verify(this.engine, times(1)).moveToFenPosition(fen, true);
    }

    @Test
    public void shouldMoveToStartPositionWhenFindingBestMove() throws Exception {
        this.chessService.findBestMove(null,
                EvaluationParameters.builder().depth(10).build());

        verify(this.engine, times(1)).moveToStartPosition(true);
    }

    @Test
    public void shouldFindBestMove() throws Exception {
        String expected = "a2a4";
        doReturn(expected).when(this.engine).findBestMove(any(EvaluationParameters.class));
        BestMoveResponse result = this.chessService.findBestMove(null,
                EvaluationParameters.builder().depth(10).build());
        assertEquals(expected, result.getBestMove());
    }

    @Test
    public void shouldThrowExceptionWhenFindBestMove() throws Exception {
        doThrow(IOException.class).when(this.engine).findBestMove(any(EvaluationParameters.class));
        assertThrows(ChessEngineWorkerExecutionException.class,
                () -> this.chessService.findBestMove(null,
                        EvaluationParameters.builder().depth(10).build()));
    }

    @Test
    public void shouldMoveToFenPositionWhenEvaluatingPosition() throws Exception {
        String fen = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8" +
                "/PPPP1PPP/RNBQKBNR w KQkq d6 0 2";
        initEvaluationMock(12);
        this.chessService.getPositionEvaluation(fen,
                EvaluationParameters.builder().depth(10).build());

        verify(this.engine, times(1)).moveToFenPosition(fen, true);
    }

    @Test
    public void shouldMoveToStartPositionWhenEvaluatingPosition() throws Exception {
        initEvaluationMock(12);
        this.chessService.getPositionEvaluation(null,
                EvaluationParameters.builder().depth(10).build());

        verify(this.engine, times(1)).moveToStartPosition(true);
    }

    @Test
    public void shouldGetPositionEvaluationForWhite() throws Exception {
        initEvaluationMock(12);
        PositionEvaluationResponse result =
                this.chessService.getPositionEvaluation(null,
                        EvaluationParameters.builder().depth(10).build());
        assertEquals(12, result.getValue());
        assertEquals(ChessColor.WHITE, result.getColor());
        assertEquals(ChessEngineEvaluationType.CENTIPAWNS, result.getType());
    }

    @Test
    public void shouldGetPositionEvaluationForBlack() throws Exception {
        initEvaluationMock(-12);
        PositionEvaluationResponse result =
                this.chessService.getPositionEvaluation(null,
                        EvaluationParameters.builder().depth(10).build());
        assertEquals(12, result.getValue());
        assertEquals(ChessColor.BLACK, result.getColor());
        assertEquals(ChessEngineEvaluationType.CENTIPAWNS, result.getType());
    }

    @Test
    public void shouldThrowExceptionWhenEvaluatingPosition() throws Exception {
        doThrow(IOException.class).when(this.engine).getPositionEvaluation(any(EvaluationParameters.class));
        assertThrows(ChessEngineWorkerExecutionException.class,
                () -> this.chessService.getPositionEvaluation(null,
                        EvaluationParameters.builder().depth(10).build()));
    }

    private void initEvaluationMock(int value) throws IOException,
            TimeoutException {
        ChessEngineEvaluation chessEngineEvaluation =
                ChessEngineEvaluation.builder()
                        .type(ChessEngineEvaluationType.CENTIPAWNS)
                        .value(value)
                        .build();
        doReturn(chessEngineEvaluation).when(this.engine).getPositionEvaluation(any(EvaluationParameters.class));
    }
}
