package com.ghostchu.peerbanhelper.push.impl;

import com.ghostchu.peerbanhelper.push.AbstractPushProvider;
import com.ghostchu.peerbanhelper.util.json.JsonUtil;
import com.google.gson.JsonObject;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

@Slf4j
public class SmtpPushProvider extends AbstractPushProvider {
    private final Config config;

    public SmtpPushProvider(Config config) {
       this.config = config;
    }

    public static SmtpPushProvider loadFromJson(JsonObject json) {
        return new SmtpPushProvider(JsonUtil.getGson().fromJson(json, Config.class));
    }

    public static SmtpPushProvider loadFromYaml(ConfigurationSection section){
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", section.getBoolean("ssl"));
        props.put("mail.smtp.host", section.getString("host"));
        props.put("mail.smtp.port", section.getString("port"));
        var username = section.getString("username");
        var password = section.getString("password");
        var sender = section.getString("sender");
        var senderName = section.getString("name");
        var receivers = section.getStringList("receiver");
        Config config = new Config(props, username, password, sender, senderName, receivers);
        return new SmtpPushProvider(config);
    }

    @Override
    public JsonObject saveJson() {
        return JsonUtil.readObject(JsonUtil.standard().toJson(config));
    }

    public void sendMail(String email, String subject, String text) throws MessagingException, UnsupportedEncodingException {
        var session = Session.getDefaultInstance(config.getProps(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getUsername(), config.getPassword());
            }
        });
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(config.getSender(), config.getSenderName(), "UTF-8"));
        msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));
        msg.setSubject(stripMarkdown(subject));
        msg.setContent(markdown2Html(text), "text/html;charset=utf-8");
        Transport.send(msg);
    }

    @Override
    public String getConfigType() {
        return "smtp";
    }

    @Override
    public boolean push(String title, String content) {
        boolean anySuccess = false;
        for (String receiver : config.getReceivers()) {
            try {
                sendMail(receiver, title, content);
                anySuccess = true;
            } catch (Exception e) {
                log.warn("Failed to send mail to {}", receiver, e);
            }
        }
        return anySuccess;

    }

    @AllArgsConstructor
    @Data
    public static class Config {
        private Properties props;
        private String username;
        private String password;
        private String sender;
        private String senderName;
        private @NotNull List<String> receivers;
    }
}