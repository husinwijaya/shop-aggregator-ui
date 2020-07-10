package net.sin.model;

import java.util.Objects;

public class ShopResult {
    private ECommercePlatform platform;
    private long id;
    private String name;
    private String url;

    public ECommercePlatform getPlatform() {
        return platform;
    }

    public void setPlatform(ECommercePlatform platform) {
        this.platform = platform;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShopResult)) return false;
        ShopResult that = (ShopResult) o;
        return id == that.id &&
                platform == that.platform;
    }

    @Override
    public int hashCode() {
        return Objects.hash(platform, id);
    }
}
