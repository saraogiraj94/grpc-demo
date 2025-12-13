package org.example.test.sec09;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.sec09.Money;
import org.example.sec09.WithdrawRequest;
import org.example.test.common.ResponseObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ServerStreamValidationTest extends AbstractTest {

	@ParameterizedTest
	@MethodSource("testData")
	public void blockingInputValidationTest(WithdrawRequest withdrawRequest, Status.Code code) {

		var ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
			var response = this.bankServiceBlockingStub.withdraw(withdrawRequest).hasNext();
		});

		Assertions.assertEquals(code, ex.getStatus().getCode());

	}

	@ParameterizedTest
	@MethodSource("testData")
	public void asyncInputValidationTest(WithdrawRequest withdrawRequest, Status.Code code) {

		var observer = ResponseObserver.<Money>create();
		this.bankServiceStub.withdraw(withdrawRequest, observer);
		observer.await();

		//As exception not getting any items in list
		Assertions.assertTrue(observer.getList().isEmpty());
		Assertions.assertNotNull(observer.getThrowable());
		Assertions.assertEquals(code, ((StatusRuntimeException) observer.getThrowable()).getStatus().getCode());

	}

	private Stream<Arguments> testData() {
		return Stream.of(Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(11).setAmount(101).build(), Status.Code.INVALID_ARGUMENT),
				Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(10).setAmount(17).build(), Status.Code.INVALID_ARGUMENT),
				Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(10).setAmount(120).build(), Status.Code.FAILED_PRECONDITION));
	}
}
