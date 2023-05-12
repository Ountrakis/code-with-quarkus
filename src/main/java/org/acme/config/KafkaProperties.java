package org.acme.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix ="kafka" )
public interface KafkaProperties {
    @WithName("bootstrap.servers")
    String getBootstrapServer();

    @WithName("consumer-group")
    String getConsumerGroup();

    @WithName("auto-offset")
    String getAutoOffset();
}
