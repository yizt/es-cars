package org.es.cars.es.model;

/**
 * Created by mick.yi on 2017/4/24.
 * 时间分组字段
 */
public class ESDateGroup extends ESQueryGroup{
    private String interval;//间隔
    private String format;//时间格式

    public String getInterval() {
        return interval;
    }

    public String getFormat() {
        return format;
    }

    public ESDateGroup(String field, String interval, String format) {
        super(field, GroupType.DATEHISTOGRAM);
        this.interval = interval;
        this.format = format;
    }
}
