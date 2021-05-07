package ta4jexamples.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.num.Num;

public class MACDHistogramIndicator extends CachedIndicator<Num> {
    private Indicator<Num> macdLine;
    private Indicator<Num> macdSignalLine;

    public MACDHistogramIndicator(Indicator<Num> indicator) {
        super(indicator);
        macdLine = new MACDIndicator(indicator);
        macdSignalLine = new EMAIndicator(macdLine, 9);
    }

    @Override
    protected Num calculate(int index) {
        return macdLine.getValue(index).minus(macdSignalLine.getValue(index));
    }
}
