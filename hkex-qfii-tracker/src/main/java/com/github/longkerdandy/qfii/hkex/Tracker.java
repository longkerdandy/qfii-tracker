package com.github.longkerdandy.qfii.hkex;

import com.github.longkerdandy.qfii.hkex.parser.HongkongConnectParser;
import com.github.longkerdandy.qfii.hkex.parser.ShanghaiConnectParser;
import com.github.longkerdandy.qfii.hkex.parser.ShenzhenConnectParser;
import com.github.longkerdandy.qfii.hkex.storage.PostgreStorage;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HKEX News Web Page Parse and Track Application
 */
public class Tracker {

  private static final Logger logger = LoggerFactory.getLogger(Tracker.class);

  private final String directory; // base directory for saved pages
  private final PostgreStorage storage; // postgresql db

  public Tracker(String directory, PostgreStorage storage) {
    if (!directory.endsWith("/")) {
      directory = directory + "/";
    }
    this.directory = directory;
    this.storage = storage;
  }

  public static void main(String[] args) throws Exception {
    // create the parser
    CommandLineParser parser = new DefaultParser();
    CommandLine line = parser.parse(getOptions(), args);

    // config file
    PropertiesConfiguration config = new PropertiesConfiguration(
        line.getOptionValue("config", "config/tracker.properties"));

    // storage
    PostgreStorage storage = new PostgreStorage(config.getString("storage.url"),
        config.getString("storage.username"), config.getString("storage.password"));

    // target market
    String market = line.getOptionValue("market", "all");

    // clear
    if (line.hasOption("clear")) {
      storage.clear(market);
      logger.info("Data has been cleared");
    }

    // update
    if (line.hasOption("update")) {
      // files directory
      String directory = line.getOptionValue("directory", "hkex/");

      // parse date range and update storage
      Date startDate = DateUtils.parseDate(line.getOptionValue("start"), "yyyy/MM/dd");
      Date endDate = DateUtils.parseDate(line.getOptionValue("end"), "yyyy/MM/dd");

      // create tracker instance
      Tracker tracker = new Tracker(directory, storage);

      // target market
      switch (market) {
        case "all":
          tracker.parseRangeForAll(startDate, endDate);
          break;
        case "shanghai":
          tracker.parseRangeForShanghai(startDate, endDate);
          break;
        case "shenzhen":
          tracker.parseRangeForShenZhen(startDate, endDate);
          break;
        case "hongkong":
          tracker.parseRangeForHongKong(startDate, endDate);
          break;
        default:
          throw new IllegalArgumentException(
              "Unexpected value for line option 'market': " + market);
      }

      logger.info("Market {} date {} to {} data has been updated", market,
          line.getOptionValue("start"), line.getOptionValue("end"));
    }
  }

  private static Options getOptions() {
    Option clear = new Option("clear",
        "Clear saved data before proceed");
    Option update = new Option("update",
        "Update stocks shareholding data from downloaded HKEX files");
    Option directory = Option.builder("directory").argName("directory")
        .desc("Downloaded HKEX files root directory")
        .hasArg().build();
    Option market = Option.builder("market").argName("market")
        .desc("Target market could be all, shanghai, shenzhen and hongkong")
        .hasArg().build();
    Option start = Option.builder("start").argName("start")
        .desc("Start date in the format of 2017/03/17")
        .hasArg().build();
    Option end = Option.builder("end").argName("end").desc("End date in the format of 2017/03/17")
        .hasArg().build();
    Option config = Option.builder("config").argName("config").desc("Configuration file")
        .hasArg().build();

    Options options = new Options();
    options.addOption(clear);
    options.addOption(update);
    options.addOption(directory);
    options.addOption(market);
    options.addOption(start);
    options.addOption(end);
    options.addOption(config);

    return options;
  }

  /**
   * Parse pages for all market with the specific date range
   *
   * @param startDate Start Date (include)
   * @param endDate End Date (include)
   */
  public void parseRangeForAll(Date startDate, Date endDate) throws IOException, ParseException {
    parseRangeForShanghai(startDate, endDate);
    parseRangeForShenZhen(startDate, endDate);
    parseRangeForHongKong(startDate, endDate);
  }

  /**
   * Parse pages for Shanghai market with the specific date range
   *
   * @param startDate Start Date (include)
   * @param endDate End Date (include)
   */
  public void parseRangeForShanghai(Date startDate, Date endDate)
      throws IOException, ParseException {
    ShanghaiConnectParser parser = new ShanghaiConnectParser(this.directory, this.storage);
    parser.parseRangeAndUpdate(startDate, endDate);
  }

  /**
   * Parse pages for Shenzhen market with the specific date range
   *
   * @param startDate Start Date (include)
   * @param endDate End Date (include)
   */
  public void parseRangeForShenZhen(Date startDate, Date endDate)
      throws IOException, ParseException {
    ShenzhenConnectParser parser = new ShenzhenConnectParser(this.directory, this.storage);
    parser.parseRangeAndUpdate(startDate, endDate);
  }

  /**
   * Parse pages for Hongkong market with the specific date range
   *
   * @param startDate Start Date (include)
   * @param endDate End Date (include)
   */
  public void parseRangeForHongKong(Date startDate, Date endDate)
      throws IOException, ParseException {
    HongkongConnectParser parser = new HongkongConnectParser(this.directory, this.storage);
    parser.parseRangeAndUpdate(startDate, endDate);
  }
}
