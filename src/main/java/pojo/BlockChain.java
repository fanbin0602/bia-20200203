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

    public List<Block> getBlockChain() {
        return blockChain;
    }

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
        Block block = new Block(0, 1578000000000L, "0", "GENESIS BLOCK");
        calculateNonceAndHash(block);
        return block;
    }

    /**
     * 生成下一个区块
     * @param data 新区块中的数据
     * @return 新区块
     */
    public Block generateNextBlock(String data) {
        // 获取当前区块列表中最新的区块（也就是列表里面最后一个区块）
        Block lastBlock = getLastBlock();
        // 索引：上一区块的索引 + 1
        // 时间戳：取当前的系统时间
        // 上一区块的 hash 值：从上一个区块中取它的 hash 属性的值
        // 数据：方法传入的参数

        // 创建一个 Block 对象，把以上四个值赋给响应的属性
        Block block = new Block(lastBlock.getIndex() + 1,
                System.currentTimeMillis(),
                lastBlock.getHash(),
                data);
        // 挖矿（计算 hash 值和 nonce）
        calculateNonceAndHash(block);
        // 返回 block
        return block;
    }

    /**
     * 向区块列表中追加新的区块
     * @param block 要被添加的区块
     */
    public void addBlock(Block block) {
        // 先判断新区块的合法性，再添加
        if (isValidBlock(block, getLastBlock())) {
            blockChain.add(block);
        }
    }

    /**
     * 验证区块的合法性
     * @param block 待验证的区块
     * @param previousBlock 前一个区块
     * @return 是否合法
     */
    private boolean isValidBlock(Block block, Block previousBlock) {
        // 索引：必须是当前最新区块的索引 + 1
        if (block.getIndex() != (previousBlock.getIndex() + 1)) {
            System.out.println("索引错误");
            return false;
        }
        // 时间戳：时间戳必须大于当前最新区块的时间戳
        if (block.getTimestamp() <= previousBlock.getTimestamp()) {
            System.out.println("时间戳错误");
            return false;
        }
        // 前一区块的 hash 值：必须与当前区块列表中最新区块的 hash 值相同
        if (!block.getPreviousHash().equals(previousBlock.getHash())) {
            System.out.println("前一区块的 hash 值错误");
            return false;
        }
        // 判断 hash 是不是合法
        if (!HashUtil.getSHA256(block.originalString()).equals(block.getHash())) {
            System.out.println("哈希值错误");
            return false;
        }
        System.out.println("区块合法");
        return true;
    }

    /**
     * 替换最长区块链
     * @param newBlockChain 新区块列表数据
     */
    public void replaceChain(List<Block> newBlockChain) {
        // 验证：
        // 1. 新的区块列表是不是比本地的区块列表更长
        // 2. 新的区块链是一个合法有效的区块链
        if (newBlockChain.size() > blockChain.size() && isValidBlocks(newBlockChain)) {
            // 验证通过：直接把新的区块列表数据赋给当前的 blockChain 属性
            blockChain = newBlockChain;
        }
    }

    /**
     * 判断一个区块列表是否合法
     * @param blocks 待验证的区块列表
     * @return 验证结果
     */
    private boolean isValidBlocks(List<Block> blocks) {
        System.out.println("验证区块链是否合法");
        // 新的区块链的创世区块必须和本地区块链的创世区块一致
        Block genesisBlock = blocks.get(0);
        if (!genesisBlock.equals(blockChain.get(0))) {
            System.out.println("创世区块不同，验证失败");
            return false;
        }
        // 新区块链里面所有的区块都是合法的
        for (int i = 1; i < blocks.size(); i++) {
            // 如果有任意一个区块是不合法的，那么，返回 false
            if (!isValidBlock(blocks.get(i), blocks.get(i - 1))) {
                System.out.println("区块不合法，验证失败");
                return false;
            }
        }
        System.out.println("区块链合法");
        return true;
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

    /**
     * 获取当前区块链最新的区块
     * @return
     */
    private Block getLastBlock() {
        return blockChain.get(blockChain.size() - 1);
    }

    @Override
    public String toString() {
        return "BlockChain{" +
                "blockChain=" + blockChain +
                '}';
    }

}
