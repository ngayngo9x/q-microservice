package com.pheu.thrift.client;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

public interface QProtocolProvider {

	TProtocol getProtocol(TTransport transport);

}
