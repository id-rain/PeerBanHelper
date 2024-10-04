package com.ghostchu.peerbanhelper.module.impl.webapi;

import com.ghostchu.peerbanhelper.module.AbstractFeatureModule;
import com.ghostchu.peerbanhelper.util.context.IgnoreScan;
import com.ghostchu.peerbanhelper.web.JavalinWebContainer;
import com.ghostchu.peerbanhelper.web.Role;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@IgnoreScan
public class PBHFriendController extends AbstractFeatureModule {
    @Autowired
    private JavalinWebContainer webContainer;


    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public @NotNull String getName() {
        return "WebAPI - Friend";
    }

    @Override
    public @NotNull String getConfigName() {
        return "webapi-friend";
    }

    @Override
    public void onEnable() {
        webContainer.javalin()
                .get("/api/friend/list", this::list, Role.USER_READ)
                .put("/api/friend/add", this::addFriend, Role.USER_WRITE)
                .delete("/api/friend/remove", this::removeFriend, Role.USER_WRITE);
    }

    private void removeFriend(@NotNull Context context) {
    }

    private void addFriend(@NotNull Context context) {
    }

    private void list(@NotNull Context context) {
    }

    @Override
    public void onDisable() {

    }
}
