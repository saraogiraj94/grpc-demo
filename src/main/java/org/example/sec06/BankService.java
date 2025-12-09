package org.example.sec06;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.example.models.AccountBalance;
import org.example.models.AllAccountBalance;
import org.example.models.BalanceCheckRequest;
import org.example.models.BankServiceGrpc;
import org.example.models.DepositRequest;
import org.example.models.Money;
import org.example.models.WithdrawRequest;
import org.example.sec06.repository.AccountRepo;
import org.example.sec06.requestHandlers.DepositRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

	private static final Logger log = LoggerFactory.getLogger(BankService.class);

	@Override
	public void getAccountBalance(BalanceCheckRequest request, StreamObserver<AccountBalance> responseObserver) {
		var accountNumber = request.getAccountNumber();
		var balance = AccountRepo.getBalance(accountNumber);

		var accountBalance = AccountBalance.newBuilder().setAccountNumber(accountNumber).setBalance(balance).build();
		responseObserver.onNext(accountBalance);
		responseObserver.onCompleted();
	}

	@Override
	public void getAllAccounts(Empty request, StreamObserver<AllAccountBalance> responseObserver) {
		Map<Integer, Integer> allAccounts = AccountRepo.getAllAccounts();
		List<AccountBalance> list = allAccounts.entrySet()
				.stream()
				.map(e -> AccountBalance.newBuilder().setAccountNumber(e.getKey()).setBalance(e.getValue()).build())
				.toList();
		var allAccountBalance = AllAccountBalance.newBuilder().addAllAccountsBalance(list).build();
		responseObserver.onNext(allAccountBalance);
		responseObserver.onCompleted();
	}

	@Override
	public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
		var accountNumber = request.getAccountNumber();
		var amount = request.getAmount();
		var accountBalance = AccountRepo.getBalance(accountNumber);
		if (amount > accountBalance) {
			responseObserver.onCompleted();
			return;
		}

		for (int i = 0; i < amount / 10; i++) {
			var money = Money.newBuilder().setAmount(10).build();
			responseObserver.onNext(money);
			log.info("money sent {}", money);
			AccountRepo.deductAmount(accountNumber, 10);
			Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
		}

		responseObserver.onCompleted();
	}

	//StreamObserver <DepositRequest> is to make server know that multiple request will come and streaming need to be switched on from client side
	@Override
	public StreamObserver<DepositRequest> deposit(StreamObserver<AccountBalance> responseObserver) {
		return new DepositRequestHandler(responseObserver);
	}
}
