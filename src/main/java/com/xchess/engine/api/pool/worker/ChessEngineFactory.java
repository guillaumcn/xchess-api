package com.xchess.engine.api.pool.worker;

import com.xchess.ChessEngine;
import com.xchess.process.ProcessWrapper;
import com.xchess.stockfish.Stockfish;
import com.xchess.stockfish.config.StockfishConfig;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChessEngineFactory extends BasePooledObjectFactory<ChessEngine> {
    private final ChessEngineProperties properties;

    @Autowired
    public ChessEngineFactory(ChessEngineProperties properties) {
        this.properties = properties;
    }

    @Override
    public ChessEngine create() throws Exception {
        if (this.properties.getType().equals("stockfish")) {
            return new Stockfish(new ProcessWrapper(this.properties.getCommand()),
                    new StockfishConfig().setTimeoutInMs(this.properties.getTimeout()));
        }
        throw new InvalidPropertyException(ChessEngineProperties.class,
                "chess.engine.type", "Invalid property value");

    }

    @Override
    public PooledObject<ChessEngine> wrap(ChessEngine engineWorker) {
        return new DefaultPooledObject<>(engineWorker);
    }

    @Override
    public void destroyObject(PooledObject<ChessEngine> p,
                              DestroyMode destroyMode) throws Exception {
        super.destroyObject(p, destroyMode);
        ChessEngine chessEngine = p.getObject();
        chessEngine.stop();
    }

    @Override
    public boolean validateObject(PooledObject<ChessEngine> p) {
        return p.getObject().healthCheck();
    }
}
