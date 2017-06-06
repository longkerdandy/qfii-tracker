package com.github.longkerdandy.qfii.hkex;

import com.github.longkerdandy.qfii.hkex.parser.ConnectParser;
import com.github.longkerdandy.qfii.hkex.parser.ShanghaiConnectParser;
import com.github.longkerdandy.qfii.hkex.parser.ShenzhenConnectParser;
import com.github.longkerdandy.qfii.hkex.storage.InfluxDBStorage;
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
 * Stock Tracker Application
 */
public class Tracker {

  private static final Logger logger = LoggerFactory.getLogger(Tracker.class);

  public static void main(String[] args) throws Exception {

    // create the parser
    CommandLineParser parser = new DefaultParser();
    CommandLine line = parser.parse(getOptions(), args);

    // config file
    PropertiesConfiguration config = new PropertiesConfiguration(
        line.getOptionValue("config", "config/tracker.properties"));

    // storage
    InfluxDBStorage storage = new InfluxDBStorage(config.getString("influxDB.url"),
        config.getString("influxDB.username"), config.getString("influxDB.password"));

    // update
    if (line.hasOption("clear")) {
      storage.clear();
      logger.info("Data has been cleared");
    }

    // update
    if (line.hasOption("update")) {

      // files directory
      String directory = line.getOptionValue("directory", "hkex/");

      // parser
      ShanghaiConnectParser shParser = new ShanghaiConnectParser(directory,
          config.getInt("parser.timeout"), storage);
      ShenzhenConnectParser szParser = new ShenzhenConnectParser(directory,
          config.getInt("parser.timeout"), storage);

      // parse date range and update storage
      Date startDate = DateUtils.parseDate(line.getOptionValue("start"), "yyyy/MM/dd");
      Date endDate = DateUtils.parseDate(line.getOptionValue("end"), "yyyy/MM/dd");
      shParser.parseRangeAndUpdate(startDate, endDate);
      szParser.parseRangeAndUpdate(startDate, endDate);

      logger.info("{} to {} data has been updated", line.getOptionValue("start"), line.getOptionValue("end"));
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
    options.addOption(start);
    options.addOption(end);
    options.addOption(config);

    return options;
  }
}
