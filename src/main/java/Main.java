import pojo.Block;
import pojo.BlockChain;
import util.HashUtil;

/**
 * @author fanbin
 * @date 2020/2/5
 */
public class Main {

    public static void main(String[] args) {

        BlockChain bc1 = new BlockChain();
        bc1.addBlock(bc1.generateNextBlock("1-1"));
        bc1.addBlock(bc1.generateNextBlock("1-2"));
        bc1.addBlock(bc1.generateNextBlock("1-3"));

        BlockChain bc2 = new BlockChain();
        bc2.addBlock(bc2.generateNextBlock("2-1"));
        bc2.addBlock(bc2.generateNextBlock("2-2"));

        System.out.println(bc1);
        System.out.println(bc2);

        System.out.println("执行替换");
        bc2.replaceChain(bc1.getBlockChain());

        System.out.println(bc1);
        System.out.println(bc2);

    }

}
