package org.example.test.common;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ResponseObserver<T> implements StreamObserver<T> {

	public static final Logger log = LoggerFactory.getLogger(ResponseObserver.class);
	private final List<T> list = Collections.synchronizedList(new ArrayList<>());
	private final CountDownLatch countDownLatch;
	private Throwable throwable;

	private ResponseObserver(int countDown) {
		this.countDownLatch = new CountDownLatch(countDown);
	}

	public static <T> ResponseObserver<T> create() {
		return new ResponseObserver<>(1);
	}

	public static <T> ResponseObserver<T> create(int countDown) {
		return new ResponseObserver<>(countDown);
	}

	@Override
	public void onNext(T t) {
		log.info("received item {}", t);
		this.list.add(t);
	}

	@Override
	public void onError(Throwable throwable) {
		log.info("received error {}", throwable.getMessage());
		this.throwable = throwable;
		this.countDownLatch.countDown();
	}

	@Override
	public void onCompleted() {
		log.info("received completed");
		this.countDownLatch.countDown();
	}

	public void await() {
		try {
			this.countDownLatch.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public List<T> getList() {
		return list;
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
