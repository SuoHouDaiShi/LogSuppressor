package com.twsfts.goetylogsuppressor;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import java.util.regex.Pattern;

/**
 * Dedicated-server-only Log4j filter for four specific Minecraft log message shapes.
 *
 * Matching is intentionally namespace-agnostic: any resource location of the form
 * namespace:path is accepted, and the statistic filename is not tied to any player.
 *
 * The filter is attached to the Log4j root logger, so rejected events do not reach
 * either the console appender or the file appender. Other WARN/ERROR messages remain
 * untouched.
 */
@Mod(value = GoetyLogSuppressor.MOD_ID, dist = Dist.DEDICATED_SERVER)
public final class GoetyLogSuppressor {
    public static final String MOD_ID = "goety_log_suppressor";

    // Minecraft resource-location syntax is namespace:path. The path may contain
    // nested folders, which is why '/' is allowed here as well.
    private static final String RESOURCE_LOCATION = "[a-z0-9_.-]+:[a-z0-9_./-]+";

    private static final Pattern UNKNOWN_ATTACHMENT = Pattern.compile(
            "^Encountered unknown or non-serializable data attachment "
                    + RESOURCE_LOCATION + "\\. Skipping\\.$");

    private static final Pattern BLOCK_ENTITY = Pattern.compile(
            "^Skipping BlockEntity with id " + RESOURCE_LOCATION + "$");

    private static final Pattern UNKNOWN_ATTRIBUTE = Pattern.compile(
            "^Ignoring unknown attribute '" + RESOURCE_LOCATION + "'$");

    private static final Pattern INVALID_STATISTIC = Pattern.compile(
            "^Invalid statistic in .*[/\\\\]stats[/\\\\].*\\.json: Don't know what "
                    + RESOURCE_LOCATION + " is$");

    public GoetyLogSuppressor() {
        Logger root = (Logger) LogManager.getRootLogger();
        root.addFilter(GenericCompatibilitySpamFilter.INSTANCE);
    }

    private static final class GenericCompatibilitySpamFilter extends AbstractFilter {
        private static final GenericCompatibilitySpamFilter INSTANCE = new GenericCompatibilitySpamFilter();

        @Override
        public Result filter(LogEvent event) {
            Message message = event.getMessage();
            if (message == null) {
                return Result.NEUTRAL;
            }

            String text = message.getFormattedMessage();
            return isTargetMessage(text) ? Result.DENY : Result.NEUTRAL;
        }

        private static boolean isTargetMessage(String text) {
            if (text == null) {
                return false;
            }

            return UNKNOWN_ATTACHMENT.matcher(text).matches()
                    || BLOCK_ENTITY.matcher(text).matches()
                    || UNKNOWN_ATTRIBUTE.matcher(text).matches()
                    || INVALID_STATISTIC.matcher(text).matches();
        }
    }
}
