package fr.milekat.discordbot.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class WriteLog {

    public WriteLog() throws IOException {
        File file = new File("logs.txt");
        if (!file.createNewFile()) {
            File logDirectory = new File("logs/");
            if (logDirectory.mkdir()) {
                System.out.println("logs directory created.");
            }
            Files.move(Paths.get("logs.txt"), Paths.get("logs/logs_" + DateMileKat.setDateSysNow() + ".txt"));
            boolean success = file.createNewFile();
            if (success) System.out.println("New log file create: " + file.getName());
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
