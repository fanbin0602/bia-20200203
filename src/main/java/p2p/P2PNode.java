package p2p;

import com.alibaba.fastjson.JSON;
import msg.Constant;
import msg.Message;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;
import pojo.Block;
import pojo.BlockChain;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * P2P 节点
 * @author fanbin
 * @date 2020/2/10
 */
public class P2PNode {

    /**
     * 本地区块链
     */
    private BlockChain blockChain;

    /**
     * 与本地节点连接的其他节点的信息
     */
    List<WebSocket> sockets;

    public P2PNode(BlockChain blockChain) {
        //
        this.blockChain = blockChain;
        // 初始化节点列表
        sockets = new ArrayList<WebSocket>();
    }

    public BlockChain getBlockChain() {
        return blockChain;
    }

    public List<WebSocket> getSockets() {
        return sockets;
    }

    /**
     * 初始化节点
     * @param port 指定服务端的端口号
     */
    public void initNode(int port) {

        WebSocketServer server = new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                System.out.println("server:连接成功，对方地址是：" + conn.getRemoteSocketAddress());
                sockets.add(conn);
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                System.out.println("server:断开连接，对方地址是：" + conn.getRemoteSocketAddress());
                sockets.remove(conn);
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                System.out.println("server:收到消息，发送方是：" + conn.getRemoteSocketAddress());
                System.out.println("server:收到消息，内容是：" + message);
                // 处理接收到的消息
                handleMessage(conn, JSON.parseObject(message, Message.class));
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                System.out.println("server:出现错误");
                sockets.remove(conn);
            }

            @Override
            public void onStart() {
                System.out.println("server:启动成功");
            }
        };

        server.start();
        System.out.println("server:服务已经启动，端口号是：" + port);

    }

    /**
     * 创建客户端，并连接到远程节点
     * @param remote 远程节点的地址，格式为：ws://127.0.0.1:7000
     */
    public void connectToNode(String remote) {

        try {
            WebSocketClient client = new WebSocketClient(new URI(remote)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("client:连接成功，对方地址是：" + this.getRemoteSocketAddress());
                    sockets.add(this);
                    // 向服务端发送请求，获取最新的区块
                    this.send(reqLatestBlockMsg());
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("client:收到消息，发送方是：" + this.getRemoteSocketAddress());
                    System.out.println("client:收到消息，内容是：" + message);
                    // 处理接收到的消息
                    handleMessage(this, JSON.parseObject(message, Message.class));
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("client:断开连接，对方地址是：" + this.getRemoteSocketAddress());
                    sockets.remove(this);
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("client:出现错误");
                    sockets.remove(this);
                }
            };
            client.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * 处理接收到的消息
     * @param socket 远程连接
     * @param message 收到的消息
     */
    private void handleMessage(WebSocket socket, Message message) {
        System.out.println("handleMessage 开始处理收到的消息：" + JSON.toJSONString(message));
        switch (message.getType()) {
            case Constant.REQ_LATEST_BLOCK:
                // 收到请求最新区块的消息
                socket.send(resLatestBlockMsg());
                break;
            case Constant.REQ_BLOCK_CHAIN:
                //  收到请求整个区块列表的消息
                socket.send(resBlockChainMsg());
                break;
            case Constant.RES_BLOCKS:
                //  收到对方发送过来的区块数据（最新区块或整个区块列表）
                handleBlockResponse(message.getData());
                break;
        }
    }

    /**
     * 处理收到的区块数据的消息
     * @param data
     */
    private void handleBlockResponse(String data) {
        // 把收到的消息内容解析成
        List<Block> blocksReceived = JSON.parseArray(data, Block.class);
        // 获取收到的区块数据里面的最新区块
        Block latestBlockReceived = blocksReceived.get(blocksReceived.size() - 1);
        // 获取本地节点最新区块
        Block latestBlock = blockChain.getLastBlock();
        // 比较双方最新区块的索引，如果远程的最新区块索引更大，做下一步处理
        if (latestBlockReceived.getIndex() > latestBlock.getIndex()) {
            // 判断远程的最新区块是否可以追加到本地区块链末尾，直接追加
            if (latestBlock.getHash().equals(latestBlockReceived.getPreviousHash())
                    && latestBlock.getIndex() + 1 == latestBlockReceived.getIndex()) {
                System.out.println("在本地区块链末尾追加接收到的最新区块");
                blockChain.addBlock(latestBlockReceived);
                // 广播最新区块
                broadcast(resLatestBlockMsg());
            // 判断远程的区块数据是一个区块还是整个区块链
            } else if (blocksReceived.size() == 1) {
                // 如果是一个区块，则获取远程节点的整个区块链
                System.out.println("向其他节点请求整个区块列表");
                // 向其他节点请求整个区块列表
                broadcast(reqBlockChainMsg());
            } else {
                // 否则，替换本地区块链数据
                System.out.println("替换本地的区块数据");
                blockChain.replaceChain(blocksReceived);
                // 广播最新区块
                broadcast(resLatestBlockMsg());
            }
        } else {
            // 否则不处理
            System.out.println("对方的区块链不必本地的更长，因此，不作处理");
        }

    }

    /**
     * 广播消息
     * @param message 消息内容
     */
    private void broadcast(String message) {
        for (WebSocket socket : sockets) {
            socket.send(message);
        }
    }

    public void broadcastLatestBlock() {
        broadcast(resLatestBlockMsg());
    }

    /**
     * 生成消息文本：请求最新区块
     * @return
     */
    private String reqLatestBlockMsg() {
        return JSON.toJSONString(new Message(Constant.REQ_LATEST_BLOCK));
    }

    /**
     * 生成消息文本：请求区块列表
     * @return
     */
    private String reqBlockChainMsg() {
        return JSON.toJSONString(new Message(Constant.REQ_BLOCK_CHAIN));
    }

    /**
     * 生成消息文本：响应最新区块
     * @return
     */
    private String resLatestBlockMsg() {
        Block[] blocks = {this.blockChain.getLastBlock()};
        String data = JSON.toJSONString(blocks);
        return JSON.toJSONString(new Message(Constant.RES_BLOCKS, data));
    }

    /**
     * 生成消息文本：响应区块列表
     * @return
     */
    private String resBlockChainMsg() {
        String data = JSON.toJSONString(this.blockChain.getBlockChain());
        return JSON.toJSONString(new Message(Constant.RES_BLOCKS, data));
    }

    public static void main(String[] args) throws InterruptedException {
        // 创建区块链对象
        BlockChain bc = new BlockChain();
        bc.addBlock(bc.generateNextBlock("Hello"));
        // 创建 P2P 节点
        P2PNode p2p = new P2PNode(bc);
        // 初始化节点
        // p2p.initNode(7000);

        // 等待一秒钟
        // Thread.sleep(1000);

        // 创建客户端并向服务端发起连接
        // p2p.connectToNode("ws://127.0.0.1:7000");

        System.out.println(p2p.reqLatestBlockMsg());
        System.out.println(p2p.reqBlockChainMsg());
        System.out.println(p2p.resLatestBlockMsg());
        System.out.println(p2p.resBlockChainMsg());
    }

}
