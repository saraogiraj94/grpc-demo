package org.example.sec12;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import org.example.sec12.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class BankService extends org.example.sec12.BankServiceGrpc.BankServiceImplBase {

	private static final Logger log = LoggerFactory.getLogger(BankService.class);

	@Override
	public void getAccountBalance(org.example.sec12.BalanceCheckRequest request, StreamObserver<org.example.sec12.AccountBalance> responseObserver) {
		var accountNumber = request.getAccountNumber();
		var balance = AccountRepository.getBalance(accountNumber);
		var accountBalance = org.example.sec12.AccountBalance.newBuilder().setAccountNumber(accountNumber).setBalance(balance).build();
		((ServerCallStreamObserver<org.example.sec12.AccountBalance>) responseObserver).setCompression("gzip");
		responseObserver.onNext(accountBalance);
		responseObserver.onCompleted();
	}

	@Override
	public void withdraw(org.example.sec12.WithdrawRequest request, StreamObserver<org.example.sec12.Money> responseObserver) {
		var accountNumber = request.getAccountNumber();
		var requestedAmount = request.getAmount();
		var accountBalance = AccountRepository.getBalance(accountNumber);

		if (requestedAmount > accountBalance) {
			responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException());
			return;
		}

		for (int i = 0; i < (requestedAmount / 10) && !Context.current().isCancelled(); i++) {
			var money = org.example.sec12.Money.newBuilder().setAmount(10).build();
			responseObserver.onNext(money);
			log.info("money sent {}", money);
			AccountRepository.deductAmount(accountNumber, 10);
			Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
		}
		log.info("streaming completed");
		responseObserver.onCompleted();
	}
}
