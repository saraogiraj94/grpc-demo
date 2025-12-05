package org.example.test.sec06;

import org.example.models.Money;
import org.example.models.WithdrawRequest;
import org.example.test.common.ResponseObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerStreamingClientTest extends AbstractTest {

	public static final Logger log = LoggerFactory.getLogger(ServerStreamingClientTest.class);

	@Test
	public void getWithdrawStream() {
		var withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(2).setAmount(20).build();
		var iterator = this.bankServiceBlockingStub.withdraw(withdrawRequest);
		int count = 0;
		while (iterator.hasNext()) {
			log.info("recevied money {}", iterator.next());
			count++;
		}
		Assertions.assertEquals(2, count);
	}

	@Test
	public void getWithdrawStreamAsync() {
		var withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(2).setAmount(20).build();
		var observer = ResponseObserver.<Money>create();
		this.bankServiceStub.withdraw(withdrawRequest, observer);
		observer.await();
		Assertions.assertEquals(2, observer.getList().size());
	}
}
