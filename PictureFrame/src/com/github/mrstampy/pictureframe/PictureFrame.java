package com.github.mrstampy.pictureframe;

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
import javafx.scene.input.ZoomEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		addHeightListener(primaryStage);
		addWidthListener(primaryStage);
		addFullScreenListener(primaryStage);
		addCtrlXListener(primaryStage);
		addXPosListener(primaryStage);
		addYPosListener(primaryStage);
		addZoomListener(scene);
		primaryStage.addEventHandler(MouseEvent.ANY, pictureView.getMouseEventHandler());
		
		primaryStage.initStyle(StageStyle.UNIFIED);
		if (settings.isFullScreen()) {
			primaryStage.setFullScreen(true);
			fullScreenPrep(primaryStage);
		} else {
			primaryStage.setHeight(settings.getHeight());
			primaryStage.setWidth(settings.getWidth());

			if (settings.getX() == 0.0) {
				primaryStage.centerOnScreen();
			} else {
				primaryStage.setX(settings.getX());
				primaryStage.setY(settings.getY());
			}
		}

		primaryStage.show();

		if (settings.isFullScreen()) fullScreenPrep(primaryStage);
	}

	private void addZoomListener(Scene scene) {
		scene.addEventHandler(ZoomEvent.ZOOM, new EventHandler<ZoomEvent>() {

			@Override
			public void handle(ZoomEvent event) {
				pictureView.zoom(event.getZoomFactor());
			}
		});
	}

	private void addYPosListener(Stage primaryStage) {
		primaryStage.yProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				settings.setY(newValue.doubleValue());
			}
		});
	}

	private void addXPosListener(Stage primaryStage) {
		primaryStage.xProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				settings.setX(newValue.doubleValue());
			}
		});
	}

	private void addCtrlXListener(Stage primaryStage) {
		primaryStage.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.isShortcutDown() && "x".equalsIgnoreCase(event.getCharacter())) {
					try {
						stop();
					} catch (Exception e) {
						log.error("Error on stop", e);
					}
				}
			}
		});
	}

	private void addFullScreenListener(Stage primaryStage) {
		primaryStage.fullScreenProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				changeFullScreen(newValue);
			}
		});
	}

	private void addWidthListener(Stage primaryStage) {
		primaryStage.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				pictureView.setWidth(newValue.doubleValue());
				settings.setWidth(newValue.doubleValue());
			}
		});
	}

	private void addHeightListener(Stage primaryStage) {
		primaryStage.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				pictureView.setHeight(newValue.doubleValue());
				settings.setHeight(newValue.doubleValue());
			}
		});
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
				} catch (InterruptedException e) {
				}

				if (exiting) return;

				settings.setFullScreen(newValue);
				if (!newValue) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							Rectangle2D rect = Screen.getPrimary().getVisualBounds();
							if (settings.getWidth() == rect.getWidth()) {
								PictureFrame.primaryStage.setWidth(settings.getWidth() / 2);
								PictureFrame.primaryStage.setHeight(settings.getHeight() / 2);
								PictureFrame.primaryStage.centerOnScreen();
							}
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
