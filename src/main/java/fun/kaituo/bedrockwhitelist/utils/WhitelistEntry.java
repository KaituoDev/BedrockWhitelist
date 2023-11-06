package fun.kaituo.bedrockwhitelist.utils;

import com.google.gson.annotations.Expose;

public class WhitelistEntry {

    @Expose
    private String uuid;
    @Expose
    private String name;

    public WhitelistEntry(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @SuppressWarnings("unused")
    public String getUuid() {
        return uuid;
    }
    @SuppressWarnings("unused")
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }
    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }
}
