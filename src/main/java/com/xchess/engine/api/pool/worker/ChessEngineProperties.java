package com.xchess.engine.api.pool.worker;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "chess.engine")
public class ChessEngineProperties {
    private String type;
    private String command;
    private int timeout;
}
