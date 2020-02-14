package http;

import com.alibaba.fastjson.JSON;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.java_websocket.WebSocket;
import p2p.P2PNode;
import pojo.Block;
import pojo.BlockChain;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP Web 服务
 * @author fanbin
 * @date 2020/2/12
 */
public class HttpServer {

    /**
     * 区块链数据
     */
    private BlockChain bc;

    /**
     * P2P 节点
     */
    private P2PNode p2p;

    /**
     * 构造方法
     * @param p2p
     */
    public HttpServer(P2PNode p2p) {
        this.p2p = p2p;
        this.bc = p2p.getBlockChain();
    }

    /**
     * 初始化 HTTP 服务
     * @param port 端口号
     */
    public void initServer(int port) {
        try {
            Server server = new Server(port);

            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/block-chain/");
            server.setHandler(context);

            context.addServlet(new ServletHolder(new HelloServlet()), "/hello");

            // 查询区块列表
            context.addServlet(new ServletHolder(new BlocksServlet()), "/blocks");
            // 生成新区块
            context.addServlet(new ServletHolder(new MineBlockServlet()), "/mineBlock");
            // 查询节点列表
            context.addServlet(new ServletHolder(new PeersServlet()), "/peers");
            // 添加节点
            context.addServlet(new ServletHolder(new AddPeerServlet()), "/addPeer");

            System.out.println("监听 HTTP 端口号：" + port);
            server.start();
            server.join();

        } catch (Exception ex) {
            System.out.println("初始化 HTTP 服务错误" + ex.getMessage());
        }

    }

    private class HelloServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(JSON.toJSONString("Hello, world!"));
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("UTF-8");
            String name = req.getParameter("name");
            resp.getWriter().println(JSON.toJSONString("Hello, " + name + "!"));
        }
    }

    // 1. 查询区块列表
    private class BlocksServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(JSON.toJSONString(bc.getBlockChain()));
        }
    }

    // 2. 生成新区块（挖矿）
    private class MineBlockServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doPost(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("UTF-8");
            // 获取新区块要保存的数据
            String data = req.getParameter("data");
            // 生成新区块
            Block newBlock = bc.generateNextBlock(data);
            bc.addBlock(newBlock);
            // 广播最新的区块
            p2p.broadcastLatestBlock();
            // 把生成的新区块返回
            resp.getWriter().println(JSON.toJSONString(newBlock));
        }
    }

    // 3. 查询节点列表
    private class PeersServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("UTF-8");
            List<Map<String, String>> wsList = new ArrayList<Map<String, String>>();
            for (WebSocket socket : p2p.getSockets()) {
                InetSocketAddress address = socket.getRemoteSocketAddress();
                Map<String, String> wsMap = new HashMap<String, String>();
                wsMap.put("remoteHost", address.getHostName());
                wsMap.put("remotePort", String.valueOf(address.getPort()));
                wsList.add(wsMap);
            }
            resp.getWriter().println(JSON.toJSONString(wsList));
        }
    }

    // 4. 添加节点
    private class AddPeerServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doPost(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("UTF-8");
            String peer = req.getParameter("peer");
            p2p.connectToNode(peer);
            resp.getWriter().println(JSON.toJSONString("ok"));
        }
    }

}
