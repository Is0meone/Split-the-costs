module pl.edu.pw.app {
	requires javafx.controls;
	requires javafx.fxml;

	requires org.controlsfx.controls;
	requires com.dlsc.formsfx;
	requires org.kordamp.ikonli.javafx;
	requires org.kordamp.bootstrapfx.core;

	opens pl.edu.pw.app to javafx.fxml;
	exports pl.edu.pw.app;
}