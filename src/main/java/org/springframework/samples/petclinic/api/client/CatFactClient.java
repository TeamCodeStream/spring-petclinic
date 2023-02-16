package org.springframework.samples.petclinic.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.api.agent.Trace;
import io.micrometer.core.annotation.Timed;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.samples.petclinic.Util.doWait;

@Component
public class CatFactClient {
	private final ObjectMapper objectMapper;

	public CatFactClient(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Trace
	@Timed
	public CatFact fetchCatFact() {
		doWait(60);
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpGet get = new HttpGet("https://catfact.ninja/fact");
			try (CloseableHttpResponse response = httpClient.execute(get)) {
				return objectMapper.readValue(response.getEntity().getContent(), CatFact.class);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
