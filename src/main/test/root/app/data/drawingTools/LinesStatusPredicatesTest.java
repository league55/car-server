package root.app.data.drawingTools;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import root.app.model.Line;
import root.app.model.MarkersPair;
import root.app.model.Point;

import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static root.app.data.drawingTools.LinesStatusPredicates.*;

/**
 * Created by maksym on 30.10.17.
 */
@RunWith(JUnitParamsRunner.class)
public class LinesStatusPredicatesTest {

    @Test
    @Parameters(method = "testData")
    public void testAllPairsComplete(MarkersPair pair, Predicate<MarkersPair> positivePredicate) throws Exception {

        assertEquals(positivePredicate.equals(COMPLETE_PAIRS), COMPLETE_PAIRS.test(pair));
    }

    @Test
    @Parameters(method = "testData")
    public void testAWithoutEnd(MarkersPair pair, Predicate<MarkersPair> positivePredicate) throws Exception {

        assertEquals(positivePredicate.equals(LINE_A_WITHOUT_END), LINE_A_WITHOUT_END.test(pair));
    }

    @Test
    @Parameters(method = "testData")
    public void testAComplete(MarkersPair pair, Predicate<MarkersPair> positivePredicate) throws Exception {

        assertEquals(positivePredicate.equals(LINE_A_COMPLETE), LINE_A_COMPLETE.test(pair));
    }

    @Test
    @Parameters(method = "testData")
    public void testBWithoutEnd(MarkersPair pair, Predicate<MarkersPair> positivePredicate) throws Exception {

        assertEquals(positivePredicate.equals(LINE_B_WITHOUT_END), LINE_B_WITHOUT_END.test(pair));
    }


    private Object[] testData() {
        final Line completeLine = new Line(new Point(0.0, 0.0), new Point(0.0, 0.0));
        final Line lineWithoutEnd = new Line(new Point(0.0, 0.0), null);

        return new Object[]{
                new Object[]{new MarkersPair(1L, completeLine, completeLine), COMPLETE_PAIRS},
                new Object[]{new MarkersPair(1L, lineWithoutEnd, completeLine), LINE_A_WITHOUT_END},
                new Object[]{new MarkersPair(1L, completeLine, null), LINE_A_COMPLETE},
                new Object[]{new MarkersPair(1L, completeLine, lineWithoutEnd), LINE_B_WITHOUT_END}
        };
    }
}