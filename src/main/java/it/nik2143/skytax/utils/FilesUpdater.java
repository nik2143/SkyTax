package it.nik2143.skytax.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Charsets;

public class FilesUpdater {

    private int newVersion; // The next version of the Config
    final private boolean instantDeprecated = false; // Just an option to depecrate the config if the version is different
    final private JavaPlugin plugin;

    private final File file;
    private List<String> lines;
    private final String FileName;

    public FilesUpdater(JavaPlugin plugin, File file, String FileName) {
        this.file = file;
        this.plugin = plugin;
        this.FileName = FileName;
    }

    public void checkUpdate(int oldV,int newVersion) {
        this.newVersion = newVersion;
        if (oldV == -1 || instantDeprecated) {
            if (oldV != newVersion)
                deprecateConfig();
            return;
        }

        if (oldV == newVersion) {
            Bukkit.getConsoleSender().sendMessage("[SkyTax | "+ StringUtils.capitalize(FileName.replace(".yml","")) +"] The "+FileName.replace(".yml","")+" is updated!");
            return;
        }

        lines = readFile(file);
        updateConfig();
    }

    public void updateConfig() {

        Bukkit.getConsoleSender().sendMessage("[SkyTax | "+ StringUtils.capitalize(FileName.replace(".yml","")) +"] Updating the "+FileName.replace(".yml","")+"!");

        List<String> newLines = readInsideFile("/"+file.getName());

        lines.removeIf(s -> s.trim().isEmpty() || s.trim().startsWith("#") || s.split(":").length == 1);
        lines.forEach(s -> {
            String[] a = s.split(":");
            String newS = joinString(Arrays.copyOfRange(a, 1, a.length), ":");
            int index = getIndex(a[0], newLines);
            if (index > -1)
                newLines.set(index, newLines.get(index).split(":")[0] + ":" + newS);
        });

        String versionLine = "FileVersion: ";

        newLines.set(getIndex(versionLine, newLines), versionLine + newVersion);
        writeFile(file, newLines);
        Bukkit.getConsoleSender().sendMessage("[SkyTax | "+ StringUtils.capitalize(FileName.replace(".yml","")) +"] "+StringUtils.capitalize(FileName.replace(".yml",""))+" updated!");
    }

    private void deprecateConfig() {
        Bukkit.getConsoleSender().sendMessage("[SkyTax | "+ StringUtils.capitalize(FileName.replace(".yml","")) +"] Now your "+FileName.replace(".yml","")+" is deprecated please check your folder for re-setting it!");
        String depName = "deprecated_"+FileName+"_" + LocalDate.now();
        File old = new File(file.getParentFile(), depName + ".yml");
        try {
            Files.copy(file.toPath(), old.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING );
            if (!file.delete()){
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&4[SkyTax | "+ StringUtils.capitalize(FileName.replace(".yml","")) +"] Can't delete deprecated file, remove it manually and restart the server"));
            }
        } catch (Exception e) {}

    }

    public String joinString(String[] text, String character) {
        return String.join(character,text);
    }

    public int getIndex(String line, List<String> list) {
        for (String s : list)
            if (s.startsWith(line) || s.equalsIgnoreCase(line))
                return list.indexOf(s);
        return -1;
    }

    public void writeFile(File file, List<String> toWrite) {
        try {
            Files.write(file.toPath(), toWrite, Charsets.UTF_8);
        } catch (Exception e) {}
    }

    public List<String> readFile(File file) {
        try {
            return Files.readAllLines(file.toPath(), Charsets.UTF_8);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<String> readInsideFile(String path) {
        try (InputStream in = plugin.getClass().getResourceAsStream(path);
             BufferedReader input = new BufferedReader(new InputStreamReader(in))) {
            return input.lines().collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

}
