package com.xchess.engine.api.pool;

import com.xchess.ChessEngine;
import com.xchess.engine.api.pool.worker.ChessEngineFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Function;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PoolWrapper {
    protected GenericObjectPool<ChessEngine> pool;

    @Autowired
    public PoolWrapper(PoolProperties poolProperties,
                       ChessEngineFactory chessEngineFactory) throws Exception {
        GenericObjectPoolConfig<ChessEngine> config =
                new GenericObjectPoolConfig<>();
        config.setMinIdle(poolProperties.getMinIdle());
        config.setMaxTotal(poolProperties.getMaxTotal());
        config.setSoftMinEvictableIdleDuration(Duration.ofMillis(poolProperties.getEvictableIdleDurationInMs()));
        config.setTimeBetweenEvictionRuns(Duration.ofMillis(poolProperties.getTimeBetweenEvictionRunsInMs()));

        this.pool = new GenericObjectPool<>(chessEngineFactory, config);
        this.pool.preparePool();
    }

    protected PoolWrapper() {
    }

    public <T> T queueAction(Function<ChessEngine, T> action) throws Exception {
        ChessEngine engineWorker = borrowObject();
        try {
            T result = action.apply(engineWorker);
            this.pool.returnObject(engineWorker);
            return result;
        } catch (Exception e) {
            this.pool.invalidateObject(engineWorker);
            throw e;
        }
    }

    protected ChessEngine borrowObject() throws Exception {
        return this.pool.borrowObject();
    }
}
