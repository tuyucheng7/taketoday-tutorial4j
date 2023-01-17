module cn.tuyucheng.taketoday {
    requires javafx.controls;
    requires javafx.fxml;
	requires org.slf4j;

    requires org.controlsfx.controls;

    opens cn.tuyucheng.taketoday to javafx.fxml;
	opens cn.tuyucheng.taketoday.view to javafx.fxml;
	opens cn.tuyucheng.taketoday.model to javafx.base;
    exports cn.tuyucheng.taketoday;
}