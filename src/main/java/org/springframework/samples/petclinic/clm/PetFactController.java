package org.springframework.samples.petclinic.clm;

import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.MetricService;
import org.springframework.samples.petclinic.api.PetFactResponse;
import org.springframework.samples.petclinic.api.PetFactService;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.samples.petclinic.Util.doWait;

@RestController
public class PetFactController {
	private final PetFactService petFactService;

	public PetFactController(PetFactService petFactService) {
		this.petFactService = petFactService;
	}

	// 736.551164049810504 for http actuator
	@GetMapping(value = "/clm/facts", produces = MediaType.APPLICATION_JSON_VALUE)
	public PetFactResponse getPetFacts() {
		final var sw = new StopWatch();
		sw.start();
		doWait(50);
		final var result = petFactService.getPetFacts();
		sw.stop();
		final var elapsed = sw.getTotalTimeMillis();
		MetricService.increaseCount("/clm/facts", elapsed);
		return result;
	}
}
