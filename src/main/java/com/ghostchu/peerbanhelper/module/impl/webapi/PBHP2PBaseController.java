package com.ghostchu.peerbanhelper.module.impl.webapi;

import com.ghostchu.peerbanhelper.Main;
import com.ghostchu.peerbanhelper.decentralized.ipfs.IPFS;
import com.ghostchu.peerbanhelper.module.AbstractFeatureModule;
import com.ghostchu.peerbanhelper.util.context.IgnoreScan;
import com.ghostchu.peerbanhelper.web.JavalinWebContainer;
import com.ghostchu.peerbanhelper.web.Role;
import com.ghostchu.peerbanhelper.web.wrapper.StdResp;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@Slf4j
@IgnoreScan
public class PBHP2PBaseController extends AbstractFeatureModule {
    @Autowired
    private JavalinWebContainer webContainer;
    @Autowired
    private IPFS ipfs;

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public @NotNull String getName() {
        return "WebAPI - P2P Base";
    }

    @Override
    public @NotNull String getConfigName() {
        return "webapi-p2p-base";
    }

    @Override
    public void onEnable() {
        webContainer.javalin()
                .get("/p2p-api/heartbeat", this::heartbeat, Role.P2P_GUEST)
                .get("/p2p-api/manifest", this::manifest, Role.P2P_GUEST)
                .get("/p2p-api/keypair/getPublicKey", this::retrievePubKey, Role.P2P_GUEST)
                .post("/p2p-api/keypair/sign", this::sign, Role.P2P_GUEST);
    }

    private void sign(@NotNull Context context) {
        var bytes = context.bodyAsBytes();
        if (bytes.length > 128) {
            throw new IllegalArgumentException("Sign for data that larger than 128 bytes is disallowed");
        }
        var signed = ipfs.getIdentityEd25519Private().sign(bytes);
        context.json(new StdResp(true, null, Base64.getEncoder().encodeToString(signed)));
    }

    private void retrievePubKey(@NotNull Context context) {
        context.json(new StdResp(true, null, Base64.getEncoder().encodeToString(ipfs.getIdentityEd25519Public().bytes())));
    }

    private void manifest(@NotNull Context context) {
        context.json(new StdResp(true, null, Main.getUserAgent()));
    }

    private void heartbeat(@NotNull Context context) {
        context.status(HttpStatus.NO_CONTENT);
    }

    @Override
    public void onDisable() {

    }

}
