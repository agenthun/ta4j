/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014-2017 Marc de Verdelhan, 2017-2021 Ta4j Organization & respective
 * authors (see AUTHORS)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ta4jexamples.indicators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ta4j.core.*;
import org.ta4j.core.analysis.criteria.pnl.AverageProfitCriterion;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.PriceVariationIndicator;
import org.ta4j.core.indicators.helpers.TypicalPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;

import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import ta4jexamples.loaders.CsvBarsLoader;

/**
 * This class builds a CSV file containing values from indicators.
 */
public class IndicatorsToCsv {

    public static void main(String[] args) {

        /*
         * Getting bar series
         */
        BarSeries series = CsvBarsLoader.loadAppleIncSeries();

        /*
         * Creating indicators
         */
        // Close price
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        // Typical price
        TypicalPriceIndicator typicalPrice = new TypicalPriceIndicator(series);
        // Price variation
        PriceVariationIndicator priceVariation = new PriceVariationIndicator(series);
        // Simple moving averages
        SMAIndicator shortSma = new SMAIndicator(closePrice, 8);
        SMAIndicator longSma = new SMAIndicator(closePrice, 20);
        // Exponential moving averages
        EMAIndicator shortEma = new EMAIndicator(closePrice, 8);
        EMAIndicator longEma = new EMAIndicator(closePrice, 20);
        // Percentage price oscillator
        PPOIndicator ppo = new PPOIndicator(closePrice, 12, 26);
        // Rate of change
        ROCIndicator roc = new ROCIndicator(closePrice, 100);
        // Relative strength index
        RSIIndicator rsi = new RSIIndicator(closePrice, 6);
        MACDIndicator macd = new MACDIndicator(closePrice);

        StochasticOscillatorKIndicator rsv = new StochasticOscillatorKIndicator(series, 9);
        KFastIndicator kFast = new KFastIndicator(rsv, 3);
        DFastIndicator dFast = new DFastIndicator(kFast, 3);
        KSlowIndicator kSlow = new KSlowIndicator(rsv, 3);
        DSlowIndicator dSlow = new DSlowIndicator(kSlow, 3);

        Rule buyingRule = new CrossedUpIndicatorRule(kSlow, dSlow);
        Rule sellingRule = new CrossedDownIndicatorRule(kSlow, dSlow);
        Strategy strategySKDJ = new BaseStrategy("SKDJ", buyingRule, sellingRule);
        Strategy strategyKDJ = new BaseStrategy("KDJ", new CrossedUpIndicatorRule(kFast, dFast), new CrossedDownIndicatorRule(kFast, dFast));
        Strategy strategyRSI = new BaseStrategy("RSI", new CrossedDownIndicatorRule(rsi, 20), new CrossedUpIndicatorRule(rsi, 80));

        BarSeriesManager seriesManager = new BarSeriesManager(series);
        AnalysisCriterion criterion = new AverageProfitCriterion();
        Strategy strategyBest = criterion.chooseBest(seriesManager, Arrays.asList(strategySKDJ, strategyKDJ, strategyRSI));
        TradingRecord tradingRecord = seriesManager.run(strategyBest);
        Logger.getLogger(IndicatorsToCsv.class.getName()).log(Level.INFO, strategyBest.getName() + ": " + tradingRecord.getLastEntry());


        // Williams %R
        WilliamsRIndicator williamsR = new WilliamsRIndicator(series, 20);
        // Average true range
        ATRIndicator atr = new ATRIndicator(series, 20);
        // Standard deviation
        StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 14);

        /*
         * Building header
         */
        StringBuilder sb = new StringBuilder(
                "timestamp,close,typical,variation,sma8,sma20,ema8,ema20,ppo,roc,rsi,macd,kFast,dFast,kSlow,dSlow,williamsr,atr,sd\n");

        StringBuilder sbLog = new StringBuilder("\n");
        for (int i = 0; i < series.getBarCount(); i++) {
            Bar item = series.getBar(i);
            Num k = kFast.getValue(i);
            Num d = dFast.getValue(i);
            Num k2 = kSlow.getValue(i);
            Num d2 = dSlow.getValue(i);
            sbLog.append("[").append(item.getSimpleDateName()).append("] ")
                    .append("[").append(i).append("] ")
                    .append(series.getBar(i).getClosePrice()).append(", ")
                    .append(k).append(", ").append(d)
                    .append(" | ").append(k2).append(", ").append(d2)
                    .append("\n");
        }
        Logger.getLogger(IndicatorsToCsv.class.getName()).log(Level.INFO, sbLog.toString());

        /*
         * Adding indicators values
         */
        final int nbBars = series.getBarCount();
        for (int i = 0; i < nbBars; i++) {
            sb.append(series.getBar(i).getEndTime()).append(',').append(closePrice.getValue(i)).append(',')
                    .append(typicalPrice.getValue(i)).append(',').append(priceVariation.getValue(i)).append(',')
                    .append(shortSma.getValue(i)).append(',').append(longSma.getValue(i)).append(',')
                    .append(shortEma.getValue(i)).append(',').append(longEma.getValue(i)).append(',')
                    .append(ppo.getValue(i)).append(',')
                    .append(roc.getValue(i)).append(',')
                    .append(rsi.getValue(i)).append(',')
                    .append(macd.getValue(i)).append(',')
                    .append(kFast.getValue(i)).append(',')
                    .append(dFast.getValue(i)).append(',')
                    .append(kSlow.getValue(i)).append(',')
                    .append(dSlow.getValue(i)).append(',')
                    .append(williamsR.getValue(i)).append(',')
                    .append(atr.getValue(i)).append(',')
                    .append(sd.getValue(i)).append('\n');
        }

        /*
         * Writing CSV file
         */
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File("ta4j-examples/target", "indicators_" + series.getName() + ".csv")));
            writer.write(sb.toString());
        } catch (IOException ioe) {
            Logger.getLogger(IndicatorsToCsv.class.getName()).log(Level.SEVERE, "Unable to write CSV file", ioe);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

    }
}
