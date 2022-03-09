package com.polyu.blockchain.p2p.netty;

import com.polyu.blockchain.chain.Block;
import com.polyu.blockchain.chain.MainChain;
import com.polyu.blockchain.chain.transaction.Output;
import com.polyu.blockchain.chain.transaction.Transaction;
import com.polyu.blockchain.common.p2p.Handler;
import com.polyu.blockchain.common.wrapper.Message;
import com.polyu.blockchain.common.wrapper.RegistryPackage;
import com.polyu.blockchain.p2p.netty.server.BootStrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BusinessHandler extends ChannelInboundHandlerAdapter implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(BusinessHandler.class);

    private static BusinessHandler handler;
    private static Set<String> uuidSet = new HashSet<>();

    public static BusinessHandler getInstance() {
        if (handler == null) {
            handler = new BusinessHandler();
        }
        return handler;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        Channel ch = ctx.channel();
        ch.writeAndFlush(new Message(true));
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
            ctx.channel().writeAndFlush(rep);
            return;
        }
        if (message.isAddressReply()) {
            PeerServerConnectKeeper.add(ctx.channel(), new RegistryPackage(message.getIp(), message.getPort()));
            return;
        }
        // mine request process
        if (message.isMineRequest()) {
            uuidSet.add(message.getUuid());
            Block block = message.getContent();
            // mine
            block.mineBlock(5);

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
            return;
        }
        // mine reply process
        if (message.isMineReply()) {
            String uuid = message.getUuid();
            if (!uuidSet.contains(uuid)) {
                return;
            }
            uuidSet.remove(uuid);
            Block block = message.getContent();
            MainChain.add(block);
            if (!MainChain.isChainValid()) {
                ArrayList<Block> blockChain = MainChain.getBlockChain();
                blockChain.remove(blockChain.size() - 1);
                // unsuccessful -> continue to accept new reply
                uuidSet.add(uuid);
                return;
            }
            ArrayList<Transaction> transactions = block.getTransactions();
            // update local UTXO
            for (Transaction t : transactions) {
                t.processTransaction();
            }
            return;
        }
        if (message.isSynChain()) {
            MainChain.blockChain = message.getBlockChain();
            MainChain.UTXOs = message.getUTXOs();
            MainChain.genesisTransaction = message.getGenesisTransaction();
            return;
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
            message.setContent(block);
            peer.writeAndFlush(message);
        }
    }

    /**
     * syn chain
     */
    public static void synChain(ArrayList<Block> blockChain, HashMap<String, Output> UTXOs, Transaction genesisTransaction) {
        Map<RegistryPackage, Channel> map = PeerServerConnectKeeper.getMap();
        for (RegistryPackage key : map.keySet()) {
            Channel peer = map.get(key);
            Message message = new Message();
            message.setSynChain(true);

            message.setBlockChain(blockChain);
            message.setUTXOs(UTXOs);
            message.setGenesisTransaction(genesisTransaction);
            peer.writeAndFlush(message);
        }
    }

    @Override
    public void processBroadcast(Object o) {
        mineBroadcast((Block) o);
    }
}
