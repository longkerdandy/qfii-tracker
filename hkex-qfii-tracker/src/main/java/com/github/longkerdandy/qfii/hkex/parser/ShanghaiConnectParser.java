package com.github.longkerdandy.qfii.hkex.parser;

import com.github.longkerdandy.qfii.hkex.storage.InfluxDBStorage;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * Shanghai Connect Stock Parser
 */
public class ShanghaiConnectParser extends ConnectParser {

  private final String directory;
  private final int timeout;
  private final InfluxDBStorage storage;

  public ShanghaiConnectParser(String directory, int timeout, InfluxDBStorage storage) {
    this.directory = directory + "shanghai/";
    this.timeout = timeout;
    this.storage = storage;
  }

  public void parseRangeAndUpdate(Date startDate, Date endDate) throws IOException, ParseException {
    parseRangeAndUpdate(startDate, endDate, this.directory, this.timeout, this.storage);
  }

  @Override
  public String adjustCode(String code) {
    if (code.startsWith("9")) {
      code = code.replaceFirst("9", "60");
    }
    return code;
  }

  @Override
  public String adjustName(String name) {
    if (name.equals(" 片仔？")) {
      return "片仔癀";
    }
    return name;
  }
}
