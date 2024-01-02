package com.xchess.engine.api.controller;

import com.xchess.ChessEngine;
import com.xchess.engine.api.pool.PoolWrapper;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class PoolWrapperTestImplementation extends PoolWrapper {
    public PoolWrapperTestImplementation(GenericObjectPool<ChessEngine> pool) {
        super();
        this.pool = pool;
    }
}
