package com.ghostchu.peerbanhelper.module.impl.webapi;

import com.ghostchu.peerbanhelper.Main;
import com.ghostchu.peerbanhelper.module.AbstractFeatureModule;
import com.ghostchu.peerbanhelper.text.Lang;
import com.ghostchu.peerbanhelper.util.ByteUtil;
import com.ghostchu.peerbanhelper.util.context.IgnoreScan;
import com.ghostchu.peerbanhelper.web.JavalinWebContainer;
import com.ghostchu.peerbanhelper.web.Role;
import com.ghostchu.peerbanhelper.web.wrapper.StdResp;
import com.ghostchu.peerbanhelper.youkai.Youkai;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.javalin.http.Context;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.ghostchu.peerbanhelper.text.TextManager.tl;

@Component
@Slf4j
@IgnoreScan
public class YoukaiController extends AbstractFeatureModule {
    private final JavalinWebContainer javalinWebContainer;
    private Cache<String, YoukaiTask> youkaiCache = CacheBuilder
            .newBuilder()
            .maximumSize(15)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    public YoukaiController(JavalinWebContainer javalinWebContainer) {
        super();
        this.javalinWebContainer = javalinWebContainer;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public @NotNull String getName() {
        return "Youkai - Controller";
    }

    @Override
    public @NotNull String getConfigName() {
        return "youkai";
    }

    @Override
    public void onEnable() {
        javalinWebContainer.javalin()
                .post("/youkai/new", this::createYoukaiTask, Role.USER_WRITE)
                .get("/youkai/{taskId}", this::getYoukaiTask, Role.USER_WRITE);
    }

    private void getYoukaiTask(Context context) {
        var taskId = context.pathParam("taskId");
        var taskResult = youkaiCache.getIfPresent(taskId);
        if (taskResult != null) {
            context.json(new StdResp(true, null, taskResult));
        } else {
            context.json(new StdResp(false, tl(locale(context), Lang.YOUKAI_TASK_NOT_FOUND), null));
        }
    }

    private void createYoukaiTask(Context context) {
        var taskCreation = context.bodyAsClass(YoukaiTaskCreate.class);
        var taskId = UUID.randomUUID().toString();
        Youkai youkai = new Youkai(taskCreation.ip(), taskCreation.port(), 0, ByteUtil.hexToByteArray(taskCreation.infoHash()),
                "-PBHYK1-", "PeerBanHelper/" + Main.getMeta().getVersion() + " Youkai/0.0.1");
        YoukaiTask task = new YoukaiTask();
        task.setRunning(true);
        youkai.test().thenAccept(youkaiResult -> {
            task.setResult(youkaiResult);
            task.setRunning(false);
            System.out.println("Youkai task completed: " + taskId);
        });
        youkaiCache.put(taskId, task);
        context.json(new StdResp(true, null, taskId));
    }

    @Override
    public void onDisable() {

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class YoukaiTask {
        private boolean running;
        private Youkai.YoukaiResult result;
    }

    record YoukaiTaskCreate(
            String ip,
            int port,
            String infoHash
    ) {
    }
}
