package mobile.aerogear.org.metrics;

import org.aerogear.mobile.core.configuration.ServiceConfiguration;

public class MetricsConfig {
    private String uri;

    public MetricsConfig(ServiceConfiguration configuration) {
        setUri(configuration.getUri());
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
