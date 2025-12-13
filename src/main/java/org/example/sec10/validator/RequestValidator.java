package org.example.sec10.validator;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import org.example.sec10.ErrorMessage;
import org.example.sec10.ValidationCode;

import java.util.Optional;

public class RequestValidator {

	public static final Metadata.Key<ErrorMessage> ERROR_MESSAGE_KEY = ProtoUtils.keyForProto(ErrorMessage.getDefaultInstance());

	public static Optional<StatusRuntimeException> validateAccount(int accountNumber) {
		if (accountNumber > 0 && accountNumber < 11) {
			return Optional.empty();
		}
		var metadata = toMetadata(ValidationCode.INVALID_ACCOUNT);
		return Optional.of(Status.INVALID_ARGUMENT.asRuntimeException(metadata));
	}

	public static Optional<StatusRuntimeException> validateAmount(int amount) {
		if (amount > 0 && amount % 10 == 0) {
			return Optional.empty();
		}
		var metadata = toMetadata(ValidationCode.INVALID_AMOUNT);
		return Optional.of(Status.INVALID_ARGUMENT.asRuntimeException(metadata));
	}

	public static Optional<StatusRuntimeException> hasSufficientBalance(int accountBalance, int amount) {
		if (amount <= accountBalance) {
			return Optional.empty();
		}
		var metadata = toMetadata(ValidationCode.INVALID_AMOUNT);
		return Optional.of(Status.FAILED_PRECONDITION.asRuntimeException(metadata));
	}

	private static Metadata toMetadata(ValidationCode validationCode) {
		var metadata = new Metadata();
		var value = ErrorMessage.newBuilder().setValidationCode(validationCode).build();
		metadata.put(ERROR_MESSAGE_KEY, value);
		var stringKey = Metadata.Key.of("desc", Metadata.ASCII_STRING_MARSHALLER);
		metadata.put(stringKey, validationCode.toString());
		return metadata;
	}

}
