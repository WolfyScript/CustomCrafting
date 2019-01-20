package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.DataSet;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import org.bukkit.plugin.Plugin;

import java.io.*;

public class ConfigHandler {

    private Plugin instance;
    private WolfyUtilities api;
    private ConfigAPI configAPI;
    private LanguageAPI languageAPI;

    private DataSet dataSet = new DataSet();

    public ConfigHandler(WolfyUtilities api){
        this.api = api;
        this.instance = api.getPlugin();
        this.configAPI = api.getConfigAPI();
        this.languageAPI = api.getLanguageAPI();
    }

    public void load(){
        configAPI.registerConfig(new Config(configAPI, "me/wolfyscript/customcrafting/configs", instance.getDataFolder().getPath(),"config"));
        configAPI.getConfig("config").loadDefaults();

        loadLang();
    }

    public void loadLang(){
        String chosenlang = configAPI.getConfig("config").getString("language");
        languageAPI.registerLanguage(new Language(chosenlang, new Config(configAPI, "me/wolfyscript/customcrafting/configs/lang", instance.getDataFolder().getPath()+"/lang", chosenlang), configAPI));
    }

    public void loadDataSet() throws IOException {
        File file = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "dataSet.dat");
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                api.sendConsoleMessage("Loading DataSet...");
                Object object = ois.readObject();
                if (object instanceof DataSet) {
                    dataSet = (DataSet) object;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ois.close();
        } else {
            dataSet = new DataSet();
        }
    }

    public void saveDataSet() {
        api.sendConsoleMessage("Saving DataSet...");
        try {
            FileOutputStream fos = new FileOutputStream(new File(CustomCrafting.getInst().getDataFolder() + File.separator + "dataSet.dat"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(dataSet);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
