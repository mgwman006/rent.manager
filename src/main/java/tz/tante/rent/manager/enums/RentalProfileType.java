package tz.tante.rent.manager.enums;

public enum RentalProfileType
{
  INVALID("Invalid Rental Profile Type"),
  INDIVIDUAL("Individual Landlord/Agent with a single user and without a registered business"),
  BUSINESS("Registered Agency Business with maximum of 5 general users  and a registered business"),
  ENTERPRISE("Registered Agency at Enterprise level business with more than 5 role based users and a registered business");


  private String description;

  RentalProfileType(String description)
  {
    this.description = description;
  }

  public String getDescription()
  {
    return description;
  }

  public static RentalProfileType fromString(String givenType)
  {
    RentalProfileType type = RentalProfileType.INVALID;
    for (RentalProfileType value : RentalProfileType.values())
    {
      if (value.name().equalsIgnoreCase(givenType))
      {
        type = value;
        break;
      }
    }
    return type;
  }
}
