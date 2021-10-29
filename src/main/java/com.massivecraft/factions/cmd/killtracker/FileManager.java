package com.massivecraft.factions.cmd.killtracker;

import com.massivecraft.factions.P;

import java.io.File;

/**
 * @author Saser
 */
public class FileManager {


    private CustomFile messages = new CustomFile(new File(P.p.getDataFolder()+"/data.yml"));


    public void setupFiles(){
        messages.setup(true, "");
    }


    public CustomFile getMessages() {
        return messages;
    }
}
