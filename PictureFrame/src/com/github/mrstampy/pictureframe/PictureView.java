package com.github.mrstampy.pictureframe;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PictureView {
  private static final Logger log = LoggerFactory.getLogger(PictureView.class);

  private Timer timer;

  private PictureScanner scanner = new PictureScanner();

  private ImageView view1 = new ImageView();
  private ImageView view2 = new ImageView();
  private Rectangle r;

  private FadeTransition fade1 = new FadeTransition();
  private FadeTransition fade2 = new FadeTransition();
  
  private FadeTransition fromFade;
  private FadeTransition toFade;

  private FillTransition fillTransition;

  private StackPane stackPane = new StackPane(view2, view1);

  private long duration = 3;
  private long transition = 3;

  private Random rand = new Random(System.nanoTime());

  private VBox vbox = new VBox(stackPane);

  private volatile boolean running;

  public PictureView() {
    init();
  }

  public void setDirectory(File directory) {
    scanner.setDirectory(directory);
  }

  public void start() {
    log.debug("start");
    running = true;
    
    scanner.scan();

    startImpl();
  }

  private void startImpl() {
    log.debug("startImpl");
    Platform.runLater(new Runnable() {

      @Override
      public void run() {
        transition();
        transitionBackground();
      }
    });
  }

  private void schedule() {
    log.debug("Scheduling switch");
    if (timer == null) timer = new Timer("PictureFrame image switch timer", true);

    timer.schedule(new TimerTask() {

      @Override
      public void run() {
        if(running) switchImages();
      }
    }, getDuration() * 1000);
  }

  private void switchImages() {
    Platform.runLater(new Runnable() {

      @Override
      public void run() {
        transition();
      }
    });
  }

  public void stop() {
    log.debug("stop");
    running = false;
    if (timer != null) {
      timer.cancel();
      timer = null;
    }

    stopAnimations();
    reset();
  }

  private void reset() {
    fillTransition.setToValue(Color.WHITE);
    fillTransition.playFromStart();
  }

  private void stopAnimations() {
    fillTransition.stop();
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public long getTransition() {
    return transition;
  }

  public void setTransition(long transition) {
    this.transition = transition;
  }

  public Parent getView() {
    return vbox;
  }

  private void transition() {
    setFadeDirection();
    
    fromFade.stop();
    toFade.stop();
    
    setImage((ImageView) toFade.getNode());

    fromFade.setDelay(Duration.seconds(getTransition()));
    fromFade.setToValue(0.0);
    fromFade.play();

    toFade.setDelay(Duration.seconds(getTransition()));
    toFade.setToValue(1.0);
    toFade.play();
  }

  private void setFadeDirection() {
    if(fromFade == null) {
      fromFade = fade1;
      toFade = fade2;
    } else {
      FadeTransition tmp = fromFade;
      fromFade = toFade;
      toFade = tmp;
    }
  }

  private void setImage(ImageView view) {
    try {
      view.setOpacity(0.0);
      Image image = scanner.getRandomImage();
      view.setImage(image);
    } catch (FileNotFoundException e) {
      log.error("Could not load random image", e);
    }
  }

  private void init() {
    view2.setOpacity(0.0);
    view1.setPreserveRatio(true);
    view2.setPreserveRatio(true);

    fade1.setNode(view1);
    fade1.setDuration(Duration.seconds(getTransition()));
    fade1.setInterpolator(Interpolator.LINEAR);

    fade2.setNode(view2);
    fade2.setDuration(Duration.seconds(getTransition()));
    fade2.setInterpolator(Interpolator.LINEAR);
    
    fade1.setOnFinished(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        log.debug("schedule");
        schedule();
      }
    });

    r = getRectangle();
    stackPane.getChildren().add(0, r);

    fillTransition = new FillTransition(Duration.seconds(3), r);
    fillTransition.setOnFinished(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        if (!running) return;
        transitionBackground();
      }
    });
  }

  public void setHeight(double height) {
    view1.setFitHeight(height);
    view2.setFitHeight(height);
    r.setHeight(height);
  }

  public void setWidth(double width) {
    r.setWidth(width);
  }

  public EventHandler<MouseEvent> getMouseEventHandler() {
    return new EventHandler<MouseEvent>() {
      
      private Thread singleClick;

      @Override
      public void handle(MouseEvent event) {
//        log.debug("{}", event);
        if (MouseEvent.MOUSE_PRESSED == event.getEventType()) {
          handleMousePressed(event);
        } else if (MouseEvent.DRAG_DETECTED == event.getEventType()) {
          next();
        }
      }

      private void handleMousePressed(MouseEvent event) {
        if (event.getClickCount() == 2) {
          stopThread();
          if (running) {
            stop();
          } else {
            start();
          }
        } else if (event.getButton() == MouseButton.SECONDARY) {
          showDirectoryChooser();
        } else if(event.getClickCount() == 1) {
          startThread();
        }
      }
      
      private void startThread() {
        stopThread();
        singleClick = new Thread("Single click detector") {
          public void run() {
            try {
              Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            Platform.runLater(new Runnable() {
              
              @Override
              public void run() {
                next();
              }
            });
          }
        };
        singleClick.start();
      }
      
      @SuppressWarnings("deprecation")
      private void stopThread() {
        if(singleClick == null || !singleClick.isAlive()) return;
        
        singleClick.stop();
      }

      private void showDirectoryChooser() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Picture Directories");
        chooser.setInitialDirectory(scanner.getDirectory());

        File chosen = chooser.showDialog(null);
        if (chosen != null) scanner.setDirectory(chosen);
      }
    };
  }

  private void next() {
    boolean b = running;
    stop();
    running = b;
    if(running) {
      startImpl();
    } else {
      transition();
    }
  }

  private void transitionBackground() {
    fillTransition.setToValue(new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), rand.nextDouble()));
    fillTransition.playFromStart();
  }

  private Rectangle getRectangle() {
    Rectangle r = new Rectangle(stackPane.getWidth(), stackPane.getHeight());

    r.setFill(Color.WHITE);

    return r;
  }

}
