package com.polyu.blockchain.chain;

import com.polyu.blockchain.chain.facade.impl.BlockChainServiceImp;
import com.polyu.blockchain.chain.transaction.Input;
import com.polyu.blockchain.chain.transaction.Output;
import com.polyu.blockchain.chain.transaction.Transaction;
import com.polyu.blockchain.chain.transaction.Wallet;
import com.polyu.blockchain.common.util.KeyUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class MainChain {
    private static final Logger log = LoggerFactory.getLogger(MainChain.class);

    public static ArrayList<Block> blockChain = new ArrayList<>();
    public static HashMap<String, Output> UTXOs = new HashMap<>();

    private static final int difficulty = 5;
    private static Wallet walletA;
    private static Wallet walletB;
    public static Transaction genesisTransaction;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {


        walletA = new Wallet();
        Wallet coinbase = new Wallet();
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 1000f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);     // manually sign the genesis transaction
        genesisTransaction.transactionId = "0"; // manually set the transaction id
        genesisTransaction.outputs.add(new Output(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.

        log.info("Creating and Mining Genesis block.");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);
        log.info("init wallet public key: {}.", KeyUtil.PublicKeyToString(walletA.publicKey));
        log.info("init wallet public key: {}.", KeyUtil.PrivateKeyToString(walletA.privateKey));

        walletB = new Wallet();

        BlockChainServiceImp blockChainServiceImp = new BlockChainServiceImp();
        blockChainServiceImp.transfer(KeyUtil.PublicKeyToString(walletA.publicKey),
                KeyUtil.PrivateKeyToString(walletA.privateKey),
                KeyUtil.PublicKeyToString(walletB.publicKey), 100);
        float balance = blockChainServiceImp.getBalance(KeyUtil.PublicKeyToString(walletB.publicKey));
        System.out.println("balance = " + balance);
        /**
        //Create wallets:
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        // create genesis transaction, which sends 100 NoobCoin to walletA:
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);     //manually sign the genesis transaction
        genesisTransaction.transactionId = "0"; //manually set the transaction id
        genesisTransaction.outputs.add(new Output(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.

        log.info("Creating and Mining Genesis block.");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        // testing
        Block block1 = new Block(genesis.hash);
        log.info("WalletA's balance is: {}", walletA.getBalance());
        log.info("WalletA is Attempting to send funds (40) to WalletB.");

        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);

        log.info("WalletA's balance is: {}", walletA.getBalance());
        log.info("WalletB's balance is: {}", walletB.getBalance());

        Block block2 = new Block(block1.hash);
        log.info("WalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        log.info("WalletA's balance is: " + walletA.getBalance());
        log.info("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
        log.info("WalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20));
        log.info("WalletA's balance is: " + walletA.getBalance());
        log.info("WalletB's balance is: " + walletB.getBalance());

        isChainValid();
        log.info("blockchain = {}", blockChain);
         */

    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, Output> tempUTXOs = new HashMap<>(); // a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        // loop through block chain to check hashes:
        for (int i = 1; i < blockChain.size(); i++) {

            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i - 1);
            // compare registered hash and calculated hash:
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                log.info("Current Hashes not equal");
                return false;
            }
            // compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                log.error(blockChain.toString());
                log.warn("Previous Hashes not equal");
                return false;
            }
            // check if hash is solved
            if (currentBlock.hash == null || !currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                log.info("This block hasn't been mined");
                return false;
            }

            // loop block chain's transactions:
            Output tempOutput;
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if (!currentTransaction.verifySignature()) {
                    log.info("Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    log.info("Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for (Input input : currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if (tempOutput == null) {
                        log.info("Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if (input.UTXO.value != tempOutput.value) {
                        log.info("Referenced input Transaction({}) value is Invalid ", t);
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for (Output output : currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if (!currentTransaction.outputs.get(0).recipient.equals(currentTransaction.recipient)) {
                    log.info("Transaction({}) output recipient is not who it should be", t);
                    return false;
                }
                if (!currentTransaction.outputs.get(1).recipient.equals(currentTransaction.sender)) {
                    log.info("Transaction({}) output 'change' is not sender.", t);
                    return false;
                }

            }

        }
        log.info("blockchain is valid.");
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockChain.add(newBlock);
    }

    public static void add(Block newBlock) {
        blockChain.add(newBlock);
    }

    public static HashMap<String, Output> getUTXOs() {
        return UTXOs;
    }

    public static ArrayList<Block> getBlockChain() {
        return blockChain;
    }

    public static int getDifficulty() {
        return difficulty;
    }
}
