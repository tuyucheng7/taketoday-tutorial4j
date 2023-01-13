package cn.tuyucheng.taketoday.blockchain;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlockchainUnitTest {

	public static List<Block> blockchain = new ArrayList<>();
	public static int prefix = 4;
	public static String prefixString = new String(new char[prefix]).replace('\0', '0');

	@BeforeClass
	public static void setUp() {
		Block genesisBlock = new Block("The is the Genesis Block.", "0", new Date().getTime());
		genesisBlock.mineBlock(prefix);
		blockchain.add(genesisBlock);
		Block firstBlock = new Block("The is the First Block.", genesisBlock.getHash(), new Date().getTime());
		firstBlock.mineBlock(prefix);
		blockchain.add(firstBlock);
	}

	@Test
	public void givenBlockchain_whenNewBlockAdded_thenSuccess() {
		Block newBlock = new Block("The is a New Block.", blockchain.get(blockchain.size() - 1)
			.getHash(), new Date().getTime());
		newBlock.mineBlock(prefix);
		assertTrue(newBlock.getHash()
			.substring(0, prefix)
			.equals(prefixString));
		blockchain.add(newBlock);
	}

	@Test
	public void givenBlockchain_whenValidated_thenSuccess() {
		boolean flag = true;
		for (int i = 0; i < blockchain.size(); i++) {
			String previousHash = i == 0 ? "0"
				: blockchain.get(i - 1)
				.getHash();
			flag = blockchain.get(i)
				.getHash()
				.equals(blockchain.get(i)
					.calculateBlockHash())
				&& previousHash.equals(blockchain.get(i)
				.getPreviousHash())
				&& blockchain.get(i)
				.getHash()
				.substring(0, prefix)
				.equals(prefixString);
			if (!flag)
				break;
		}
		assertTrue(flag);
	}

	@AfterClass
	public static void tearDown() {
		blockchain.clear();
	}
}