package root.app.data.services.impl;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.data.drawingTools.DrawLabel;
import root.app.data.services.DrawingService;
import root.app.data.services.ZoneComputingService;
import root.app.model.MarkersPair;
import root.app.model.Zone;
import root.app.properties.ConfigService;
import root.app.properties.LineConfigService;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static root.app.data.services.ZoneComputingService.ZONE_PREFIX;

@Slf4j
@Service
public class DrawingServiceImpl implements DrawingService {
    public static final String LABEL_PREFIX = "pair_label_";

    private static MarkersPair pairInProgress;
    private final BiConsumer<Zone, AnchorPane> drawLabel = new DrawLabel();

    private final LineConfigService lineProvider;
    private final ConfigService zoneConfigService;
    private final ZoneComputingService computingService;

    @Autowired
    public DrawingServiceImpl(LineConfigService lineProvider, ConfigService zoneConfigService, ZoneComputingService computingService) {
        this.lineProvider = lineProvider;
        this.zoneConfigService = zoneConfigService;
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
                        return lineB;
                    }).collect(toList());

            imageWrapperPane.getChildren().addAll(linesA);
            imageWrapperPane.getChildren().addAll(linesB);
        });

    }

    @Override
    public void showZones(AnchorPane imageWrapperPane, List<Zone> zones) {
        final ObservableList<Node> children = imageWrapperPane.getChildren();

        zones.forEach(parentZone -> {
            List<Polygon> polygon = computingService.toFxPolygon(parentZone);
            //if these zones weren't added before
            if (children.filtered(node -> node.getId() != null && node.getId().equals(parentZone.getChildZones().get(0).getId())).size() == 0) {
                children.addAll(polygon);
            }

            drawLabel.accept(parentZone, imageWrapperPane);

        });
    }

    @Override
    public void removeZone(AnchorPane imageWrapperPane, Zone zone) {
        MarkersPair pair = zone.getPair();
        final ObservableList<Node> children = imageWrapperPane.getChildren();

        removePair(imageWrapperPane, pair);
        zone.getChildZones().forEach(child -> removePair(imageWrapperPane, child.getPair()));
        zone.getChildZones().forEach(child -> removeChildZone(imageWrapperPane, child));
        children.removeIf(node -> node.getId() != null && node.getId().contains(ZONE_PREFIX + zone.getId()));
    }

    private void removeChildZone(AnchorPane imageWrapperPane, Zone.ChildZone child) {
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
    public void submitZone(List<MarkersPair> pairs, MarkersPair pair, Pane pane) {
        final Long pairId = lineProvider.save(pair);
        zoneConfigService.save(getParentZone(lineProvider.findOne(pairId)));
        pairs.add(pairInProgress);
        log.info("Saved new line with ID {}", pairId);
    }

    private Zone getParentZone(MarkersPair pair) {
        return new Zone(pair, computingService.getChildZones(pair));
    }

}
