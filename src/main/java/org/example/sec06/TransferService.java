package org.example.sec06;

import io.grpc.stub.StreamObserver;
import org.example.models.TransferRequest;
import org.example.models.TransferResponse;
import org.example.models.TransferServiceGrpc;
import org.example.sec06.requestHandlers.TransferRequestHandler;

public class TransferService extends TransferServiceGrpc.TransferServiceImplBase {

	@Override
	public StreamObserver<TransferRequest> transfer(StreamObserver<TransferResponse> responseObserver) {
		return new TransferRequestHandler(responseObserver);
	}
}
