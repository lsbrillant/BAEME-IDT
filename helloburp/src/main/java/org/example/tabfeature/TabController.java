package org.example.tabfeature;

import lombok.Getter;
import org.example.logtable.LogTableController;

public class TabController {
    @Getter
    private TabModel model;
    @Getter
    private TabView view;
    @Getter
    private LogTableController logTableController;

    public TabController(LogTableController logTableController) {
        this.model = new TabModel(this);
        this.view = new TabView(this);
        this.logTableController = logTableController;
        this.view.resetFilter(); // auto-select the Dashboard tab on table creation
    }
}
