package pojo;

import util.HashUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 区块链
 * @author fanbin
 * @date 2020/2/6
 */
public class BlockChain {

    /**
     * 难度（合法哈希值的要求：前 n 位的数字是 0）
     */
    private static final int DIFFICULTY = 3;
    private static final char ZERO = '0';

    /**
     * 区块列表
     */
    private List<Block> blockChain;

    /**
     * 构造方法
     */
    public BlockChain() {
        // 初始化区块列表
        blockChain = new ArrayList<Block>();
        // 向区块列表中添加第一个区块，也就是「创世区块」
        blockChain.add(createGenesisBlock());
    }

    /**
     * 创建创世区块
     * @return 创世区块
     */
    private Block createGenesisBlock() {
        Block block = new Block();
        block.setIndex(0);
        block.setTimestamp(1578000000000L);
        // block.setHash("000c81784691c7bb8bacab6affb23312fd707bf0463e9afc217ce1a32ab3a4aa");
        block.setPreviousHash("0");
        block.setData("GENESIS BLOCK");
        // block.setNonce(1545);
        calculateNonceAndHash(block);
        return block;
    }

    /**
     * 挖矿（计算区块的 Nonce 和 Hash）
     * @param block 需要进行计算的区块
     */
    private void calculateNonceAndHash(Block block) {
        // nonce 从 0 开始
        int nonce = 0;
        // 循环计算区块的 hash，直到计算出合法的 hash 为止
        // 将合法的 hash 和 nonce 值，赋值给区块对应的属性
        while (true) {
            // 将当前的 Nonce 值赋给 block 对应的属性
            block.setNonce(nonce);
            System.out.println("当前的 Nonce 值是：" + nonce);
            // 获取原像字符串并计算哈希值
            String hash = HashUtil.getSHA256(block.originalString());
            System.out.println("计算出的 Hash 值是：" + hash);
            // 判断当前的 hash 值是否合法
            // 如果合法，将 hash 值赋给 block 对应的属性，退出循环
            if (isValidHash(hash)) {
                System.out.println("找到了合法的 hash 值");
                block.setHash(hash);
                break;
            }
            // nonce 增加 1
            nonce++;
        }
    }

    /**
     * 验证哈希值是否合法
     * @param hash 哈希值
     * @return 验证结果
     */
    private boolean isValidHash(String hash) {
        if (hash == null) {
            return false;
        }
        for (int i = 0; i < hash.length(); i++) {
            if (hash.charAt(i) != ZERO) {
                return i >= DIFFICULTY;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "BlockChain{" +
                "blockChain=" + blockChain +
                '}';
    }

}
