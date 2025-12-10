package org.example.sec06.requestHandlers;

import io.grpc.stub.StreamObserver;
import org.example.models.AccountBalance;
import org.example.models.TransferRequest;
import org.example.models.TransferResponse;
import org.example.models.TransferStatus;
import org.example.sec06.repository.AccountRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferRequestHandler implements StreamObserver<TransferRequest> {

	private static final Logger log = LoggerFactory.getLogger(TransferRequestHandler.class);

	public final StreamObserver<TransferResponse> responseObserver;

	public TransferRequestHandler(StreamObserver<TransferResponse> responseObserver) {this.responseObserver = responseObserver;}

	@Override
	public void onNext(TransferRequest transferRequest) {
		var status = transfer(transferRequest);
		var response = TransferResponse.newBuilder()
				.setFromAccount(buildAccountBalance(transferRequest.getFromAccount()))
				.setToAccount(buildAccountBalance(transferRequest.getToAccount()))
				.setStatus(status)
				.build();
		this.responseObserver.onNext(response);
	}

	@Override
	public void onError(Throwable throwable) {
		log.info("client error {}", throwable.getMessage());
	}

	@Override
	public void onCompleted() {
		log.info("transfer request stream completed");
		this.responseObserver.onCompleted();
	}

	private TransferStatus transfer(TransferRequest transferRequest) {
		var amount = transferRequest.getAmount();
		var fromAccount = transferRequest.getFromAccount();
		var toAccount = transferRequest.getToAccount();
		var status = TransferStatus.REJECTED;
		if (AccountRepo.getBalance(fromAccount) >= amount && (fromAccount != toAccount)) {
			AccountRepo.deductAmount(fromAccount, amount);
			AccountRepo.addAmount(toAccount, amount);
			status = TransferStatus.COMPLETED;
		}
		return status;
	}

	private AccountBalance buildAccountBalance(int accountNumber) {
		return AccountBalance.newBuilder().setAccountNumber(accountNumber).setBalance(AccountRepo.getBalance(accountNumber)).build();
	}

}
