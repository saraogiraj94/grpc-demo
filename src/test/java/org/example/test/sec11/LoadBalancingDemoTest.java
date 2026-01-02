package org.example.test.sec11;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.models.BalanceCheckRequest;
import org.example.models.BankServiceGrpc;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoadBalancingDemoTest {

	private static final Logger log = LoggerFactory.getLogger(LoadBalancingDemoTest.class);
	protected ManagedChannel channel;
	private BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;

	@BeforeAll
	public void setup() {
		this.channel = ManagedChannelBuilder.forAddress("localhost", 8585).usePlaintext().build();
		this.bankBlockingStub = BankServiceGrpc.newBlockingStub(channel);
	}

	@Test
	public void loadBalancingDemo() {
		for (int i = 0; i <= 10; i++) {
			var req = BalanceCheckRequest.newBuilder().setAccountNumber(i).build();
			var res = this.bankBlockingStub.getAccountBalance(req);
			log.info("{}", res);
		}
	}

}
