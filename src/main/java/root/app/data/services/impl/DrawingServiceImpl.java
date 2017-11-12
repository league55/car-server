package root.app.data.services.impl;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.data.drawingTools.DrawLabel;
import root.app.data.services.DrawingService;
import root.app.model.MarkersPair;
import root.app.properties.LineConfigService;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static root.app.data.drawingTools.LinesStatusPredicates.*;

@Slf4j
@Service
public class DrawingServiceImpl implements DrawingService {
    public static final String LABEL_PREFIX = "pair_label_";

    private final BiConsumer<MarkersPair, AnchorPane> drawLabel = new DrawLabel();

    private final LineConfigService lineProvider;

    @Autowired
    public DrawingServiceImpl(LineConfigService lineProvider) {
        this.lineProvider = lineProvider;
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
    public void removePair(AnchorPane imageWrapperPane, MarkersPair pair) {
        Predicate<Node> isLineToDelete = node -> {
            return node instanceof Line
                    && (((Line) node).getEndX() == pair.getLineB().getEnd().getX()
                    || ((Line) node).getEndX() == pair.getLineA().getEnd().getX());
        };
        imageWrapperPane.getChildren().removeIf(isLineToDelete);
        imageWrapperPane.getChildren().removeIf(node -> (LABEL_PREFIX + pair.getId()).equals(node.getId()));
    }

    @Override
    public root.app.model.Line drawLines(List<MarkersPair> pairs, root.app.model.Point point) {
        boolean allPairsComplete = pairs.stream().filter(COMPLETE_PAIRS).count() == pairs.size();
        Optional<MarkersPair> firstNotNullA_WithoutEnd = pairs.stream().filter(LINE_A_WITHOUT_END).findFirst();
        Optional<MarkersPair> firstNotNullA_NullB = pairs.stream().filter(LINE_A_COMPLETE).findFirst();
        Optional<MarkersPair> firstNotNullB_WithoutEnd = pairs.stream().filter(LINE_B_WITHOUT_END).findFirst();

        if (pairs.isEmpty() || allPairsComplete) {
            MarkersPair pair = new MarkersPair();
            root.app.model.Line lineA = new root.app.model.Line();
            lineA.setStart(point);
            pair.setLineA(lineA);
            pairs.add(pair);
            return null;
        } else if (firstNotNullA_WithoutEnd.isPresent()) {
            root.app.model.Line lineA = firstNotNullA_WithoutEnd.get().getLineA();
            lineA.setEnd(point);
            return lineA;

        } else if (firstNotNullA_NullB.isPresent()) {
            root.app.model.Line lineB = new root.app.model.Line();
            lineB.setStart(point);
            firstNotNullA_NullB.get().setLineB(lineB);
            return null;
        } else if (firstNotNullB_WithoutEnd.isPresent()) {
            MarkersPair pair = firstNotNullB_WithoutEnd.get();
            root.app.model.Line lineB = pair.getLineB();
            lineB.setEnd(point);

            lineProvider.save(pair);
            log.info("Saved new line with ID {}", pair.getId());
            return lineB;
        }
        throw new IllegalStateException("Can't determine state of line pair!");
    }

}
