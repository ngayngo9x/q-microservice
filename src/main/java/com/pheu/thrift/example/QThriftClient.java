package com.pheu.thrift.example;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import com.pheu.service.client.QServiceDiscover;
import com.pheu.service.client.QServiceDiscoverImpl;
import com.pheu.service.client.ServiceDiscoveryException;
import com.pheu.thrift.client.QFailoverPoolProvider;
import com.pheu.thrift.client.QProtocolProvider;
import com.pheu.thrift.client.QRoundRobinSelectorStrategy;
import com.pheu.thrift.client.QThriftExec;
import com.pheu.thrift.client.QThriftProvider;
import com.pheu.thrift.client.QThriftProviderImpl;

public class QThriftClient {

	public static void main(String[] args) throws ServiceDiscoveryException {

		QServiceDiscover<Void> discover = new QServiceDiscoverImpl.Builder<Void>().connectionTimeout(1000)
				.sessionTimeout(1000).connectString("localhost:2181").serviceName("qthriftserverice").build();
		discover.start();

		String message = "Quy";

		QThriftProvider<Void> thriftProvider = new QThriftProviderImpl.Builder<Void>()
				.poolProvider(new QFailoverPoolProvider<Void>(discover)).serverInfoProvider(discover)
				.protocalProvider(new QProtocolProvider() {
					@Override
					public TProtocol getProtocol(TTransport transport) {
						return new TCompactProtocol(transport);
					}
				}).selectorStrategy(new QRoundRobinSelectorStrategy()).build();

		QThriftExec<String, Void> qThriftExec = new QThriftExec<String, Void>(thriftProvider) {
			@Override
			public String call(TProtocol protocol) throws TException {
				TestThriftService.Client client = new TestThriftService.Client(protocol);
				return client.echo(message);
			}
		};

		try {
			for (int i = 1; i <= 10; i++) {
				System.out.println(qThriftExec.exec());
			}
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		discover.close();

	}

}
