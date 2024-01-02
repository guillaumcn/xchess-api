package com.xchess.engine.api.controller;

import com.xchess.ChessEngine;
import com.xchess.engine.api.exceptions.ChessEngineWorkerExecutionException;
import com.xchess.evaluation.ChessEngineEvaluation;
import com.xchess.evaluation.ChessEngineEvaluationType;
import com.xchess.evaluation.parameter.EvaluationParameters;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ChessControllerTest {
    private ChessController testSubject;
    private ChessEngine engine;

    @Before
    public void setUp() throws Exception {
        GenericObjectPool<ChessEngine> pool = mock(GenericObjectPool.class);
        this.engine = mock(ChessEngine.class);
        when(pool.borrowObject()).thenReturn(engine);
        testSubject =
                new ChessController(new PoolWrapperTestImplementation(pool));
    }

    @Test
    public void shouldCallEngineGetVersionMethod() throws Exception {
        doReturn(12.4f).when(this.engine).getEngineVersion();

        assertEquals(12.4f, this.testSubject.getEngineVersion());
    }

    @Test
    public void shouldMoveToFenPositionWhenGettingPossibleMoves() throws Exception {
        String fen = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8" +
                "/PPPP1PPP/RNBQKBNR w KQkq d6 0 2";
        this.testSubject.getPossibleMoves(fen, null);

        verify(this.engine, times(1)).moveToFenPosition(fen, true);
    }

    @Test
    public void shouldMoveToStartPositionWhenGettingPossibleMoves() throws Exception {
        this.testSubject.getPossibleMoves(null, null);

        verify(this.engine, times(1)).moveToStartPosition(true);
    }

    @Test
    public void shouldGetPossibleMoves() throws Exception {
        List<String> expected = Collections.singletonList("a2a4");
        doReturn(expected).when(this.engine).getPossibleMoves();
        List<String> result = this.testSubject.getPossibleMoves(null, null);
        assertEquals(expected, result);
    }

    @Test
    public void shouldGetPossibleMovesForSquare() throws Exception {
        List<String> expected = Collections.singletonList("a2a4");
        doReturn(expected).when(this.engine).getPossibleMoves("a2");
        List<String> result = this.testSubject.getPossibleMoves(null, "a2");
        assertEquals(expected, result);
    }

    @Test
    public void shouldThrowExceptionWhenGetPossibleMoves() throws Exception {
        doThrow(IOException.class).when(this.engine).getPossibleMoves();
        assertThrows(ChessEngineWorkerExecutionException.class,
                () -> this.testSubject.getPossibleMoves(null, null));
    }

    @Test
    public void shouldMoveToFenPositionWhenFindingBestMove() throws Exception {
        String fen = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8" +
                "/PPPP1PPP/RNBQKBNR w KQkq d6 0 2";
        this.testSubject.findBestMove(fen,
                EvaluationParameters.builder().depth(10).build());

        verify(this.engine, times(1)).moveToFenPosition(fen, true);
    }

    @Test
    public void shouldMoveToStartPositionWhenFindingBestMove() throws Exception {
        this.testSubject.findBestMove(null,
                EvaluationParameters.builder().depth(10).build());

        verify(this.engine, times(1)).moveToStartPosition(true);
    }

    @Test
    public void shouldFindBestMove() throws Exception {
        String expected = "a2a4";
        doReturn(expected).when(this.engine).findBestMove(any(EvaluationParameters.class));
        String result = this.testSubject.findBestMove(null,
                EvaluationParameters.builder().depth(10).build());
        assertEquals(expected, result);
    }

    @Test
    public void shouldThrowExceptionWhenFindBestMove() throws Exception {
        doThrow(IOException.class).when(this.engine).findBestMove(any(EvaluationParameters.class));
        assertThrows(ChessEngineWorkerExecutionException.class,
                () -> this.testSubject.findBestMove(null,
                        EvaluationParameters.builder().depth(10).build()));
    }

    @Test
    public void shouldMoveToFenPositionWhenEvaluatingPosition() throws Exception {
        String fen = "rnbqkbnr/ppp1pppp/8/3p4/4P3/8" +
                "/PPPP1PPP/RNBQKBNR w KQkq d6 0 2";
        this.testSubject.getPositionEvaluation(fen,
                EvaluationParameters.builder().depth(10).build());

        verify(this.engine, times(1)).moveToFenPosition(fen, true);
    }

    @Test
    public void shouldMoveToStartPositionWhenEvaluatingPosition() throws Exception {
        this.testSubject.getPositionEvaluation(null,
                EvaluationParameters.builder().depth(10).build());

        verify(this.engine, times(1)).moveToStartPosition(true);
    }

    @Test
    public void shouldGetPositionEvaluation() throws Exception {
        ChessEngineEvaluation expected = ChessEngineEvaluation.builder()
                .type(ChessEngineEvaluationType.CENTIPAWNS)
                .value(12)
                .build();
        doReturn(expected).when(this.engine).getPositionEvaluation(any(EvaluationParameters.class));
        ChessEngineEvaluation result =
                this.testSubject.getPositionEvaluation(null,
                        EvaluationParameters.builder().depth(10).build());
        assertEquals(expected, result);
    }

    @Test
    public void shouldThrowExceptionWhenEvaluatingPosition() throws Exception {
        doThrow(IOException.class).when(this.engine).getPositionEvaluation(any(EvaluationParameters.class));
        assertThrows(ChessEngineWorkerExecutionException.class,
                () -> this.testSubject.getPositionEvaluation(null,
                        EvaluationParameters.builder().depth(10).build()));
    }
}
