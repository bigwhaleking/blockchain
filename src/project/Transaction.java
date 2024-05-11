package project;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.security.*;

public class Transaction {
	public String transactionId;
	public PublicKey sender;
	public PublicKey reciepient;
	public float value;
	public byte[] signature;
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	private static int sequence = 0;
	
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender=from;
		this.reciepient=to;
		this.value=value;
		this.inputs=inputs;
		}
	
	
	
	private String calculateHash() {
		sequence++;
		return StringUtil.applySha256(StringUtil.getStringFromKey(sender)+StringUtil.getStringFromKey(reciepient)+Float.toString(value)+sequence);
	}

	
	public void generateSignature(PrivateKey privatekey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
		signature = StringUtil.applyECDSASig(privatekey, data);
	}
	
	public boolean verifiySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
		return StringUtil.verifiyECDSASig(sender, data, signature);
	}
	
	public boolean processTransaction() {
		if(verifiySignature()==false) {
			System.out.println("Transaction Signature failed to verifiy");
			return false;
		}
		
		for(TransactionInput i : inputs) {
			i.UTXO = chain.UTXOs.get(i.transactionOutputId);
		}
		
		if(getInputValue() < chain.minimumTransaction) {
			System.out.println("Transaction iputs to small : " + getInputValue());
		}
		
		float leftOver = getInputValue() - value;
		transactionId = calculateHash();
		outputs.add(new TransactionOutput(this.reciepient, value, transactionId));
		outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));
		
		for(TransactionOutput o : outputs) {
			chain.UTXOs.put(o.id, o);
		}
		
		for(TransactionInput i : inputs) {
			chain.UTXOs.remove(i.UTXO.id);
		}
		
		return true;
	}
	
	public float getInputValue() {
		float total = 0;
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue;
			total += i.UTXO.value;
		}
		return total;
	}
	
	public float getOutputValue() {
		float total = 0;
		for(TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}
}
