package org.example.test.sec10;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import org.example.commons.GrpcServer;
import org.example.sec10.BankService;
import org.example.sec10.BankServiceGrpc;
import org.example.sec10.ErrorMessage;
import org.example.sec10.ValidationCode;
import org.example.test.common.AbstractChannelTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.Optional;

public abstract class AbstractTest extends AbstractChannelTest {

	private final GrpcServer grpcServer = GrpcServer.create(new BankService());
	protected BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;
	protected BankServiceGrpc.BankServiceStub bankServiceStub;

	private static final Metadata.Key<ErrorMessage> ERROR_MESSAGE_KEY = ProtoUtils.keyForProto(ErrorMessage.getDefaultInstance());

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

	protected ValidationCode getValidationCode(Throwable t) {
		return Optional.ofNullable(Status.trailersFromThrowable(t))
				.map(m -> m.get(ERROR_MESSAGE_KEY))
				.map(ErrorMessage::getValidationCode)
				.orElseThrow();
	}
}
