package msg;

/**
 * 消息类型常量
 * @author fanbin
 * @date 2020/2/10
 */
public class Constant {

    private Constant() {

    }

    /**
     * 请求最新区块
     */
    public static final int REQ_LATEST_BLOCK = 0;

    /**
     * 请求整个区块链
     */
    public static final int REQ_BLOCK_CHAIN = 1;

    /**
     * 响应区块数据
     */
    public static final int RES_BLOCKS = 2;


}
