package com.github.mrstampy.pictureframe;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PictureFrame extends Application {

	private Settings settings = new Settings();
	private PictureView pictureView = new PictureView(settings);

	static Stage primaryStage;

	public void start(Stage primaryStage) throws Exception {
		PictureFrame.primaryStage = primaryStage;

		Parent pane = pictureView.getView();

		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);

		primaryStage.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				pictureView.setHeight(newValue.doubleValue());
				settings.setHeight(newValue.doubleValue());
			}
		});

		primaryStage.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				pictureView.setWidth(newValue.doubleValue());
				settings.setWidth(newValue.doubleValue());
			}
		});

		primaryStage.addEventFilter(MouseEvent.ANY, pictureView.getMouseEventHandler());
		primaryStage.initStyle(StageStyle.UNIFIED);
		primaryStage.setHeight(settings.getHeight());
		primaryStage.setWidth(settings.getWidth());
		primaryStage.centerOnScreen();
		primaryStage.show();
	}

	public void stop() throws Exception {
		pictureView.stop();
		System.exit(0);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
