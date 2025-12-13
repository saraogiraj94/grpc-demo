package org.example.sec09;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.example.sec09.repository.AccountRepo;
import org.example.sec09.validator.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class BankService extends org.example.sec09.BankServiceGrpc.BankServiceImplBase {

	private static final Logger log = LoggerFactory.getLogger(BankService.class);

	@Override
	public void getAccountBalance(org.example.sec09.BalanceCheckRequest request, StreamObserver<org.example.sec09.AccountBalance> responseObserver) {
		RequestValidator.validateAccount(request.getAccountNumber())
				.map(Status::asRuntimeException)
				.ifPresentOrElse(responseObserver::onError, () -> sendBalance(request, responseObserver));
	}

	private void sendBalance(org.example.sec09.BalanceCheckRequest request, StreamObserver<org.example.sec09.AccountBalance> responseObserver) {
		var accountNumber = request.getAccountNumber();
		var balance = AccountRepo.getBalance(accountNumber);

		var accountBalance = org.example.sec09.AccountBalance.newBuilder().setAccountNumber(accountNumber).setBalance(balance).build();
		responseObserver.onNext(accountBalance);
		responseObserver.onCompleted();
	}

	@Override
	public void withdraw(org.example.sec09.WithdrawRequest request, StreamObserver<org.example.sec09.Money> responseObserver) {
		RequestValidator.validateAccount(request.getAccountNumber())
				.or(() -> RequestValidator.validateAmount(request.getAmount()))
				.or(() -> RequestValidator.hasSufficientBalance(AccountRepo.getBalance(request.getAccountNumber()), request.getAmount()))
				.map(Status::asRuntimeException)
				.ifPresentOrElse(responseObserver::onError, () -> sendMoney(request, responseObserver));
	}

	private void sendMoney(org.example.sec09.WithdrawRequest request, StreamObserver<org.example.sec09.Money> responseObserver) {
		var accountNumber = request.getAccountNumber();
		var amount = request.getAmount();
		var accountBalance = AccountRepo.getBalance(accountNumber);
		if (amount > accountBalance) {
			responseObserver.onCompleted();
			return;
		}

		for (int i = 0; i < amount / 10; i++) {
			try {
				var money = org.example.sec09.Money.newBuilder().setAmount(10).build();
				if (i == 3) {
					throw new RuntimeException("oops something went wrong");
				}
				responseObserver.onNext(money);
				log.info("money sent {}", money);
				AccountRepo.deductAmount(accountNumber, 10);
				Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
			} catch (Exception e) {
				responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
			}

		}

		responseObserver.onCompleted();
	}
}
