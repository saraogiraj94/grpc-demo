package org.example.sec06.requestHandlers;

import io.grpc.stub.StreamObserver;
import org.example.models.AccountBalance;
import org.example.models.DepositRequest;
import org.example.sec06.repository.AccountRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepositRequestHandler implements StreamObserver<DepositRequest> {

	private static final Logger log = LoggerFactory.getLogger(DepositRequestHandler.class);

	public final StreamObserver<AccountBalance> responseObserver;
	private int accountNumber;

	public DepositRequestHandler(StreamObserver<AccountBalance> responseObserver) {this.responseObserver = responseObserver;}

	@Override
	public void onNext(DepositRequest depositRequest) {
		switch (depositRequest.getRequestCase()) {
			case ACCOUNT_NUMBER -> this.accountNumber = depositRequest.getAccountNumber();
			case MONEY -> AccountRepo.addAmount(this.accountNumber, depositRequest.getMoney().getAmount());
		}
	}

	@Override
	public void onError(Throwable throwable) {
		//When the client wants to cancel the request
		log.info("client error{}", throwable.getMessage());
	}

	@Override
	public void onCompleted() {
		//When client want to complete the request, like we
		var accountBalance = AccountBalance.newBuilder()
				.setAccountNumber(this.accountNumber)
				.setBalance(AccountRepo.getBalance(this.accountNumber))
				.build();
		responseObserver.onNext(accountBalance);
		responseObserver.onCompleted();
	}
}
