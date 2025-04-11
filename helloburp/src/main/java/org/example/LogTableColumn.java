package org.example;

import javax.swing.table.TableColumn;

public class LogTableColumn extends TableColumn implements Comparable<LogTableColumn> {
    private String name;
    private int order;
    private boolean readOnly;
    private String description;
    private String visibleName;

    @Override
    public void setPreferredWidth(int width) {
        super.setPreferredWidth(width);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        this.setPreferredWidth(width);
    }

    public int compareTo(LogTableColumn o) {
        return Integer.compare(this.order, o.order);
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVisibleName(String visibleName) {
        this.visibleName = visibleName;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getHeaderValue() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

}
