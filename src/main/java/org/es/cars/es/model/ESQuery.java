package org.es.cars.es.model;

import java.util.List;

/**
 * Created by mick.yi on 2017/4/26.
 * es查询对象，构建含有子type的查询
 */
public class ESQuery {
    /**
     * 维度列表
     */
    private List<String> dimensions;
    /**
     * 子维度列表
     */
    private List<String> childDims;
    /**
     * type
     */
    private String type;
    /**
     * 子type
     */
    private String childType;

    public List<String> getDimensions() {
        return dimensions;
    }

    public List<String> getChildDims() {
        return childDims;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChildType() {
        return childType;
    }

    public ESQuery(List<String> dimensions, List<String> childDims, String type, String childType) {
        this.dimensions = dimensions;
        this.childDims = childDims;
        this.type = type;
        this.childType = childType;
    }

    public ESQuery(List<String> dimensions, List<String> childDims, String childType) {
        this(dimensions,childDims,null,childType);
    }

    public ESQuery(String type) {
        this(null,null,type,null);
    }
}
