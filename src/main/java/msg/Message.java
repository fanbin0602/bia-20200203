package msg;

/**
 * 消息实体
 * @author fanbin
 * @date 2020/2/10
 */
public class Message {

    /**
     * 消息类型
     * 0 - 请求最新区块
     * 1 - 请求整个区块链
     * 2 - 响应区块数据（响应最新区块、响应整个区块链、广播最新区块）
     */
    private int type;

    /**
     * 消息内容
     */
    private String data;

    public Message(int type) {
        this.type = type;
    }

    public Message(int type, String data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
