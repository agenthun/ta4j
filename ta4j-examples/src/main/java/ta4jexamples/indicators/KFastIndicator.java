package ta4jexamples.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MMAIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.num.Num;

/**
 * (6,3,3): 对价格波动的敏感性得到加强，它变动的频率非常高，适合于短线客寻找买点和卖点
 * (9,3,3): 系统默认参数
 * (18,3,3): 具有信号稳定而且灵敏度不低的优点，在大多数情况下都比较适用
 * (24,3,3): 在更大程度上排除了价格波动所产生的虚假信号，用它来寻找价格的中线买点或者是中线卖点是一个比较好的选择
 */
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
