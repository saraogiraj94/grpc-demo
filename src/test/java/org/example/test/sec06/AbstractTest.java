package org.example.test.sec06;

import org.example.commons.GrpcServer;
import org.example.models.BankServiceGrpc;
import org.example.sec06.BankService;
import org.example.test.common.AbstractChannelTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractTest extends AbstractChannelTest {

	private final GrpcServer grpcServer = GrpcServer.create(new BankService());
	protected BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;
	protected BankServiceGrpc.BankServiceStub bankServiceStub;

	@BeforeAll
	public void setup() {
		this.grpcServer.start();
		this.bankServiceBlockingStub = BankServiceGrpc.newBlockingStub(channel);
		this.bankServiceStub = BankServiceGrpc.newStub(channel);
	}

	@AfterAll
	public void stop() {
		this.grpcServer.stop();
	}
}
