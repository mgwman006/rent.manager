package tz.tante.rent.manager.enums;

public enum LeaseStatus
{
  ACTIVE( "Active"),
  ENDED( "Ended"),
  TERMINATED( "Terminated"),
  EXPIRED( "Expired"),
  PENDING( "Pending"),
  PENDING_LANDLORD_APPROVAL( "Pending Waiting For Landlord Approval"),
  PENDING_TENANT_APPROVAL( "Pending Waiting For Tenant Approval");

  private String value;

  LeaseStatus( String value )
  {
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }
}
