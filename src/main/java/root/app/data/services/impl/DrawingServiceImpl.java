package root.app.data.services.impl;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.data.drawingTools.DrawLabel;
import root.app.data.drawingTools.ZoneLabel;
import root.app.data.services.DrawingService;
import root.app.data.services.ImageScaleService;
import root.app.data.services.ZoneComputingService;
import root.app.model.MarkersPair;
import root.app.model.RoadWay;
import root.app.properties.RoadWaysConfigService;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static root.app.data.services.ZoneComputingService.ZONE_PREFIX;
import static root.app.data.services.impl.ImageScaleServiceImpl.ScreenSize;

@Slf4j
@Service
public class DrawingServiceImpl implements DrawingService {
    public static final String LABEL_PREFIX = "pair_label_";
    private static final String LINE_ID = "line_";

    private final BiConsumer<RoadWay, AnchorPane> drawLabel = new DrawLabel();

    private final RoadWaysConfigService zoneConfigService;
    private final ImageScaleService scaleService;
    private final ZoneComputingService computingService;
    private ZoneLabel zoneLabel = new ZoneLabel();

    @Autowired
    public DrawingServiceImpl(RoadWaysConfigService zoneConfigService, ImageScaleService scaleService, ZoneComputingService computingService) {
        this.zoneConfigService = zoneConfigService;
        this.scaleService = scaleService;
        this.computingService = computingService;
    }

    @Override
    public void showLines(AnchorPane imageWrapperPane, List<MarkersPair> markerLines) {
        markerLines.forEach(line -> {
            List<Line> linesA = markerLines
                    .stream()
                    .map(pair -> {
                        Line lineA = new Line(
                                pair.getLineA().getStart().getX(),
                                pair.getLineA().getStart().getY(),
                                pair.getLineA().getEnd().getX(),
                                pair.getLineA().getEnd().getY());

                        lineA.setStroke(Color.RED);
                        lineA.setId(LINE_ID + pair.getId() + "_A");
                        return lineA;
                    }).collect(toList());

            List<Line> linesB = markerLines
                    .stream()
                    .map(pair -> {
                        Line lineB = new Line(
                                pair.getLineB().getStart().getX(),
                                pair.getLineB().getStart().getY(),
                                pair.getLineB().getEnd().getX(),
                                pair.getLineB().getEnd().getY());
                        lineB.setStroke(Color.DARKORANGE);
                        lineB.setId(LINE_ID + pair.getId() + "_B");
                        return lineB;
                    }).collect(toList());

            imageWrapperPane.getChildren().addAll(linesA);
            imageWrapperPane.getChildren().addAll(linesB);
        });

    }

    @Override
    public void showZones(AnchorPane imageWrapperPane, ScreenSize screenSize) {
        final ObservableList<Node> children = imageWrapperPane.getChildren();

        final List<RoadWay.Zone> allZones = zoneConfigService.findAllZones();

        if (allZones.isEmpty()) return;

        List<Polygon> polygon = computingService.toFxPolygons(allZones, screenSize);
        //if these way weren't added before
        if (children.filtered(node -> node.getId() != null && node.getId().equals(allZones.get(0).getId())).size() == 0) {
            children.addAll(polygon);
            final List<Label> labels = allZones.stream().map(zone -> zoneLabel.apply(zone)).collect(toList());
            children.addAll(labels);
        }

    }

    private void removeChildZone(AnchorPane imageWrapperPane, RoadWay.Zone child) {
        Predicate<Node> isZoneToDelete = node -> node instanceof Polygon && (child.getId().equals(node.getId()));
        final ObservableList<Node> children = imageWrapperPane.getChildren();

        children.removeIf(isZoneToDelete);
    }

    private void removePair(AnchorPane imageWrapperPane, MarkersPair pair) {
        Predicate<Node> isLineToDelete = node -> {
            return node instanceof Line
                    && (((Line) node).getEndX() == pair.getLineB().getEnd().getX()
                    || ((Line) node).getEndX() == pair.getLineA().getEnd().getX());
        };

        final ObservableList<Node> children = imageWrapperPane.getChildren();

        children.removeIf(isLineToDelete);
        children.removeIf(node -> (LABEL_PREFIX + pair.getId()).equals(node.getId()));
    }

    @Override
    public void submitRegion(MarkersPair pair) {
        zoneConfigService.saveAll(computingService.getRoadWays(pair));
        log.info("Saved new region");
    }

    @Override
    public void clearAll(AnchorPane imageWrapperPane) {
        final ObservableList<Node> children = imageWrapperPane.getChildren();
        children.removeIf(node -> node.getId() != null && node.getId().contains(LABEL_PREFIX));
        children.removeIf(node -> node.getId() != null && node.getId().contains(ZONE_PREFIX));
        children.removeIf(node -> node.getId() != null && node.getId().contains(LINE_ID));
    }

}
