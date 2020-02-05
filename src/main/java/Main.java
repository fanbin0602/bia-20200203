import pojo.Block;

/**
 * @author fanbin
 * @date 2020/2/5
 */
public class Main {

    public static void main(String[] args) {
        Block block1 = new Block();
        block1.setIndex(0);
        block1.setTimestamp(1578000000000L);
        block1.setHash("000c81784691c7bb8bacab6affb23312fd707bf0463e9afc217ce1a32ab3a4aa");
        block1.setPreviousHash("0");
        block1.setData("GENESIS BLOCK");
        block1.setNonce(1545);

        System.out.println(block1);

        Block block2 = new Block();
        block2.setIndex(1);
        block2.setTimestamp(1578000000000L);
        block2.setHash("000c81784691c7bb8bacab6affb23312fd707bf0463e9afc217ce1a32ab3a4aa");
        block2.setPreviousHash("0");
        block2.setData("GENESIS BLOCK");
        block2.setNonce(1545);

        System.out.println(block2);

        System.out.println(block1.equals(block2));
        System.out.println(block1.equals(null));
        System.out.println(block2.equals(block1));
        System.out.println(block2.equals(null));
        System.out.println(block1.equals(block1));
        System.out.println(block1.equals("abc"));
    }

}
