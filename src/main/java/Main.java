import http.HttpServer;
import p2p.P2PNode;
import pojo.Block;
import pojo.BlockChain;
import util.HashUtil;

/**
 * @author fanbin
 * @date 2020/2/5
 */
public class Main {

    public static void main(String[] args) {
        // 8080 7000
        if (args.length == 2) {
            int httpPort = Integer.valueOf(args[0]);
            int wsPort = Integer.valueOf(args[1]);

            BlockChain bc = new BlockChain();
            P2PNode p2p = new P2PNode(bc);
            p2p.initNode(wsPort);
            HttpServer http = new HttpServer(p2p);
            http.initServer(httpPort);
        } else {
            System.out.println("参数错误");
        }

    }

}
