package org.example.test.sec12;

import org.example.sec12.Money;
import org.example.sec12.WithdrawRequest;
import org.example.test.common.ResponseObserver;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

public class ExecutorCallOptionsTest extends AbstractTest {

	public static final Logger log = LoggerFactory.getLogger(ExecutorCallOptionsTest.class);

	@Test
	public void executorThreadDemo() {
		var withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(2).setAmount(20).build();
		var observer = ResponseObserver.<Money>create();
		this.bankServiceStub.withExecutor(Executors.newVirtualThreadPerTaskExecutor()).withdraw(withdrawRequest, observer);
		observer.await();
	}
}
