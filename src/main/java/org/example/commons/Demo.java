package org.example.commons;

import org.example.sec06.BankService;

public class Demo {

//	public static void main(String[] args) {
//		GrpcServer.create(new BankService()).start().await();
//	}

	public static class BankInstance1 {

		public static void main(String[] args) {
			GrpcServer.create(6565, new BankService()).start().await();
		}
	}

	public static class BankInstance2 {

		public static void main(String[] args) {
			GrpcServer.create(7575, new BankService()).start().await();
		}
	}
}
