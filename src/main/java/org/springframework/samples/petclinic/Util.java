package org.springframework.samples.petclinic;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Simple method that waits so the spans take some time to execute.
 */
public class Util {
	public static void doWait() {
		doWait(200);
	}

	public static void doWait(long timeout) {
		try {
			if (isEvenDay()) {
				timeout += 1000;
			}
			Thread.sleep(timeout);
		} catch (InterruptedException ex) {
			// ignore
		}
	}

	public static boolean isEvenDay() {
		return LocalDate.now().getDayOfYear() % 2 == 0;
	}

	public static boolean timeForFakeError() {
		String nowMillisStr = Long.toString(System.currentTimeMillis());
		return nowMillisStr.endsWith("7");
	}
}
