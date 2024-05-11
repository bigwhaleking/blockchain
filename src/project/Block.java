package project;
import java.util.Date;
import java.util.ArrayList;

public class Block {
	
	public String hash;
	public String previousHash;
	private String merkleRoot;
	private long timeStamp;
	private int nonce;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	
	//block constructor
	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
	}
	
	//calculate new hash based on blocks contents
	public String calculateHash() {
		String calculatehash = StringUtil.applySha256(previousHash+Long.toString(timeStamp)+Integer.toString(nonce)+merkleRoot);
		return calculatehash;	
	}
	
	public void mineBlock(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0', '0');
		while(!hash.substring(0, difficulty).equals(target)){
			nonce++;
			hash = calculateHash();
		}
		System.out.println("Block Mined : "+ hash);
	}
	
	public boolean addTransaction(Transaction transaction) {
		if(transaction==null) return false;
		if((previousHash!="0")) {
			if((transaction.processTransaction()!=true)) {
				System.out.println("Transaction failed to process.");
				return false;
			}
		}
		transactions.add(transaction);
		System.out.println("Transaction Successfully added to Block");
		return true;
	}
	
}
