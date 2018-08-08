package com.thrift;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import thrift.generated.PersonService;

public class ThriftServer {

    public static void main(String[] args) throws Exception {

        TNonblockingServerSocket serverSocket = new TNonblockingServerSocket(8899);
        THsHaServer.Args arg = new THsHaServer.Args(serverSocket).minWorkerThreads(2).maxWorkerThreads(2);

        PersonService.Processor<PersonServiceImpl> processor = new PersonService.Processor<>(new PersonServiceImpl());

        // 压缩的传输协议
        arg.protocolFactory(new TCompactProtocol.Factory());
        // 传输方式
        arg.transportFactory(new TFramedTransport.Factory());
        arg.processorFactory(new TProcessorFactory(processor));

        TServer server = new THsHaServer(arg);

        System.out.println("server started");

        // 死循环
        server.serve();

    }


}
