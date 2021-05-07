package ta4jexamples.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.num.Num;

public class KSlowIndicator extends CachedIndicator<Num> {
    private final Indicator<Num> indicator;

    public KSlowIndicator(StochasticOscillatorKIndicator k, int barCount) {
        this(new EMAIndicator(new EMAIndicator(k, barCount), barCount));
    }

    protected KSlowIndicator(Indicator<Num> indicator) {
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
