package org.example.test.sec06;

import com.google.protobuf.Empty;
import org.example.models.BalanceCheckRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnaryBlockingClientTest extends AbstractTest {

	public static final Logger log = LoggerFactory.getLogger(UnaryBlockingClientTest.class);

	@Test
	public void getAccountBalance() {
		var balanceCheckRequest = BalanceCheckRequest.newBuilder().setAccountNumber(1).build();
		var balance = bankServiceBlockingStub.getAccountBalance(balanceCheckRequest);
		log.info("the account balance is {}", balance);
		Assertions.assertEquals(100, balance.getBalance());
	}

	@Test
	public void getAllAccountBalances() {
		var allAccounts = bankServiceBlockingStub.getAllAccounts(Empty.getDefaultInstance());
		log.info("the account balance of all accounts {}", allAccounts);
		Assertions.assertEquals(10, allAccounts.getAccountsBalanceCount());
	}

}
