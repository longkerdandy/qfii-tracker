package com.github.longkerdandy.dividend.hkex;

import com.github.longkerdandy.dividend.hkex.parser.AAStocksParser;
import com.github.longkerdandy.dividend.hkex.spider.AAStocksSpider;
import com.github.longkerdandy.dividend.hkex.storage.PostgreStorage;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tracker {

  private static final Logger logger = LoggerFactory.getLogger(Tracker.class);

  public static void main(String[] args) throws Exception {
    // command line options
    CommandLine line = new DefaultParser().parse(getOptions(), args);

    // files directory
    String directory = line.getOptionValue("directory", "hkex/");

    // target symbols
    List<String> symbols;
    if (line.hasOption("symbols")) {
      symbols = Arrays.asList(line.getOptionValues("symbols"));
    } else {
      symbols = HKEXWhiteList.getList();
    }

    // config file
    PropertiesConfiguration config = new PropertiesConfiguration(
        line.getOptionValue("config", "config/tracker.properties"));

    // storage
    PostgreStorage storage = new PostgreStorage(config.getString("storage.url"),
        config.getString("storage.username"), config.getString("storage.password"));

    // spider
    AAStocksSpider spider = new AAStocksSpider(directory);
    spider.download(symbols);

    // parser
    AAStocksParser parser = new AAStocksParser(directory, storage);
    parser.update(symbols);
  }

  private static Options getOptions() {
    Option directory = Option.builder("directory").argName("directory")
        .desc("Downloaded AAStocks files directory")
        .hasArg().build();
    Option symbols = Option.builder("symbols").argName("symbols")
        .desc("Target stock symbol(code) for Hong Kong market")
        .hasArgs().build();
    Option config = Option.builder("config").argName("config").desc("Configuration file")
        .hasArg().build();

    Options options = new Options();
    options.addOption(directory);
    options.addOption(symbols);
    options.addOption(config);

    return options;
  }
}
