package root.app.data.services.impl;

import com.google.common.collect.Lists;
import de.saxsys.javafx.test.JfxRunner;
import javafx.scene.layout.AnchorPane;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import root.app.model.Line;
import root.app.model.MarkersPair;
import root.app.model.Point;
import root.app.properties.LineConfigService;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static javafx.scene.paint.Color.DARKORANGE;
import static javafx.scene.paint.Color.RED;
import static org.junit.Assert.assertTrue;

/**
 * Created by maksym on 30.10.17.
 */
@RunWith(JfxRunner.class)
public class DrawingServiceImplTest {
    private static final Line completeLine = new Line(new Point(1.0, 2.0), new Point(3.0, 4.0));
    private static final Line completeLine2 = new Line(new Point(5.0, 6.0), new Point(7.0, 8.0));
    private MarkersPair pair = new MarkersPair(1L, completeLine, completeLine2);

    @Mock
    private LineConfigService lineProvider;

    @InjectMocks
    private DrawingServiceImpl drawingService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testLinesA_AddedAndFilledRed() throws Exception {
        //GIVEN
        AnchorPane anchorPane = new AnchorPane();

        //WHEN
        final ArrayList<MarkersPair> markerLines = Lists.newArrayList(pair, pair, pair);
        drawingService.showLines(anchorPane, markerLines);

        List<javafx.scene.shape.Line> fxLines = anchorPane.getChildren().stream()
                .filter(node -> node instanceof javafx.scene.shape.Line)
                .map(node -> (javafx.scene.shape.Line) node).collect(toList());

        long lineAmount = fxLines.size();

        assertTrue(lineAmount == markerLines.size() * 2); // each pair has 2 lines

        long redLines = fxLines.stream().filter(l -> RED.equals(l.getStroke())).count();
        long darkOrangeLines = fxLines.stream().filter(l -> DARKORANGE.equals(l.getStroke())).count();

        assertTrue(redLines == fxLines.size() / 2);
        assertTrue(darkOrangeLines == fxLines.size() / 2);
    }

    @Test
    public void removePair() throws Exception {
    }

    @Test
    public void drawLines() throws Exception {
    }

}