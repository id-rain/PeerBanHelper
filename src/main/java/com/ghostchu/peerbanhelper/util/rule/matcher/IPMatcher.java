package com.ghostchu.peerbanhelper.util.rule.matcher;

import com.ghostchu.peerbanhelper.text.Lang;
import com.ghostchu.peerbanhelper.text.TranslationComponent;
import com.ghostchu.peerbanhelper.util.IPAddressUtil;
import com.ghostchu.peerbanhelper.util.rule.MatchResult;
import com.ghostchu.peerbanhelper.util.rule.RuleMatcher;
import inet.ipaddr.IPAddress;
import inet.ipaddr.format.util.DualIPv4v6Tries;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class IPMatcher extends RuleMatcher<DualIPv4v6Tries> {
    private DualIPv4v6Tries ips;

    public IPMatcher(String ruleId, String ruleName, List<DualIPv4v6Tries> ruleData) {
        super(ruleId, ruleName, ruleData);
        this.ips = ruleData.getFirst();
    }

    /**
     * 设置数据
     *
     * @param ruleName 规则名
     * @param ruleData 规则数据
     */
    public void setData(String ruleName, List<DualIPv4v6Tries> ruleData) {
        setRuleName(ruleName);
        this.ips = ruleData.getFirst();
    }

    public long size() {
        return ips.size();
    }

    @Override
    public @NotNull MatchResult match0(@NotNull String content) {
        final IPAddress ip = IPAddressUtil.getIPAddress(content);
        if (ip == null) return MatchResult.DEFAULT;
        if (ips == null) {
            return MatchResult.DEFAULT;
        }
        if (ips.elementContains(ip)) {
            return MatchResult.TRUE;
        }
        return MatchResult.DEFAULT;
    }

    @Override
    public TranslationComponent matcherName() {
        return new TranslationComponent(Lang.RULE_MATCHER_SUB_RULE, getRuleName());
    }

    @Override
    public String matcherIdentifier() {
        return "peerbanhelper:ipmatcher";
    }
}
