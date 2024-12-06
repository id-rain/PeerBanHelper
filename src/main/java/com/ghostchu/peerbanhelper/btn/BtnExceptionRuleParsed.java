package com.ghostchu.peerbanhelper.btn;

import com.ghostchu.peerbanhelper.text.Lang;
import com.ghostchu.peerbanhelper.text.TranslationComponent;
import com.ghostchu.peerbanhelper.util.IPAddressUtil;
import com.ghostchu.peerbanhelper.util.rule.AbstractMatcher;
import com.ghostchu.peerbanhelper.util.rule.MatchResult;
import com.ghostchu.peerbanhelper.util.rule.Rule;
import com.ghostchu.peerbanhelper.util.rule.RuleParser;
import com.ghostchu.peerbanhelper.util.rule.matcher.IPMatcher;
import inet.ipaddr.format.util.DualIPv4v6Tries;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class BtnExceptionRuleParsed {
    private String version;
    private Map<String, List<Rule>> peerIdRules;
    private Map<String, List<Rule>> clientNameRules;
    private Map<String, List<Rule>> ipRules;
    private Map<String, List<Rule>> portRules;

    public BtnExceptionRuleParsed(BtnExceptionRule btnRule) {
        this.version = btnRule.getVersion();
        this.ipRules = parseIPRule(btnRule.getIpRules());
        this.portRules = parsePortRule(btnRule.getPortRules());
        this.peerIdRules = parseRule(btnRule.getPeerIdRules());
        this.clientNameRules = parseRule(btnRule.getClientNameRules());
    }

    private Map<String, List<Rule>> parsePortRule(Map<String, List<Integer>> portRules) {
        Map<String, List<Rule>> rules = new HashMap<>();
        portRules.forEach((k, v) -> {
            List<Rule> addresses = new ArrayList<>();
            for (int s : v) {
                addresses.add(new AbstractMatcher() {
                    @Override
                    public @NotNull MatchResult match0(@NotNull String content) {
                        boolean hit = Integer.parseInt(content) == s;
                        return hit ? MatchResult.TRUE : MatchResult.DEFAULT;
                    }

                    @Override
                    public String metadata() {
                        return String.valueOf(s);
                    }

                    @Override
                    public TranslationComponent matcherName() {
                        return new TranslationComponent(Lang.BTN_PORT_RULE, version);
                    }

                    @Override
                    public String matcherIdentifier() {
                        return "btn-exception:port";
                    }
                });
            }
            rules.put(k, addresses);
        });
        return rules;
    }

    public Map<String, List<Rule>> parseIPRule(Map<String, List<String>> raw) {
        Map<String, List<Rule>> rules = new HashMap<>();
        raw.forEach((k, v) -> {
            DualIPv4v6Tries tries = new DualIPv4v6Tries();
            v.stream().map(IPAddressUtil::getIPAddress).forEach(tries::add);
            rules.put(k, List.of(new BtnRuleIpMatcher(version, k, k, List.of(tries))));
        });
        return rules;
    }

    public Map<String, List<Rule>> parseRule(Map<String, List<String>> raw) {
        Map<String, List<Rule>> rules = new HashMap<>();
        raw.forEach((k, v) -> rules.put(k, RuleParser.parse(v)));
        return rules;
    }

    public static class BtnRuleIpMatcher extends IPMatcher {

        private final String version;

        public BtnRuleIpMatcher(String version, String ruleId, String ruleName, List<DualIPv4v6Tries> ruleData) {
            super(ruleId, ruleName, ruleData);
            this.version = version;
        }

        @Override
        public @NotNull TranslationComponent matcherName() {
            return new TranslationComponent(Lang.BTN_IP_RULE, version);
        }

        @Override
        public String matcherIdentifier() {
            return "btn-exception:ip";
        }
    }
}
