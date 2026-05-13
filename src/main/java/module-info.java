module drinkshop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;

    requires org.controlsfx.controls;

    opens drinkshop.ui to javafx.fxml;
    exports drinkshop.ui;

    exports drinkshop.domain;
    opens drinkshop.repository to org.mockito, net.bytebuddy;
    opens drinkshop.service to org.mockito, net.bytebuddy;
    opens drinkshop.domain to javafx.base, org.mockito, net.bytebuddy;
}