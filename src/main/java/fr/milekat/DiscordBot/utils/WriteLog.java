package fr.milekat.DiscordBot.utils;

import fr.milekat.DiscordBot.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class WriteLog {

    public WriteLog() throws IOException {
        File file = new File("logs.txt");
        if (!file.createNewFile()) {
            Files.move(Paths.get("logs.txt"), Paths.get("logs/logs_" + DateMileKat.setDateSysNow() + ".txt"));
            boolean success = file.createNewFile();
            if (success) Main.log("New log file create: " + file.getName());
        }
    }

    public void logger(String log) {
        try {
            Files.write(Paths.get("logs.txt"), (log + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
