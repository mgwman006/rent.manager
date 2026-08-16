package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.models.dtos.responses.MembershipDetailsDTO;
import tz.tante.rent.manager.models.entities.Membership;
import tz.tante.rent.manager.repositories.MembershipRepository;

import java.util.List;

@Service
@Getter
@Setter
@AllArgsConstructor
public class MembershipService
{
  private final MembershipRepository membershipRepository;


  public List<MembershipDetailsDTO> getMembershipsByUserId(Long userId)
  {
    try
    {
      return membershipRepository.findByUserId(userId)
        .stream()
        .map(this::mapToMembershipDetailsDTO)
        .toList();
    }
    catch (Exception exception)
    {
      throw new TanteException(exception.getMessage());
    }
  }

  private MembershipDetailsDTO mapToMembershipDetailsDTO(Membership membership)
  {
    return new MembershipDetailsDTO(
      membership.getId(),
      membership.getUserId(),
      membership.getRentalProfile() != null ? membership.getRentalProfile().getId() : null,
      membership.getRentalProfile() != null ? membership.getRentalProfile().getName() : null
    );
  }

  public List<MembershipDetailsDTO> getMembershipsByPhoneNumber(String phoneNumber)
  {
    try
    {
      return membershipRepository.findByPhoneNumber(phoneNumber)
        .stream()
        .map(this::mapToMembershipDetailsDTO)
        .toList();
    }
    catch (Exception exception)
    {
      throw new TanteException(exception.getMessage());
    }
  }
}
