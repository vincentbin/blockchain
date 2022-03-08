package com.polyu.p2p;

public class P2PHolder {
    private static Handler handler;

    public static Handler getHandler() {
        return handler;
    }

    public static void setHandler(Handler handler) {
        P2PHolder.handler = handler;
    }
}
