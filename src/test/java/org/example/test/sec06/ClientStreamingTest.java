package org.example.test.sec06;

import org.example.models.AccountBalance;
import org.example.models.DepositRequest;
import org.example.models.Money;
import org.example.test.common.ResponseObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

public class ClientStreamingTest extends AbstractTest {

	@Test
	public void deposit() {
		var responseObserver = ResponseObserver.<AccountBalance>create();
		var requestObserver = this.bankServiceStub.deposit(responseObserver);
		requestObserver.onNext(DepositRequest.newBuilder().setAccountNumber(1).build());
		IntStream.rangeClosed(1, 10)
				.mapToObj(i -> Money.newBuilder().setAmount(10).build())
				.map(m -> DepositRequest.newBuilder().setMoney(m).build())
				.forEach(requestObserver::onNext);
		requestObserver.onCompleted();

		//waiting for response observer
		responseObserver.await();

		//
		Assertions.assertEquals(1, responseObserver.getList().size());
		Assertions.assertEquals(200, responseObserver.getList().getFirst().getBalance());

	}
}
