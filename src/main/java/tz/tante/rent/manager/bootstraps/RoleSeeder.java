//package tz.tante.rent.manager.bootstraps;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import tz.tante.rent.manager.enums.RentalProfileRoleName;
//
//@Component
//@RequiredArgsConstructor
//public class RoleSeeder implements CommandLineRunner
//{
//  private final BusinessMembershipRoleRepository businessMembershipRoleRepository;
//  private final AuthorityRoleRepository authorityRoleRepository;
//
//  @Override
//  public void run(String... args)
//  {
//    for (BusinessMembershipRoleName businessMembershipRoleName : BusinessMembershipRoleName.values())
//    {
//      if (!businessMembershipRoleRepository.existsByName(businessMembershipRoleName))
//      {
//        BusinessMembershipRole businessMembershipRole = new BusinessMembershipRole();
//        businessMembershipRole.setName(businessMembershipRoleName);
//        businessMembershipRoleRepository.save(businessMembershipRole);
//      }
//    }
//
//    for (RentalProfileRoleName rentalProfileRoleName : RentalProfileRoleName.values())
//    {
//      if (!authorityRoleRepository.existsByName(rentalProfileRoleName))
//      {
//        AuthorityRole authorityRole = new AuthorityRole();
//        authorityRole.setName(rentalProfileRoleName);
//        authorityRoleRepository.save(authorityRole);
//      }
//    }
//  }
//
//
//
//}