package com.ghostchu.peerbanhelper.util.network;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class DFHostNameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
