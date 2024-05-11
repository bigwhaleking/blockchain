package project;
import java.security.*;
import java.security.spec.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
	public PrivateKey privatekey;
	public PublicKey publickey;
	
	public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	
	public Wallet() {
		generateKeyPair();
	}
	
	//generate public/private key in keypair
	public void generateKeyPair() {
		try {
			KeyPairGenerator KeyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			
			//initialize the key generator and generate a keypiar
			KeyGen.initialize(ecSpec, random);
			KeyPair keypair = KeyGen.generateKeyPair();
			
			//set the public/private key in keypair
			privatekey = keypair.getPrivate();
			publickey = keypair.getPublic();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public float getBalance() {
		float total = 0;
		
		for(Map.Entry<String, TransactionOutput> item: chain.UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			
			if(UTXO.isMine(publickey)) {
				UTXOs.put(UTXO.id, UTXO);
				total += UTXO.value;
			}
		}
		
		return total;
	}
	
	public Transaction sendFunds(PublicKey _recipient, float value) {
		if(getBalance() < value ) {
			System.out.println("Not Enough funds to send transaction");
			return null;
		}
		
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		
		float total = 0;
		
		for(Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));
			
			if(total>value) break;
		}
		
		Transaction newTransaction = new Transaction(publickey, _recipient, value, inputs);
		newTransaction.generateSignature(privatekey);
		
		for(TransactionInput input : inputs) {
			UTXOs.remove(input.transactionOutputId);
		}
		return newTransaction;
	}
	
	
	
	
	
	
}
