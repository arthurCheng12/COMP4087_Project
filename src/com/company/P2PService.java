

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.dce.blockchain.web.model.Block;
import com.dce.blockchain.web.model.Message;
import com.dce.blockchain.web.util.BlockCache;
import com.dce.blockchain.web.util.BlockConstant;
import com.dce.blockchain.websocket.P2PClient;
import com.dce.blockchain.websocket.P2PServer;



public class P2PService implements ApplicationRunner {

	BlockService blockService;

	BlockCache blockCache;

	P2PServer p2PServer;

	P2PClient p2PClient;


	public void handleMessage(WebSocket webSocket, String msg, List<WebSocket> sockets) {
		try {
			Message message = JSON.parseObject(msg, Message.class);
			System.out.println("The IP address received is: " +webSocket.getRemoteSocketAddress().getAddress().toString()
					+", the port number is: "+ webSocket.getRemoteSocketAddress().getPort() + "P2P messages: "
			        + JSON.toJSONString(message));
			switch (message.getType()) {
			//The client requests a query for the latest block : 1
			case BlockConstant.QUERY_LATEST_BLOCK:
				write(webSocket, responseLatestBlockMsg());
				//The server calls the method to return the latest block: 2
				break;
			//Receive the latest block returned by the server : 2
			case BlockConstant.RESPONSE_LATEST_BLOCK:
				handleBlockResponse(message.getData(), sockets);
				break;
			//The client requests to query the entire blockchain : 3
			case BlockConstant.QUERY_BLOCKCHAIN:
				write(webSocket, responseBlockChainMsg());
				//The server invocation method returns the latest block : 4
				break;
			//Receive the entire blockchain information directly sent by other nodes : 4
			case BlockConstant.RESPONSE_BLOCKCHAIN:
				handleBlockChainResponse(message.getData(), sockets);
				break;
			}
		} catch (Exception e) {
			System.out.println("Processing IP address is: " +webSocket.getRemoteSocketAddress().getAddress().toString()
				+", the port number is: "+ webSocket.getRemoteSocketAddress().getPort() + "P2P message error: "
				+ e.getMessage());
		}
	}


	public synchronized void handleBlockResponse(String blockData, List<WebSocket> sockets) {
		//Deserialize the latest block information from other nodes
		Block latestBlockReceived = JSON.parseObject(blockData, Block.class);
		//The latest block of the current node
		Block latestBlock = blockCache.getLatestBlock();
		
		if (latestBlockReceived != null) {
			if(latestBlock != null) {
				//If the received block height is much larger than the local block height
				if(latestBlockReceived.getIndex() > latestBlock.getIndex() + 1) {
					broatcast(queryBlockChainMsg());
					System.out.println("Re-query the entire blockchain on all nodes");
				}else if (latestBlockReceived.getIndex() > latestBlock.getIndex() && 
						latestBlock.getHash().equals(latestBlockReceived.getPreviousHash())) {
					if (blockService.addBlock(latestBlockReceived)) {
						broatcast(responseLatestBlockMsg());
					}
					System.out.println("Add the newly received block to the local blockchain");
				}
			}else if(latestBlock == null) {
				broatcast(queryBlockChainMsg());
				System.out.println("Re-query the entire blockchain on all nodes");
			}
		}
	}
	

	public synchronized void handleBlockChainResponse(String blockData, List<WebSocket> sockets) {
		//Deserialized to obtain the entire blockchain information of other nodes
		List<Block> receiveBlockchain = JSON.parseArray(blockData, Block.class);
		if(!CollectionUtils.isEmpty(receiveBlockchain) && blockService.isValidChain(receiveBlockchain)) {
			//Sort blocks by block index first
			Collections.sort(receiveBlockchain, new Comparator<Block>() {
				public int compare(Block block1, Block block2) {
					return block1.getIndex() - block2.getIndex();
				}
			});
			
			//The latest block of other nodes
			Block latestBlockReceived = receiveBlockchain.get(receiveBlockchain.size() - 1);
			//The latest block of the current node
			Block latestBlock = blockCache.getLatestBlock();
			
			if(latestBlock == null) {
				//Replace the local blockchain
				blockService.replaceChain(receiveBlockchain);
			}else {
				//Other node blockchains that are longer than the current node process the current node's block chain
				if (latestBlockReceived.getIndex() > latestBlock.getIndex()) {
					if (latestBlock.getHash().equals(latestBlockReceived.getPreviousHash())) {
						if (blockService.addBlock(latestBlockReceived)) {
							broatcast(responseLatestBlockMsg());
						}
						System.out.println("Add the newly received block to the local blockchain");
					} else {
						//Replace local short chains with long ones
						blockService.replaceChain(receiveBlockchain);
					}
				}
			}
		}
	}
	

	public void broatcast(String message) {
		List<WebSocket> socketsList = this.getSockets();
		if (CollectionUtils.isEmpty(socketsList)) {
			return;
		}
		System.out.println("====== Start of the broadcast: ");
		for (WebSocket socket : socketsList) {
			this.write(socket, message);
		}
		System.out.println("====== End of the broadcast: ");
	}
	

	public void write(WebSocket ws, String message) {
		System.out.println("Send to IP address: " +ws.getRemoteSocketAddress().getAddress().toString()
			+ ", the port number is: "+ws.getRemoteSocketAddress().getPort() + " P2P messages:" + message);
		ws.send(message);
	}


	public String queryBlockChainMsg() {
		return JSON.toJSONString(new Message(BlockConstant.QUERY_BLOCKCHAIN));
	}
	

	public String responseBlockChainMsg() {
		Message msg = new Message();
		msg.setType(BlockConstant.RESPONSE_BLOCKCHAIN);
		msg.setData(JSON.toJSONString(blockCache.getBlockChain()));
		return JSON.toJSONString(msg);
	}


	public String queryLatestBlockMsg() {
		return JSON.toJSONString(new Message(BlockConstant.QUERY_LATEST_BLOCK));
	}
	

	public String responseLatestBlockMsg() {
		Message msg = new Message();
		msg.setType(BlockConstant.RESPONSE_LATEST_BLOCK);
		Block b = blockCache.getLatestBlock();
		msg.setData(JSON.toJSONString(b));
		return JSON.toJSONString(msg);
	}
	
	public List<WebSocket> getSockets(){
		return blockCache.getSocketsList();
	}

	public void run(ApplicationArguments args) throws Exception {
		p2PServer.initP2PServer(blockCache.getP2pport());
		p2PClient.connectToPeer(blockCache.getAddress());
		System.out.println("-----Port number-----"+blockCache.getP2pport());
		System.out.println("-----Node address-----"+blockCache.getAddress());
		
	}
	
}
