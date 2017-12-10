package root.app.data.services.impl;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
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

@Slf4j
@Service
public class DrawingServiceImpl implements DrawingService {
    public static final String LABEL_PREFIX = "pair_label_";
    public static final String ZONE_PREFIX = "zone_";

    private static MarkersPair pairInProgress;
    private final BiConsumer<MarkersPair, AnchorPane> drawLabel = new DrawLabel();

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

            markerLines.forEach(pair -> drawLabel.accept(pair, imageWrapperPane));
        });

    }

    @Override
    public void showZones(AnchorPane imageWrapperPane, List<Zone> zones) {
        List<Polygon> polygon = zones.stream().map(computingService::toFxPolygon).collect(toList());
        imageWrapperPane.getChildren().addAll(polygon);

    }

    @Override
    public void removeZone(AnchorPane imageWrapperPane, Zone zone) {
        MarkersPair pair = zone.getPair();
        Predicate<Node> isLineToDelete = node -> {
            return node instanceof Line
                    && (((Line) node).getEndX() == pair.getLineB().getEnd().getX()
                    || ((Line) node).getEndX() == pair.getLineA().getEnd().getX());
        };
        imageWrapperPane.getChildren().removeIf(isLineToDelete);
        imageWrapperPane.getChildren().removeIf(node -> (LABEL_PREFIX + pair.getId()).equals(node.getId()));
        imageWrapperPane.getChildren().removeIf(node -> (ZONE_PREFIX + zone.getId()).equals(node.getId()));
    }

    @Override
    public root.app.model.Line drawLines(List<MarkersPair> pairs, root.app.model.Point point) {

        if (pairInProgress == null || pairInProgress.getLineB() != null && pairInProgress.getLineB().getEnd() != null) {
            pairInProgress = new MarkersPair();
            root.app.model.Line lineA = new root.app.model.Line();
            lineA.setStart(point);
            pairInProgress.setLineA(lineA);
            return null;
        } else if (pairInProgress.getLineA().getEnd() == null) {
            pairInProgress.getLineA().setEnd(point);
            return pairInProgress.getLineA();
        } else if (pairInProgress.getLineB() == null) {
            root.app.model.Line lineB = new root.app.model.Line();
            lineB.setStart(point);
            pairInProgress.setLineB(lineB);
            return null;
        } else {
            pairInProgress.getLineB().setEnd(point);

            final Long pairId = lineProvider.save(pairInProgress);
            zoneConfigService.save(getZone(lineProvider.findOne(pairId)));
            pairs.add(pairInProgress);
            log.info("Saved new line with ID {}", pairId);

            return pairInProgress.getLineB();
        }
    }

    private Zone getZone(MarkersPair pair) {
        return Zone.builder()
                .isParent(true)
                .pair(pair)
                .build();
    }

}
