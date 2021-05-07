package ta4jexamples.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.MMAIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.num.Num;

public class DFastIndicator extends CachedIndicator<Num> {
    private final Indicator<Num> indicator;

    public DFastIndicator(KFastIndicator k, int barCount) {
        this(new MMAIndicator(k, barCount));
    }

    protected DFastIndicator(Indicator<Num> indicator) {
        super(indicator);
        this.indicator = indicator;
    }

    @Override
    protected Num calculate(int index) {
        return indicator.getValue(index);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + indicator;
    }
}
