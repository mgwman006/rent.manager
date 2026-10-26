package tz.tante.rent.manager.enums;

public enum RentFrequency
{
  DAILY("Per Day"),
  WEEKLY("Per Week"),
  MONTHLY("Per Month"),
  YEARLY("Per Year");

  private String value;

  RentFrequency(String value)
  {
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }
}