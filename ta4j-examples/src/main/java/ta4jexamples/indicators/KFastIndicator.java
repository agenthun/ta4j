package ta4jexamples.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MMAIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.num.Num;

public class KFastIndicator extends CachedIndicator<Num> {
    private final Indicator<Num> indicator;

    public KFastIndicator(StochasticOscillatorKIndicator k, int barCount) {
        this(new MMAIndicator(k, barCount));
    }

    protected KFastIndicator(Indicator<Num> indicator) {
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
