package org.example.sec10;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.example.sec10.AccountBalance;
import org.example.sec10.BalanceCheckRequest;
import org.example.sec10.BankServiceGrpc;
import org.example.sec10.Money;
import org.example.sec10.WithdrawRequest;
import org.example.sec10.repository.AccountRepo;
import org.example.sec10.validator.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

	private static final Logger log = LoggerFactory.getLogger(BankService.class);

	@Override
	public void getAccountBalance(BalanceCheckRequest request, StreamObserver<org.example.sec10.AccountBalance> responseObserver) {
		RequestValidator.validateAccount(request.getAccountNumber())
				.ifPresentOrElse(responseObserver::onError, () -> sendBalance(request, responseObserver));
	}

	private void sendBalance(BalanceCheckRequest request, StreamObserver<AccountBalance> responseObserver) {
		var accountNumber = request.getAccountNumber();
		var balance = AccountRepo.getBalance(accountNumber);

		var accountBalance = AccountBalance.newBuilder().setAccountNumber(accountNumber).setBalance(balance).build();
		responseObserver.onNext(accountBalance);
		responseObserver.onCompleted();
	}

	@Override
	public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
		RequestValidator.validateAccount(request.getAccountNumber())
				.or(() -> RequestValidator.validateAmount(request.getAmount()))
				.or(() -> RequestValidator.hasSufficientBalance(AccountRepo.getBalance(request.getAccountNumber()), request.getAmount()))
				.ifPresentOrElse(responseObserver::onError, () -> sendMoney(request, responseObserver));
	}

	private void sendMoney(WithdrawRequest request, StreamObserver<org.example.sec10.Money> responseObserver) {
		var accountNumber = request.getAccountNumber();
		var amount = request.getAmount();
		var accountBalance = AccountRepo.getBalance(accountNumber);
		if (amount > accountBalance) {
			responseObserver.onCompleted();
			return;
		}

		for (int i = 0; i < amount / 10; i++) {
			try {
				var money = Money.newBuilder().setAmount(10).build();
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
