package com.github.longkerdandy.dividend.hkex.parser;

import com.github.longkerdandy.dividend.hkex.model.StockDividend;
import com.github.longkerdandy.dividend.hkex.storage.PostgreStorage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AAStocksParser {

  private static final Logger logger = LoggerFactory.getLogger(AAStocksParser.class);

  private final String directory; // base directory for saved pages
  private final PostgreStorage storage; // postgresql db

  public AAStocksParser(String directory, PostgreStorage storage) {
    if (!directory.endsWith("/")) {
      directory = directory + "/";
    }
    this.directory = directory;
    this.storage = storage;
  }

  public void update(List<String> symbols) throws IOException, ParseException {
    for (String symbol : symbols) {
      StockDividend dividend = parse(symbol);
      if (dividend == null) {
        logger.warn("AAStocks dividend file not exist for symbol {}", symbol);
        continue;
      }
      logger.debug("Parse AAStocks dividend data for symbol (code) {}: {}", symbol, dividend);

      this.storage.saveDividend(dividend);
      logger.info("AAStocks dividend data has been updated for symbol (code) {}", symbol);
    }
  }

  private StockDividend parse(String symbol) throws IOException, ParseException {
    // parse html from downloaded HKEX files
    File input = new File(this.directory, symbol + ".html");
    if (!input.exists()) {
      return null;
    }

    // result object
    StockDividend dividend = new StockDividend();

    // parse title
    Document doc = Jsoup.parse(input, "UTF-8", "http://www.aastocks.com");
    String title = doc.select("title").first().text();
    int i = title.lastIndexOf("(");
    dividend.setName(StringUtils.trim(title.substring(0, i)));
    dividend.setCode(title.substring(i + 1, i + 6));

    // parse table
    Element table = doc.select("#cp_repDHData_Panel1_0 + table").first();
    Element tbody = table.select("tbody + tbody").first();
    Element row = tbody.select("tr").first();
    if (row.child(0).ownText().length() >= 10) {
      dividend.setPublicDate(DateUtils.parseDate(row.child(0).ownText(), "yyyy/MM/dd"));
    }
    if (row.child(1).ownText().length() >= 4) {
      dividend.setYear(row.child(1).ownText());
    }
    dividend.setReason(row.child(2).ownText());
    dividend.setDescription(row.child(3).text());
    dividend.setMethod(row.child(4).ownText());
    if (row.child(5).ownText().length() >= 10) {
      dividend.setRightsOffDate(DateUtils.parseDate(row.child(5).ownText(), "yyyy/MM/dd"));
    }
    if (row.child(6).ownText().length() >= 20) {
      String text = row.child(6).ownText();
      dividend.setTransferBegin(
          DateUtils.parseDate(text.substring(0, text.indexOf("-")), "yyyy/MM/dd"));
      dividend
          .setTransferEnd(DateUtils.parseDate(text.substring(text.indexOf("-") + 1), "yyyy/MM/dd"));
    } else if (row.child(6).ownText().length() >= 10) {
      dividend.setTransferEnd(DateUtils.parseDate(row.child(6).ownText(), "yyyy/MM/dd"));
    }
    if (row.child(7).ownText().length() >= 10) {
      dividend.setDivideDate(DateUtils.parseDate(row.child(7).ownText(), "yyyy/MM/dd"));
    }

    return dividend;
  }
}
