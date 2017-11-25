package com.github.longkerdandy.qfii.hkex.parser;

import com.github.longkerdandy.qfii.hkex.storage.PostgreStorage;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

/**
 * Shenzhen Connect Stock Parser
 */
public class ShenzhenConnectParser extends ConnectParser {

  private final String directory;
  private final PostgreStorage storage;

  public ShenzhenConnectParser(String directory, PostgreStorage storage) {
    this.directory = directory + "shenzhen/";
    this.storage = storage;
  }

  public void parseRangeAndUpdate(Date startDate, Date endDate) throws IOException, ParseException {
    parseRangeAndUpdate(startDate, endDate, this.directory, this.storage, "shenzhen");
  }

  @Override
  public String adjustCode(String code) {
    if (code.startsWith("77")) {
      code = code.replaceFirst("77", "300");
    } else if (code.startsWith("7")) {
      code = code.replaceFirst("7", "00");
    }
    return code;
  }

  @Override
  public String adjustName(String name) {
    return StringUtils.trim(name);
  }
}
