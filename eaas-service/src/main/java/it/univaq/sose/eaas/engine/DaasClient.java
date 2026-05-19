package it.univaq.sose.eaas.engine;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Thin client used by the EaaS to retrieve the actual data from the DaaS.
 * The EaaS does NOT trust whatever the calling client claims about the
 * candidate or the offer — it always re-fetches the canonical record.
 */
@Component
public class DaasClient {

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient client;

    public DaasClient(RestClient daasRestClient) {
        this.client = daasRestClient;
    }

    public Map<String, Object> getCandidate(String id) {
        return client.get()
                .uri("/api/candidates/{id}", id)
                .retrieve()
                .body(MAP_TYPE);
    }

    public Map<String, Object> getJob(String id) {
        return client.get()
                .uri("/api/jobs/{id}", id)
                .retrieve()
                .body(MAP_TYPE);
    }
}
