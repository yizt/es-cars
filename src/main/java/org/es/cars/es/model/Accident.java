package org.es.cars.es.model;

/**
 * Created by mick.yi on 2017/6/16.
 */
public class Accident {
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Accident{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
