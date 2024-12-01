package com.ghostchu.peerbanhelper.gui.impl.console;

import com.ghostchu.peerbanhelper.gui.impl.GuiImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

@Slf4j
public class ConsoleGuiImpl implements GuiImpl {
    private final AtomicBoolean wakeLock = new AtomicBoolean(false);

    public ConsoleGuiImpl(String[] args) {
    }

    @Override
    public void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Override
    public void createMainWindow() {

    }

    @SneakyThrows
    @Override
    public void sync() {
        while (!wakeLock.get()) {
            synchronized (wakeLock) {
                wakeLock.wait(1000 * 5);
            }
        }
    }

    @Override
    public void close() {
        synchronized (wakeLock) {
            wakeLock.set(true);
            wakeLock.notifyAll();
        }
    }

    @Override
    public void createNotification(Level level, String title, String description) {
        if (level.equals(Level.INFO)) {
            log.info("{}: {}", title, description);
        }
        if (level.equals(Level.WARNING)) {
            log.warn("{}: {}", title, description);
        }
        if (level.equals(Level.SEVERE)) {
            log.error("{}: {}", title, description);
        }
    }

    @Override
    public void createDialog(Level level, String title, String description) {
        if (level.equals(Level.INFO)) {
            log.info("{}: {}", title, description);
        }
        if (level.equals(Level.WARNING)) {
            log.warn("{}: {}", title, description);
        }
        if (level.equals(Level.SEVERE)) {
            log.error("{}: {}", title, description);
        }
    }


}
