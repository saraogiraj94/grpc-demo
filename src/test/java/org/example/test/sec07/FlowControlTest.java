package org.example.test.sec07;

import org.example.commons.GrpcServer;
import org.example.models.FlowControlServiceGrpc;
import org.example.sec07.FlowControlService;
import org.example.test.common.AbstractChannelTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FlowControlTest extends AbstractChannelTest {

	private final GrpcServer grpcServer = GrpcServer.create(new FlowControlService());
	protected FlowControlServiceGrpc.FlowControlServiceStub stub;

	@BeforeAll
	public void setup() {
		this.grpcServer.start();
		this.stub = FlowControlServiceGrpc.newStub(channel);
	}

	@AfterAll
	public void stop() {
		this.grpcServer.stop();
	}

	@Test
	public void flowCtrl() {
		var responseObserver = new ResponseHandler();
		var reqObs = this.stub.getMessage(responseObserver);
		responseObserver.setRequestSizeStreamObserver(reqObs);
		responseObserver.start();
		responseObserver.await();
	}
}
