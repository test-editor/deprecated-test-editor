package org.testeditor.core.logging;

import static org.testeditor.logging.MDCHelper.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testeditor.logging.MDCHelper;

import static org.junit.Assert.*;

public class MDCHelperTest {

	private static final Logger logger = LoggerFactory.getLogger(MDCHelperTest.class);
	
	@Test
	public void testMdcHelper() {
		// When
		setContext("A test.");
		String context = MDC.get(MDCHelper.MAIN_CONTEXT);
		logger.info("Hello, world!");
		clearContext();
		
		// Then
		assertEquals(" [A test.]", context);
		assertNull(MDC.get(MDCHelper.MAIN_CONTEXT));
	}
	
}
