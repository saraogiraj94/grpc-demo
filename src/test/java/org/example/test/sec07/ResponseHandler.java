package org.example.test.sec07;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.stub.StreamObserver;
import org.example.models.Output;
import org.example.models.RequestSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ResponseHandler implements StreamObserver<Output> {

	public static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);
	private final CountDownLatch countDownLatch = new CountDownLatch(1);
	private int size;
	private StreamObserver<RequestSize> requestSizeStreamObserver;

	@Override
	public void onNext(Output output) {
		this.size = size - 1;
		log.info("received new message value {}", output.getValue());
		Uninterruptibles.sleepUninterruptibly(ThreadLocalRandom.current().nextInt(50, 200), TimeUnit.MILLISECONDS);
		if (size == 0) {
			log.info("-------------------------------");
			this.request(3);
		}
	}

	@Override
	public void onError(Throwable throwable) {

	}

	@Override
	public void onCompleted() {
		//When server has no more data in that case we need to client connection as well
		this.requestSizeStreamObserver.onCompleted();
	}

	public void setRequestSizeStreamObserver(StreamObserver<RequestSize> requestSizeStreamObserver) {
		this.requestSizeStreamObserver = requestSizeStreamObserver;
	}

	private void request(int size) {
		log.info("req size {}", size);
		this.size = size;
		this.requestSizeStreamObserver.onNext(RequestSize.newBuilder().setSize(size).build());
	}

	public void await() {
		try {
			this.countDownLatch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void start() {
		this.request(3);
	}
}
