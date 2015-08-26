package org.testeditor.logging;

import org.slf4j.MDC;

/**
 * Provides utility methods for {@link MDC} usage.
 */
public class MDCHelper {

	public static final String MAIN_CONTEXT = "context"; //$NON-NLS-1$

	/**
	 * Puts the passed value in the {@link MDC} for key {@link #MAIN_CONTEXT}.
	 * @param value the main context
	 */
	public static void setContext(String value) {
		MDC.put(MAIN_CONTEXT, " [" + value + "]");
	}
	
	/**
	 * Removes the value for key {@link #MAIN_CONTEXT} from the {@link MDC}.
	 */
	public static void clearContext() {
		MDC.remove(MAIN_CONTEXT);
	}
	
}
