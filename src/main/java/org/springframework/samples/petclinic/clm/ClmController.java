package org.springframework.samples.petclinic.clm;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Trace;
import io.micrometer.core.annotation.Timed;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.MetricService;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Date;

import static org.springframework.samples.petclinic.Util.doWait;
import static org.springframework.samples.petclinic.Util.timeForFakeError;

@Controller
public class ClmController {

	private final OwnerRepository ownerRepository;
	private final ErrorService errorService;

	public ClmController(OwnerRepository ownerRepository, ErrorService errorService) {
		this.ownerRepository = ownerRepository;
		this.errorService = errorService;
	}

	/**
	 * This method demonstrates an auto instrumented method.
	 */
	@GetMapping("/clm/auto-only")
	public String autoOnly(Model model) {
		MetricService.increaseCount("/clm/auto-only");
		setMessage(model, "Java/org.springframework.samples.petclinic.clm.ClmController/autoOnly");
		doWait();
		return "welcome";
	}

	// What happens when error outside of controller
	@GetMapping("/clm/error")
	public String error() {
		MetricService.increaseCount("/clm/error");
		doWait();
		errorService.iAmError();
		return "never";
	}

	/**
	 * Besides the auto instrumentation, this also demonstrates a manual trace created by an annotation.
	 */
	@GetMapping("/clm/annotation")
	public String annotation(Model model) {
		MetricService.increaseCount("/clm/annotation");
		setMessage(model, "Java/org.springframework.samples.petclinic.clm.ClmController/annotation");
		annotatedMethod();
		if (timeForFakeError()) {
			throw new RuntimeException("Sample error");
		}
		doWait();
		return "welcome";
	}

	@Trace
	private void annotatedMethod() {
		if (timeForFakeError()) {
			doWait(10000);
		}
		doWait();
	}

	/**
	 * Besides the auto instrumentation, this also demonstrates a manual trace created using the API.
	 */
	@GetMapping("/clm/api")
	public String api(Model model) {
		MetricService.increaseCount("/clm/api");
		setMessage(model, "Java/org.springframework.samples.petclinic.clm.ClmController/api");
		apiMethod();
		doWait();
		return "welcome";
	}

	@Timed
	public void apiMethod() {
		Segment segment = NewRelic.getAgent().getTransaction().startSegment("segmentCreatedByTheApi");
		doWait();
		segment.end();
	}

	/**
	 * Besides the auto instrumentation, this also demonstrates a trace created by XML instrumentation.
	 */
	@GetMapping("/clm/xml")
	public String xml(Model model) {
		MetricService.increaseCount("/clm/xml");
		setMessage(model, "Java/org.springframework.samples.petclinic.clm.ClmController/xml");
		xmlMethod();
		doWait();
		return "welcome";
	}

	@Timed
	public void xmlMethod() {
		doWait();
	}

	/**
	 * Besides the auto instrumentation, this also demonstrates a trace in a static method.
	 */
	@GetMapping("/clm/static")
	public String staticRequest(Model model) {
		MetricService.increaseCount("/clm/static");
		setMessage(model, "Java/org.springframework.samples.petclinic.clm.ClmController/static");
		staticMethod();
		doWait();
		return "welcome";
	}

	@Trace
	private static void staticMethod() {
		doWait();
	}


	/**
	 * Besides the auto instrumentation, this also demonstrates a trace that contains an Http request.
	 */
	// 253.02907133243607 from actuator
	@GetMapping("/clm/http")
	public String http(Model model) {
		MetricService.increaseCount("/clm/http");
		setMessage(model, "Java/org.springframework.samples.petclinic.clm.ClmController/http");
		httpMethod();
		doWait();
		return "welcome";
	}

	@Trace
	private void httpMethod() {
		final var sw = new StopWatch();
		sw.start();
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpGet get = new HttpGet("http://example.com");
			try (CloseableHttpResponse execute = httpClient.execute(get)) {
				StatusLine statusLine = execute.getStatusLine();
				System.out.println(statusLine);
			}
		} catch (IOException e) {
			MetricService.increaseCount("httpMethod() error");
			throw new RuntimeException(e);
		}
		sw.stop();
		final var elapsed = sw.getTotalTimeMillis();
		MetricService.increaseCount("httpMethod()", elapsed);
	}


	/**
	 * Besides the auto instrumentation, this also demonstrates a trace that has a db call.
	 */
	@GetMapping("/clm/db")
	public String db(Model model) {
		MetricService.increaseCount("/clm/db");
		setMessage(model, "Java/org.springframework.samples.petclinic.clm.ClmController/db");
		dbMethod();
		doWait();
		return "welcome";
	}

	@Trace
	private void dbMethod() {
		ownerRepository.findAll(Pageable.ofSize(100000));
	}


	private void setMessage(Model model, String spanName) {
		model.addAttribute("message",
			new Date() + ": Look for a span named \"" + spanName + "\" and its children.");
	}

	@PostConstruct
	@Trace(dispatcher = true)
	void init() {
		doWait();
	}
}
