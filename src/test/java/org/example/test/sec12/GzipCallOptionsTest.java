package org.example.test.sec12;

import org.example.sec12.BalanceCheckRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GzipCallOptionsTest extends AbstractTest {

	public static final Logger log = LoggerFactory.getLogger(GzipCallOptionsTest.class);

	@Test
	public void gzipDemo() {
		var req = BalanceCheckRequest.newBuilder().setAccountNumber(1).build();
		var res = bankServiceBlockingStub.withCompression("gzip").getAccountBalance(req);
		log.info("{}", res);
	}
}
