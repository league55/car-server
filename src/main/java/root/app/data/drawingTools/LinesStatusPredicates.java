package root.app.data.drawingTools;

import root.app.model.MarkersPair;

import java.util.function.Predicate;

/**
 * Used to define line status while building markers pair
 */
public class LinesStatusPredicates {
    public static final Predicate<MarkersPair> COMPLETE_PAIRS = pair -> {
        return pair != null
                && pair.getLineA() != null && pair.getLineA().getEnd() != null
                && pair.getLineB() != null && pair.getLineB().getEnd() != null;
    };
    public static final Predicate<MarkersPair> LINE_A_WITHOUT_END = pair -> {
        return pair.getLineA() != null && pair.getLineA().getEnd() == null;
    };
    public static final Predicate<MarkersPair> LINE_B_WITHOUT_END = pair -> {
        return pair.getLineB() != null && pair.getLineB().getEnd() == null;
    };
    public static final Predicate<MarkersPair> LINE_A_COMPLETE = (Predicate<MarkersPair>) pair -> {
        return pair.getLineA() != null && pair.getLineB() == null;
    };

}
