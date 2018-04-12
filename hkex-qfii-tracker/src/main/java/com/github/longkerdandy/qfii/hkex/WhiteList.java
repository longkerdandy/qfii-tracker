package com.github.longkerdandy.qfii.hkex;

import com.github.longkerdandy.qfii.hkex.storage.PostgreStorage;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.configuration.PropertiesConfiguration;

public class WhiteList {

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

    // apply white list
    storage.applyWhiteList(HKEXWhiteList.getList());
  }

  private static Options getOptions() {
    Option config = Option.builder("config").argName("config").desc("Configuration file")
        .hasArg().build();

    Options options = new Options();
    options.addOption(config);

    return options;
  }
}
