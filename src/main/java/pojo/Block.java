package pojo;

import com.google.common.base.Objects;

/**
 * 区块
 * @author fanbin
 * @date 2020/2/5
 */
public class Block {

    /**
     * 索引
     */
    private int index;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 哈希值
     */
    private String hash;

    /**
     * 上一区块的哈希值
     */
    private String previousHash;

    /**
     * 数据
     */
    private String data;

    /**
     * NONCE
     */
    private int nonce;

    public Block() {

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    @Override
    public String toString() {
        return "Block{" +
                "index=" + index +
                ", timestamp=" + timestamp +
                ", ='" + hash + '\'' +
                ", previousHash='" + previousHash + '\'' +
                ", data='" + data + '\'' +
                ", nonce=" + nonce +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        // 如果两个指向同一个对象，则相等
        if (this == o) {
            return true;
        }
        // 如果比较对象为空，则判定为不相等
        if (o == null) {
            return false;
        }
        // 如果比较对象和当前对象不是同一个类型，则判定为不相等
        if (this.getClass() != o.getClass()) {
            return false;
        }
        // 强制转换类型，并且对比它们的每一个属性是否相等
        Block block = (Block) o;
        return block.index == this.index &&
                block.timestamp == this.timestamp &&
                block.hash.equals(this.hash) &&
                block.previousHash.equals(previousHash) &&
                block.data.equals(block.data) &&
                block.nonce == this.nonce;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(index, timestamp, hash, previousHash, data, nonce);
    }
}

/*

说明：
public 是访问修饰符：表示当前修饰的变量、方法的访问权限

 */
