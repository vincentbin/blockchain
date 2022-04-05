package com.polyu.blockchain.p2p.netty;

import com.polyu.blockchain.chain.Block;
import com.polyu.blockchain.chain.MainChain;
import com.polyu.blockchain.chain.transaction.Input;
import com.polyu.blockchain.chain.transaction.Output;
import com.polyu.blockchain.chain.transaction.Transaction;
import com.polyu.blockchain.common.p2p.Handler;
import com.polyu.blockchain.common.util.KeyUtil;
import com.polyu.blockchain.common.wrapper.Message;
import com.polyu.blockchain.common.wrapper.RegistryPackage;
import com.polyu.blockchain.p2p.netty.server.BootStrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * netty handler
 */
public class BusinessHandler extends ChannelInboundHandlerAdapter implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(BusinessHandler.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        Message message = new Message(true);
        if (PeerServerConnectKeeper.isNeedSynChain()) {
            message.setSynChain(true);
        }
        Channel ch = ctx.channel();
        ch.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Message message = (Message) msg;
        logger.info(message.toString());
        if (message.isAddressQuery()) {
            String serverAddress = BootStrap.getServerAddress();
            String[] address = serverAddress.split(":");
            Message rep = new Message(address[0], Integer.parseInt(address[1]));
            rep.setAddressReply(true);

            if (message.isSynChain()) {
                ArrayList<Block> blockChain = MainChain.blockChain;
                for (Block block : blockChain) {
                    ArrayList<Transaction> transactions = block.getTransactions();
                    for (Transaction transaction : transactions) {
                        transaction.setSenderStr(KeyUtil.PublicKeyToString(transaction.getSender()));
                        transaction.setRecipientStr(KeyUtil.PublicKeyToString(transaction.getRecipient()));

                        ArrayList<Input> inputs = transaction.getInputs();
                        ArrayList<Output> outputs = transaction.getOutputs();
                        if (inputs != null) {
                            for (Input input : inputs) {
                                Output utxo = input.getUTXO();
                                utxo.setRecipientStr(KeyUtil.PublicKeyToString(utxo.getRecipient()));
                            }
                        }
                        for (Output output : outputs) {
                            output.setRecipientStr(KeyUtil.PublicKeyToString(output.getRecipient()));
                        }
                    }
                }
                rep.setBlockChain(blockChain);

                HashMap<String, Output> utxOs = MainChain.UTXOs;
                for (Map.Entry<String, Output> utox : utxOs.entrySet()) {
                    Output output = utox.getValue();
                    output.setRecipientStr(KeyUtil.PublicKeyToString(output.getRecipient()));
                }
                rep.setUTXOs(utxOs);

                Transaction genesisTransaction = MainChain.genesisTransaction;
                genesisTransaction.setSenderStr(KeyUtil.PublicKeyToString(genesisTransaction.getSender()));
                genesisTransaction.setRecipientStr(KeyUtil.PublicKeyToString(genesisTransaction.getRecipient()));
                ArrayList<Input> inputs = genesisTransaction.getInputs();
                ArrayList<Output> outputs = genesisTransaction.getOutputs();
                if (inputs != null) {
                    for (Input input : inputs) {
                        Output utxo = input.getUTXO();
                        utxo.setRecipientStr(KeyUtil.PublicKeyToString(utxo.getRecipient()));
                    }
                }
                for (Output output : outputs) {
                    output.setRecipientStr(KeyUtil.PublicKeyToString(output.getRecipient()));
                }
                rep.setGenesisTransaction(genesisTransaction);

                rep.setSynChain(true);
            }
            ctx.channel().writeAndFlush(rep);
            return;
        }
        if (message.isAddressReply()) {
            PeerServerConnectKeeper.add(ctx.channel(), new RegistryPackage(message.getIp(), message.getPort()));
            if (message.isSynChain()) {
                ArrayList<Block> blockChain = message.getBlockChain();
                for (Block block : blockChain) {
                    ArrayList<Transaction> transactions = block.getTransactions();
                    for (Transaction transaction : transactions) {
                        transaction.setSender(KeyUtil.stringToPublicKey(transaction.getSenderStr()));
                        transaction.setRecipient(KeyUtil.stringToPublicKey(transaction.getRecipientStr()));

                        ArrayList<Input> inputs = transaction.getInputs();
                        ArrayList<Output> outputs = transaction.getOutputs();
                        if (inputs != null) {
                            for (Input input : inputs) {
                                Output utxo = input.getUTXO();
                                utxo.setRecipient(KeyUtil.stringToPublicKey(utxo.getRecipientStr()));
                            }
                        }
                        for (Output output : outputs) {
                            output.setRecipient(KeyUtil.stringToPublicKey(output.getRecipientStr()));
                        }
                    }
                }
                MainChain.blockChain = blockChain;

                HashMap<String, Output> utxOs = message.getUTXOs();
                for (Map.Entry<String, Output> utxo : utxOs.entrySet()) {
                    Output output = utxo.getValue();
                    output.setRecipient(KeyUtil.stringToPublicKey(output.getRecipientStr()));
                }
                MainChain.UTXOs = utxOs;

                Transaction genesisTransaction = message.getGenesisTransaction();
                genesisTransaction.setSender(KeyUtil.stringToPublicKey(genesisTransaction.senderStr));
                genesisTransaction.setRecipient(KeyUtil.stringToPublicKey(genesisTransaction.recipientStr));
                ArrayList<Input> inputs = genesisTransaction.getInputs();
                ArrayList<Output> outputs = genesisTransaction.getOutputs();
                if (inputs != null) {
                    for (Input input : inputs) {
                        Output output = input.getUTXO();
                        output.setRecipient(KeyUtil.stringToPublicKey(output.getRecipientStr()));
                    }
                }
                for (Output output : outputs) {
                    output.setRecipient(KeyUtil.stringToPublicKey(output.getRecipientStr()));
                }
                MainChain.genesisTransaction = genesisTransaction;
            }
            return;
        }
        // mine request process
        if (message.isMineRequest()) {
            PeerServerConnectKeeper.getUuidSet().add(message.getUuid());
            Block block = message.getContent();
            ArrayList<Transaction> transactions = block.getTransactions();
            for (Transaction transaction : transactions) {
                transaction.setSender(KeyUtil.stringToPublicKey(transaction.getSenderStr()));
                transaction.setRecipient(KeyUtil.stringToPublicKey(transaction.getRecipientStr()));

                ArrayList<Input> inputs = transaction.getInputs();
                ArrayList<Output> outputs = transaction.getOutputs();
                if (inputs != null) {
                    for (Input input : inputs) {
                        Output output = input.getUTXO();
                        output.setRecipient(KeyUtil.stringToPublicKey(output.getRecipientStr()));
                    }
                }
                for (Output output : outputs) {
                    output.setRecipient(KeyUtil.stringToPublicKey(output.getRecipientStr()));
                }
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // mine
                    block.mineBlock(5);
                    // add to local chain
                    MainChain.add(block);
                    if (!MainChain.isChainValid()) {
                        ArrayList<Block> blockChain = MainChain.getBlockChain();
                        blockChain.remove(blockChain.size() - 1);
                        return;
                    }
                    for (Transaction t : block.getTransactions()) {
                        t.updateLocalUTXO();
                    }

                    // success added, remove id, avoid others' result modify local chain
                    PeerServerConnectKeeper.getUuidSet().remove(message.getUuid());

                    // compute hash completed -> broadcast
                    Map<RegistryPackage, Channel> map = PeerServerConnectKeeper.getMap();
                    for (RegistryPackage key : map.keySet()) {
                        Channel channel = map.get(key);
                        Message replyMsg = new Message();
                        replyMsg.setMineReply(true);
                        replyMsg.setUuid(message.getUuid());
                        replyMsg.setContent(block);
                        channel.writeAndFlush(replyMsg);
                    }
                }
            }).start();
            return;
        }

        // mine reply process
        if (message.isMineReply()) {
            String uuid = message.getUuid();
            if (!PeerServerConnectKeeper.getUuidSet().contains(uuid)) {
                return;
            }
            PeerServerConnectKeeper.getUuidSet().remove(uuid);
            Block block = message.getContent();
            ArrayList<Transaction> transactions = block.getTransactions();
            for (Transaction transaction : transactions) {
                transaction.setSender(KeyUtil.stringToPublicKey(transaction.getSenderStr()));
                transaction.setRecipient(KeyUtil.stringToPublicKey(transaction.getRecipientStr()));

                ArrayList<Input> inputs = transaction.getInputs();
                ArrayList<Output> outputs = transaction.getOutputs();
                if (inputs != null) {
                    for (Input input : inputs) {
                        Output output = input.getUTXO();
                        output.setRecipient(KeyUtil.stringToPublicKey(output.getRecipientStr()));
                    }
                }
                for (Output output : outputs) {
                    output.setRecipient(KeyUtil.stringToPublicKey(output.getRecipientStr()));
                }
            }
            MainChain.add(block);
            if (!MainChain.isChainValid()) {
                ArrayList<Block> blockChain = MainChain.getBlockChain();
                blockChain.remove(blockChain.size() - 1);
                // unsuccessful -> continue to accept new reply
                PeerServerConnectKeeper.getUuidSet().add(uuid);
                return;
            }
            // update local UTXO
            for (Transaction t : transactions) {
                t.updateLocalUTXO();
            }
            return;
        }
        // syn chain process
        if (message.isSynChain()) {
            ArrayList<Block> blockChain = message.getBlockChain();
            for (Block block : blockChain) {
                ArrayList<Transaction> transactions = block.getTransactions();
                for (Transaction transaction : transactions) {
                    transaction.setSender(KeyUtil.stringToPublicKey(transaction.getSenderStr()));
                    transaction.setRecipient(KeyUtil.stringToPublicKey(transaction.getRecipientStr()));

                    ArrayList<Input> inputs = transaction.getInputs();
                    ArrayList<Output> outputs = transaction.getOutputs();
                    if (inputs != null) {
                        for (Input input : inputs) {
                            Output utxo = input.getUTXO();
                            utxo.setRecipient(KeyUtil.stringToPublicKey(utxo.getRecipientStr()));
                        }
                    }
                    for (Output output : outputs) {
                        output.setRecipient(KeyUtil.stringToPublicKey(output.getRecipientStr()));
                    }
                }
            }
            MainChain.blockChain = blockChain;

            HashMap<String, Output> utxOs = message.getUTXOs();
            for (Map.Entry<String, Output> utxo : utxOs.entrySet()) {
                Output output = utxo.getValue();
                output.setRecipient(KeyUtil.stringToPublicKey(output.getRecipientStr()));
            }
            MainChain.UTXOs = utxOs;

            Transaction genesisTransaction = message.getGenesisTransaction();
            genesisTransaction.setSender(KeyUtil.stringToPublicKey(genesisTransaction.senderStr));
            genesisTransaction.setRecipient(KeyUtil.stringToPublicKey(genesisTransaction.recipientStr));
            ArrayList<Input> inputs = genesisTransaction.getInputs();
            ArrayList<Output> outputs = genesisTransaction.getOutputs();
            if (inputs != null) {
                for (Input input : inputs) {
                    Output output = input.getUTXO();
                    output.setRecipient(KeyUtil.stringToPublicKey(output.getRecipientStr()));
                }
            }
            for (Output output : outputs) {
                output.setRecipient(KeyUtil.stringToPublicKey(output.getRecipientStr()));
            }
            MainChain.genesisTransaction = genesisTransaction;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    /**
     * broadcast mining block
     */
    public static void mineBroadcast(Block block) {
        Map<RegistryPackage, Channel> map = PeerServerConnectKeeper.getMap();
        String uuid = UUID.randomUUID().toString();
        for (RegistryPackage key : map.keySet()) {
            Channel peer = map.get(key);
            Message message = new Message();
            message.setMineRequest(true);
            message.setUuid(uuid);

            ArrayList<Transaction> transactions = block.getTransactions();
            for (Transaction transaction : transactions) {
                transaction.setSenderStr(KeyUtil.PublicKeyToString(transaction.getSender()));
                transaction.setRecipientStr(KeyUtil.PublicKeyToString(transaction.getRecipient()));

                ArrayList<Input> inputs = transaction.getInputs();
                ArrayList<Output> outputs = transaction.getOutputs();
                if (inputs != null) {
                    for (Input input : inputs) {
                        Output output = input.getUTXO();
                        output.setRecipientStr(KeyUtil.PublicKeyToString(output.getRecipient()));
                    }
                }
                for (Output output : outputs) {
                    output.setRecipientStr(KeyUtil.PublicKeyToString(output.getRecipient()));
                }
            }
            message.setContent(block);

            peer.writeAndFlush(message);
        }
    }

    @Override
    public void processBroadcast(Object o) {
        mineBroadcast((Block) o);
    }
}
