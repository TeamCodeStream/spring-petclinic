package org.springframework.samples.petclinic.api;

import com.newrelic.api.agent.Trace;
import io.micrometer.core.annotation.Timed;
import org.springframework.samples.petclinic.api.client.CatFactClient;
import org.springframework.samples.petclinic.api.client.DogFactClient;
import org.springframework.stereotype.Service;

import static org.springframework.samples.petclinic.Util.doWait;

@Service
public class PetFactService {
	private final CatFactClient catFactClient;
	private final DogFactClient dogFactClient;

	public PetFactService(CatFactClient catFactClient, DogFactClient dogFactClient) {
		this.catFactClient = catFactClient;
		this.dogFactClient = dogFactClient;
	}

	// 685ms from actuator?
	@Timed
	@Trace
	public PetFactResponse getPetFacts() {
		doWait(50);
		final var catFact = catFactClient.fetchCatFact();
		final var dogFact = dogFactClient.fetchDogFact();
		return new PetFactResponse(catFact.getFact(), dogFact.getFacts().get(0));
	}

}
