package org.example.test.sec06;

import org.example.models.TransferRequest;
import org.example.models.TransferResponse;
import org.example.models.TransferStatus;
import org.example.test.common.ResponseObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BidirectionalStreamingTest extends AbstractTest {

	@Test
	void transferTest() {
		var responseObserver = ResponseObserver.<TransferResponse>create();
		var requestObserver = this.transferServiceStub.transfer(responseObserver);
		var listRequest = List.of(TransferRequest.newBuilder().setFromAccount(1).setToAccount(1).setAmount(10).build(),
				TransferRequest.newBuilder().setFromAccount(1).setToAccount(2).setAmount(110).build(),
				TransferRequest.newBuilder().setFromAccount(1).setToAccount(2).setAmount(10).build(),
				TransferRequest.newBuilder().setFromAccount(2).setToAccount(1).setAmount(10).build());
		listRequest.forEach(requestObserver::onNext);
		requestObserver.onCompleted();
		responseObserver.await();

		Assertions.assertEquals(4, responseObserver.getList().size());
		this.validate(responseObserver.getList().get(0), TransferStatus.REJECTED, 100, 100);
		this.validate(responseObserver.getList().get(1), TransferStatus.REJECTED, 100, 100);
		this.validate(responseObserver.getList().get(2), TransferStatus.COMPLETED, 90, 110);
		this.validate(responseObserver.getList().get(3), TransferStatus.COMPLETED, 100, 100);

	}

	private void validate(TransferResponse transferResponse, TransferStatus transferStatus, int fromAcct, int toAcct) {
		Assertions.assertEquals(transferStatus, transferResponse.getStatus());
		Assertions.assertEquals(fromAcct, transferResponse.getFromAccount().getBalance());
		Assertions.assertEquals(toAcct, transferResponse.getToAccount().getBalance());
	}
}
