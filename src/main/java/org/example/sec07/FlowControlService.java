package org.example.sec07;

import io.grpc.stub.StreamObserver;
import org.example.models.FlowControlServiceGrpc;
import org.example.models.Output;
import org.example.models.RequestSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class FlowControlService extends FlowControlServiceGrpc.FlowControlServiceImplBase {

	private static final Logger log = LoggerFactory.getLogger(FlowControlService.class);

	@Override
	public StreamObserver<RequestSize> getMessage(StreamObserver<Output> responseObserver) {
		return new RequestHandler(responseObserver);
	}

	private static class RequestHandler implements StreamObserver<RequestSize> {

		private final StreamObserver<Output> streamObserver;
		private Integer emitted;

		public RequestHandler(StreamObserver<Output> streamObserver) {
			this.streamObserver = streamObserver;
			this.emitted = 0;
		}

		@Override
		public void onNext(RequestSize requestSize) {
			IntStream.rangeClosed((emitted + 1), 100)
					.limit(requestSize.getSize())
					.forEach(i -> streamObserver.onNext(Output.newBuilder().setValue(i).build()));

			emitted = emitted + requestSize.getSize();
			if (emitted >= 100) {
				streamObserver.onCompleted();
			}

		}

		@Override
		public void onError(Throwable throwable) {

		}

		@Override
		public void onCompleted() {
			this.streamObserver.onCompleted();
		}
	}
}
