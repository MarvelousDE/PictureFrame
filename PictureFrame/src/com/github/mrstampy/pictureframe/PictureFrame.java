package com.github.mrstampy.pictureframe;

import java.io.File;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PictureFrame extends Application {

	private PictureView pictureView = new PictureView();

	public void start(Stage primaryStage) throws Exception {
		pictureView.setDirectory(new File("/Users/burton/Fandom"));

		Parent pane = pictureView.getView();

		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);

		primaryStage.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				pictureView.setHeight(newValue.doubleValue());
			}
		});

		primaryStage.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				pictureView.setWidth(newValue.doubleValue());
			}
		});

		primaryStage.addEventFilter(MouseEvent.ANY, pictureView.getMouseEventHandler());
		primaryStage.initStyle(StageStyle.UNIFIED);
		primaryStage.setHeight(1000);
		primaryStage.setWidth(1000);
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
