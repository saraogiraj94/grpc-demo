package org.example.commons;

import org.example.sec06.BankService;

public class Demo {

	public static void main(String[] args) {
		GrpcServer.create(new BankService()).start().await();
	}
}
