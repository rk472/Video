package com.swadeshiapps.video.POJO;

public class Videos {
    String path,thumb;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Videos() {
    }

    public Videos(String path, String thumb) {

        this.path = path;
        this.thumb = thumb;

    }
}
