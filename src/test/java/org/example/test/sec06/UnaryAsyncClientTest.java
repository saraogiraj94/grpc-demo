package org.example.test.sec06;

import com.google.protobuf.Empty;
import org.example.models.AccountBalance;
import org.example.models.AllAccountBalance;
import org.example.models.BalanceCheckRequest;
import org.example.test.common.ResponseObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnaryAsyncClientTest extends AbstractTest {

	public static final Logger log = LoggerFactory.getLogger(UnaryAsyncClientTest.class);

	@Test
	public void asyncGetAccountBalance() throws InterruptedException {
		var balanceCheckRequest = BalanceCheckRequest.newBuilder().setAccountNumber(1).build();
		var observer = ResponseObserver.<AccountBalance>create();
		this.bankServiceStub.getAccountBalance(balanceCheckRequest, observer);
		observer.await();
		Assertions.assertEquals(1, observer.getList().size());
		Assertions.assertEquals(100, observer.getList().getFirst().getBalance());
		Assertions.assertNull(observer.getThrowable());
	}

	@Test
	public void asyncGetAllAccountsTest() {
		var observer = ResponseObserver.<AllAccountBalance>create();
		this.bankServiceStub.getAllAccounts(Empty.getDefaultInstance(), observer);
		observer.await();
		Assertions.assertEquals(1, observer.getList().size());
		Assertions.assertEquals(100, observer.getList().getFirst().getAccountsBalanceList().getFirst().getBalance());
		Assertions.assertNull(observer.getThrowable());
	}

}
