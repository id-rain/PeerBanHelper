//package dev.foxgirl.torrent;
//
//import com.ghostchu.peerbanhelper.util.ByteUtil;
//import dev.foxgirl.torrent.client.*;
//import dev.foxgirl.torrent.metainfo.InvalidBencodeException;
//import dev.foxgirl.torrent.util.Hash;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.AsynchronousSocketChannel;
//import java.util.concurrent.ExecutionException;
//
//public class Main {
//    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, InvalidBencodeException {
//
//        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
//        System.setProperty(org.slf4j.impl.SimpleLogger.SHOW_DATE_TIME_KEY, "false");
//        System.setProperty(org.slf4j.impl.SimpleLogger.SHOW_THREAD_NAME_KEY, "false");
//        System.setProperty(org.slf4j.impl.SimpleLogger.SHOW_LOG_NAME_KEY, "false");
//
//
//        var client = new Client(Identity.generateDefault(new InetSocketAddress(InetAddress.getLocalHost(), 18009)));
//        //var swarm = client.createSwarm(new Info());
//
//        var peerAddress = new InetSocketAddress(InetAddress.getByName("218.10.235.3"), 40499);
//        var peerChannel = AsynchronousSocketChannel.open();
//        var peer = new Peer(client, peerChannel);
//        peerChannel.connect(peerAddress).get();
//        peer.establishOutgoing(peerAddress, Hash.of(ByteUtil.hexToByteArray("292f44024cf08342b4a0d48b18a78a8daabbf881"))).get();
//        peer.isReady = true;
//        //Thread.sleep(4000);
//        //peer.getProtocol().send(new MessageImpl(MessageType.HAVE_ALL));
//        //peer.getProtocol().send(new MessageImpl(MessageType.HAVE, ByteBuffer.wrap(new byte[]{0}),1));
//        Thread.sleep(4000);
//        System.out.println("Send HAVE_NONE");
//        peer.getProtocol().send(new MessageImpl(MessageType.HAVE_NONE));
//        peer.getProtocol().send(new MessageImpl(MessageType.UNCHOKE));
//        peer.getProtocol().send(new MessageImpl(MessageType.INTERESTED));
//        Thread.sleep(3000);
//        // peer.getProtocol().send(new MessageImpl(MessageType.REQUEST, ByteBuffer.wrap()));
//        System.out.println("peer: "+peer);
//        System.out.println("lastIncomingMessageTime: ${peer.protocol.lastIncomingMessageTime}");
//        System.out.println("lastOutgoingMessageTime: ${peer.protocol.lastOutgoingMessageTime}");
//        System.out.println("Peer choking:" +peer.isPeerChoking());
//        peer.getProtocol().send(new MessageImpl(MessageType.REQUEST, ByteBuffer.allocate(12).putInt(2).putInt(0).putInt(16 * 1024).flip()));
//        while (true){
//            Thread.sleep(3000);
//            peer.getProtocol().send(new MessageImpl(MessageType.INTERESTED));
//            System.out.println("Peer choking:" +peer.isPeerChoking());
//        }
//    /*
//    Thread.sleep(2000)
//
//    peer.setChoking(false).get()
//    peer.setInterested(true).get()
//    */
////
////        Thread.sleep(20 * 1000);
////
////        peer.close();
////        DefaultExecutors.shutdown();
//    }
//}
