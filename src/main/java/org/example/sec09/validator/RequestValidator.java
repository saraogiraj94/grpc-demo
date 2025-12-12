package org.example.sec09.validator;

import io.grpc.Status;

import java.util.Optional;

public class RequestValidator {

	public static Optional<Status> validateAccount(int accountNumber) {
		if (accountNumber > 0 && accountNumber < 11) {
			return Optional.empty();
		}
		return Optional.of(Status.INVALID_ARGUMENT.withDescription("acct num should be between 1 and 10"));
	}

	public static Optional<Status> validateAmount(int amount) {
		if (amount > 0 && amount % 10 == 0) {
			return Optional.empty();
		}
		return Optional.of(Status.INVALID_ARGUMENT.withDescription("request amount should be in multiple of 10s"));
	}

	public static Optional<Status> hasSufficientBalance(int accountBalance, int amount) {
		if (amount <= accountBalance) {
			return Optional.empty();
		}
		return Optional.of(Status.FAILED_PRECONDITION.withDescription("request amount should be less then balance"));
	}

}
