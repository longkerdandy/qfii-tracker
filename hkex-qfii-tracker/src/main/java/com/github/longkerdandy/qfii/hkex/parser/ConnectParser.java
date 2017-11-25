package com.github.longkerdandy.qfii.hkex.parser;

import com.github.longkerdandy.qfii.hkex.model.StockShareholding;
import com.github.longkerdandy.qfii.hkex.storage.PostgreStorage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base Connect Stock Parser Class for HPEX Data
 */
public abstract class ConnectParser {

  private static final Logger logger = LoggerFactory.getLogger(ConnectParser.class);

  protected void parseRangeAndUpdate(Date startDate, Date endDate, String directory,
      PostgreStorage storage, String market)
      throws IOException, ParseException {
    while (startDate.before(endDate) || startDate.equals(endDate)) {
      parseAndUpdate(startDate, directory, storage, market);
      startDate = DateUtils.addDays(startDate, 1);
    }
  }

  private void parseAndUpdate(Date queryDate, String directory, PostgreStorage storage,
      String market)
      throws IOException, ParseException {
    List<StockShareholding> stockShareholdings = parse(queryDate, directory);
    if (!stockShareholdings.isEmpty()) {
      if (stockShareholdings.get(0).getDate().equals(queryDate)) {
        storage.saveShareholdings(stockShareholdings, market);
        logger.info("Market {} date {} data has been parsed and updated", market,
            DateFormatUtils.format(queryDate, "yyyy-MM-dd"));
      } else {
        logger.warn("Market {} date {} data in an inconsistent state, operation skipped", market,
            DateFormatUtils.format(queryDate, "yyyy-MM-dd"));
      }
    } else {
      logger.info("Market {} date {} data not existed, operation skipped", market,
          DateFormatUtils.format(queryDate, "yyyy-MM-dd"));
    }
  }

  private List<StockShareholding> parse(Date queryDate, String directory)
      throws IOException, ParseException {
    // parse html from downloaded HKEX files
    File input = new File(directory + DateFormatUtils.format(queryDate, "yyyyMMdd") + ".html");
    if (!input.exists()) {
      return Collections.emptyList();
    }
    Document doc = Jsoup.parse(input, "UTF-8", "http://sc.hkexnews.hk");

    // parse actual query date
    Date actualDate = null;
    Elements divs = doc.select("div#pnlResult > div");
    for (Element div : divs) {
      if (div.text().startsWith("持股日期")) {
        actualDate = DateUtils
            .parseDate(div.text().substring(div.text().length() - 10), "dd/MM/yyyy");
        break;
      }
    }
    if (actualDate == null) {
      throw new IllegalStateException("Date not present in the query result");
    }

    // result
    List<StockShareholding> stockShareholdings = new ArrayList<>();

    // parse shareholding data
    Elements rows = doc.select("tr.row0,tr.row1");
    for (Element row : rows) {
      StockShareholding stockShareholding = new StockShareholding();
      stockShareholding.setDate(actualDate);
      stockShareholding.setCode(adjustCode(row.child(0).ownText()));
      stockShareholding.setName(adjustName(row.child(1).ownText()));
      stockShareholding
          .setShareholding(NumberUtils.toLong(row.child(2).ownText().replaceAll(",", "")));
      stockShareholding
          .setPercent(NumberUtils.toFloat(row.child(3).ownText().replaceAll("%", "")) / 100);
      stockShareholdings.add(stockShareholding);
    }

    return stockShareholdings;
  }

  public abstract String adjustCode(String code);

  public abstract String adjustName(String name);
}
