package org.example.commons;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class GrpcServer {

	private static final Logger log = LoggerFactory.getLogger(GrpcServer.class);

	private final Server server;

	public GrpcServer(Server server) {this.server = server;}

	public static void main(String[] args) {

	}

	public static GrpcServer create(BindableService... services) {
		return create(6565, services);
	}

	public static GrpcServer create(int port, BindableService... services) {
		var builder = ServerBuilder.forPort(port);
		Arrays.asList(services).forEach(builder::addService);
		return new GrpcServer(builder.build());
	}

	public GrpcServer start() {
		var services = server.getServices().stream().map(ServerServiceDefinition::getServiceDescriptor).map(ServiceDescriptor::getName).toList();
		try {
			server.start();
			log.info("server started with services {} on port {}", services, server.getPort());
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void await() {
		try {
			server.awaitTermination();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		server.shutdownNow();
		log.info("server stopped");
	}
}
