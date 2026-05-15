package UI.helper;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.Group;

public class OccupancyGauge extends StackPane {
    private Arc progressArc;
    private Text percentageText;

    public OccupancyGauge(double radius) {
        this(radius, 15);
    }

    public OccupancyGauge(double radius, double strokeWidth) {
        Arc track = new Arc(0, 0, radius, radius, 90, 360);
        track.setType(ArcType.OPEN);
        track.setStrokeWidth(strokeWidth);
        track.setFill(null);
        track.setStroke(Color.web("#935a0abb"));

        progressArc = new Arc(0, 0, radius, radius, 90, 0);
        progressArc.setType(ArcType.OPEN);
        progressArc.setStrokeWidth(strokeWidth);
        progressArc.setFill(null);
        progressArc.setStroke(Color.web("#dfc8a9")); 
        // Makes the ends of the arc rounded
        progressArc.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

        // 3. Percentage Text
        percentageText = new Text("0%");
        percentageText.setFont(Font.font("Segoe UI", FontWeight.BOLD, radius / 2));
        percentageText.setFill(Color.web("#dfc8a9"));
        percentageText.setBoundsType(javafx.scene.text.TextBoundsType.VISUAL);

        this.getStyleClass().add("occupancy-gauge");
        track.getStyleClass().add("gauge-track");
        progressArc.getStyleClass().add("gauge-progress");
        percentageText.getStyleClass().add("percentage-text");

        Group arcGroup = new Group(track, progressArc);

        // Adding them to the StackPane (Last added = Top layer)
        getChildren().addAll(arcGroup, percentageText);
    }

    public void setPercentage(double percent) {
        // JavaFX Arcs use degrees (360 degrees = 100%)
        double angle = -(percent / 100.0) * 360;
        progressArc.setLength(angle);
        percentageText.setText((int)percent + "%");

        // Color Logic
        if (percent > 90) progressArc.setStroke(Color.web("#f59f9f"));
        else if (percent > 70) progressArc.setStroke(Color.web("#fbc09b"));
        else progressArc.setStroke(Color.web("#dfc8a9"));
    }
}