package org.example.test.sec10;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import org.example.sec10.AccountBalance;
import org.example.sec10.BalanceCheckRequest;
import org.example.sec10.ErrorMessage;
import org.example.sec10.ValidationCode;
import org.example.test.common.ResponseObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnaryClientTest extends AbstractTest {

	public static final Logger log = LoggerFactory.getLogger(UnaryClientTest.class);

	@Test
	public void blockingInputValidationTest() {

		var ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
			var request = BalanceCheckRequest.newBuilder().setAccountNumber(11).build();
			var response = this.bankServiceBlockingStub.getAccountBalance(request);
		});

		Assertions.assertEquals(Status.Code.INVALID_ARGUMENT, ex.getStatus().getCode());

		//Accessing Trailer
		Assertions.assertEquals(ValidationCode.INVALID_ACCOUNT, getValidationCode(ex));
	}

	@Test
	public void asyncInputValidationTest() {

		var observer = ResponseObserver.<AccountBalance>create();
		var request = BalanceCheckRequest.newBuilder().setAccountNumber(11).build();
		this.bankServiceStub.getAccountBalance(request, observer);
		observer.await();

		//As exception not getting any items in list
		Assertions.assertTrue(observer.getList().isEmpty());
		Assertions.assertNotNull(observer.getThrowable());
		Assertions.assertEquals(Status.Code.INVALID_ARGUMENT, ((StatusRuntimeException) observer.getThrowable()).getStatus().getCode());

		//Accessing Trailer
		Assertions.assertEquals(ValidationCode.INVALID_ACCOUNT, getValidationCode(observer.getThrowable()));
	}
}
