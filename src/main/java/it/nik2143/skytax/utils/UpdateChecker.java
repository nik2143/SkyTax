package it.nik2143.skytax.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A utility class to assist in checking for updates for plugins uploaded to
 * <a href="https://spigotmc.org/resources/">SpigotMC</a>. Before any members of this
 * class are accessed, {@link #init(JavaPlugin, int)} must be invoked by the plugin,
 * preferrably in its {@link JavaPlugin#onEnable()} method, though that is not a
 * requirement.
 * @author Parker Hawke - 2008Choco
 */
public final class UpdateChecker {

    public static final VersionScheme VERSION_SCHEME_DECIMAL = (first, second) -> {
        String[] firstSplit = splitVersionInfo(first), secondSplit = splitVersionInfo(second);
        if (firstSplit == null || secondSplit == null) return null;

        for (int i = 0; i < Math.min(firstSplit.length, secondSplit.length); i++) {
            int currentValue = NumberUtils.toInt(firstSplit[i]), newestValue = NumberUtils.toInt(secondSplit[i]);

            if (newestValue > currentValue) {
                return second;
            } else if (newestValue < currentValue) {
                return first;
            }
        }

        return (secondSplit.length > firstSplit.length) ? second : first;
    };

    private static final String USER_AGENT = "CHOCO-update-checker";
    private static final String UPDATE_URL = "https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=%d";
    private static final Pattern DECIMAL_SCHEME_PATTERN = Pattern.compile("\\d+(?:\\.\\d+)*");

    private static UpdateChecker instance;

    private UpdateResult lastResult = null;

    private final JavaPlugin plugin;
    private final int pluginID;
    private final VersionScheme versionScheme;

    private UpdateChecker(JavaPlugin plugin, int pluginID, VersionScheme versionScheme) {
        this.plugin = plugin;
        this.pluginID = pluginID;
        this.versionScheme = versionScheme;
    }

    public CompletableFuture<UpdateResult> requestUpdateCheck() {
        return CompletableFuture.supplyAsync(() -> {
            int responseCode;
            try {
                URL url = new URL(String.format(UPDATE_URL, pluginID));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.addRequestProperty("User-Agent", USER_AGENT);

                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                responseCode = connection.getResponseCode();

                JsonElement element = new JsonParser().parse(reader);
                reader.close();

                JsonObject versionObject = element.getAsJsonObject();
                String current = plugin.getDescription().getVersion(), newest = versionObject.get("current_version").getAsString();
                String latest = versionScheme.compareVersions(current, newest);

                if (current.contains("-Dev")){
                    return new UpdateResult(UpdateReason.DEV_VERSION);
                }
                if (latest == null) {
                    return new UpdateResult(UpdateReason.UNSUPPORTED_VERSION_SCHEME);
                } else if (latest.equals(current)) {
                    return new UpdateResult(current.equals(newest) ? UpdateReason.UP_TO_DATE : UpdateReason.UNRELEASED_VERSION);
                } else if (latest.equals(newest)) {
                    return new UpdateResult(UpdateReason.NEW_UPDATE, latest);
                }
            } catch (IOException e) {
                return new UpdateResult(UpdateReason.COULD_NOT_CONNECT);
            } catch (JsonSyntaxException e) {
                return new UpdateResult(UpdateReason.INVALID_JSON);
            }

            return new UpdateResult(responseCode == 401 ? UpdateReason.UNAUTHORIZED_QUERY : UpdateReason.UNKNOWN_ERROR);
        });
    }

    public UpdateResult getLastResult() {
        return lastResult;
    }

    private static String[] splitVersionInfo(String version) {
        Matcher matcher = DECIMAL_SCHEME_PATTERN.matcher(version);
        if (!matcher.find()) return null;

        return matcher.group().split("\\.");
    }

    public static UpdateChecker init(JavaPlugin plugin, int pluginID, VersionScheme versionScheme) {
        Preconditions.checkArgument(plugin != null, "Plugin cannot be null");
        Preconditions.checkArgument(pluginID > 0, "Plugin ID must be greater than 0");
        Preconditions.checkArgument(versionScheme != null, "null version schemes are unsupported");

        return (instance == null) ? instance = new UpdateChecker(plugin, pluginID, versionScheme) : instance;
    }

    public static UpdateChecker init(JavaPlugin plugin, int pluginID) {
        return init(plugin, pluginID, VERSION_SCHEME_DECIMAL);
    }

    public static UpdateChecker get() {
        Preconditions.checkState(instance != null, "Instance has not yet been initialized. Be sure #init() has been invoked");
        return instance;
    }

    public static boolean isInitialized() {
        return instance != null;
    }


    @FunctionalInterface
    public interface VersionScheme {

        String compareVersions(String first, String second);

    }

     enum UpdateReason {

        /**
         * A new update is available for download on SpigotMC.
         */
        NEW_UPDATE, // The only reason that requires an update

        /**
         * A successful connection to the API could not be established.
         */
        COULD_NOT_CONNECT,

        /**
         * The JSON retrieved was invalid or malformed.
         */
        INVALID_JSON,

        /**
         * A 401 error was returned by the API.
         */
        UNAUTHORIZED_QUERY,

        /**
         * The version of the plugin installed on the server is a development version
         */
        DEV_VERSION,

        /**
         * The version of the plugin installed on the server is greater than the one uploaded
         * to SpigotMC's resources section.
         */
        UNRELEASED_VERSION,

        /**
         * An unknown error occurred.
         */
        UNKNOWN_ERROR,

        /**
         * The plugin uses an unsupported version scheme, therefore a proper comparison between
         * versions could not be made.
         */
        UNSUPPORTED_VERSION_SCHEME,

        /**
         * The plugin is up to date with the version released on SpigotMC's resources section.
         */
        UP_TO_DATE

    }

    public final class UpdateResult {

        private final UpdateReason reason;
        private final String newestVersion;

        {
            UpdateChecker.this.lastResult = this;
        }

        private UpdateResult(UpdateReason reason, String newestVersion) {
            this.reason = reason;
            this.newestVersion = newestVersion;
        }

        private UpdateResult(UpdateReason reason) {
            Preconditions.checkArgument(reason != UpdateReason.NEW_UPDATE, "Reasons that require updates must also provide the latest version String");
            this.reason = reason;
            this.newestVersion = plugin.getDescription().getVersion();
        }


        public UpdateReason getReason() {
            return reason;
        }

        public boolean requiresUpdate() {
            return reason == UpdateReason.NEW_UPDATE;
        }

        public String getNewestVersion() {
            return newestVersion;
        }

    }

}