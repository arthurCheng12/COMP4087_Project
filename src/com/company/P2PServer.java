
import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dce.blockchain.web.service.P2PService;

public class P2PServer {

	P2PService p2pService;

	public void initP2PServer(int port) {
		WebSocketServer socketServer = new WebSocketServer(new InetSocketAddress(port)) {

			public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
				p2pService.getSockets().add(webSocket);
			}

			public void onClose(WebSocket webSocket, int i, String s, boolean b) {
				p2pService.getSockets().remove(webSocket);
				System.out.println("connection closed to address:" + webSocket.getRemoteSocketAddress());
			}

			public void onMessage(WebSocket webSocket, String msg) {

				p2pService.handleMessage(webSocket, msg, p2pService.getSockets());
			}


			public void onError(WebSocket webSocket, Exception e) {
				p2pService.getSockets().remove(webSocket);
				System.out.println("connection failed to address:" + webSocket.getRemoteSocketAddress());
			}

			public void onStart() {

			}

		};
		socketServer.start();
		System.out.println("listening websocket p2p port on: " + port);
	}

}
