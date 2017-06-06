package com.github.longkerdandy.qfii.hkex.parser;

import com.github.longkerdandy.qfii.hkex.storage.InfluxDBStorage;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * Shenzhen Connect Stock Parser
 */
public class ShenzhenConnectParser extends ConnectParser {

  private final String directory;
  private final int timeout;
  private final InfluxDBStorage storage;

  public ShenzhenConnectParser(String directory, int timeout, InfluxDBStorage storage) {
    this.directory = directory + "shenzhen/";
    this.timeout = timeout;
    this.storage = storage;
  }

  public void parseRangeAndUpdate(Date startDate, Date endDate) throws IOException, ParseException {
    parseRangeAndUpdate(startDate, endDate, this.directory, this.timeout, this.storage);
  }

  @Override
  public String adjustCode(String code) {
    if (code.startsWith("7")) {
      code = code.replaceFirst("7", "00");
    }
    return code;
  }

  @Override
  public String adjustName(String name) {
    return name;
  }
}
