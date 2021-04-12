
import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dce.blockchain.web.service.P2PService;


public class P2PClient {

	P2PService p2pService;

	public void connectToPeer(String addr) {
		try {
			final WebSocketClient socketClient = new WebSocketClient(new URI(addr)) {

				public void onOpen(ServerHandshake serverHandshake) {
					//The client sends a request to query the latest block
					p2pService.write(this, p2pService.queryLatestBlockMsg());
					p2pService.getSockets().add(this);
				}


				public void onMessage(String msg) {
					p2pService.handleMessage(this, msg, p2pService.getSockets());
				}


				public void onClose(int i, String msg, boolean b) {
					p2pService.getSockets().remove(this);
					System.out.println("connection closed");
				}


				public void onError(Exception e) {
					p2pService.getSockets().remove(this);
					System.out.println("connection failed");
				}
			};
			socketClient.connect();
		} catch (URISyntaxException e) {
			System.out.println("p2p connect is error:" + e.getMessage());
		}
	}

}
