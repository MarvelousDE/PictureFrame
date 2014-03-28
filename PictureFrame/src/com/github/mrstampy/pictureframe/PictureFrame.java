package com.github.mrstampy.pictureframe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PictureFrame extends Application {
	private static final Logger log = LoggerFactory.getLogger(PictureFrame.class);

	private Settings settings = new Settings();
	private PictureView pictureView = new PictureView(settings);

	static Stage primaryStage;

	private boolean exiting = false;

	public void start(Stage primaryStage) throws Exception {
		PictureFrame.primaryStage = primaryStage;
		
		primaryStage.setTitle("Picture Frame");

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

		primaryStage.fullScreenProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				changeFullScreen(newValue);
			}
		});
		
		primaryStage.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if(event.isShortcutDown() && "x".equalsIgnoreCase(event.getCharacter())) {
					try {
						stop();
					} catch (Exception e) {
						log.error("Error on stop", e);
					}
				}
			}
		});

		primaryStage.addEventFilter(MouseEvent.ANY, pictureView.getMouseEventHandler());
		primaryStage.initStyle(StageStyle.UNIFIED);
		if (settings.isFullScreen()) {
			primaryStage.setFullScreen(true);
			fullScreenPrep(primaryStage);
		} else {
			primaryStage.setHeight(settings.getHeight());
			primaryStage.setWidth(settings.getWidth());
			primaryStage.centerOnScreen();
		}

		primaryStage.show();

		if (settings.isFullScreen()) fullScreenPrep(primaryStage);
	}

	private void fullScreenPrep(Stage primaryStage) {
		Rectangle2D rect = Screen.getPrimary().getVisualBounds();
		primaryStage.setWidth(rect.getWidth());
		primaryStage.setHeight(rect.getHeight() + (isMac() ? 27 : 0));
	}

	private boolean isMac() {
		return System.getProperty("os.name").contains("Mac");
	}

	public void stop() throws Exception {
		exiting = true;
		pictureView.stop();
		System.exit(0);
	}

	private void changeFullScreen(final Boolean newValue) {
		Thread thread = new Thread() {
			public void run() {
				try {
					Thread.sleep(500);
				} catch(InterruptedException e) {
				}
				
				if(exiting) return;
				
				settings.setFullScreen(newValue);
				if (!newValue) {
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							PictureFrame.primaryStage.setWidth(settings.getWidth() / 2);
							PictureFrame.primaryStage.setHeight(settings.getHeight() / 2);
						}
					});
				}
			}
		};
		
		thread.start();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
