package id.go.kemenkoinfra.ipfo.sifpi.auth.mapper;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.InvestorDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.OwnerDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateInvestorRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateOwnerRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.InvestorProfile;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

    // OWNER REGISTRATION - Request to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "investorProfile", ignore = true)
    @Mapping(target = "verifiedBy", ignore = true)
    @Mapping(target = "verifiedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "nama", target = "name")
    @Mapping(source = "organisasi", target = "organization")
    @Mapping(source = "jabatan", target = "jabatan")
    User toEntityOwner(CreateOwnerRequest request);

    // INVESTOR REGISTRATION - Request to Entity (User part only)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "investorProfile", ignore = true)
    @Mapping(target = "verifiedBy", ignore = true)
    @Mapping(target = "verifiedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "nama", target = "name")
    @Mapping(source = "organisasi", target = "organization")
    @Mapping(source = "jabatan", target = "jabatan")
    User toEntityInvestor(CreateInvestorRequest request);

    // INVESTOR PROFILE - Request to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "budgetInvestasi", target = "budgetRange")
    @Mapping(source = "sectorInterest", target = "sectorInterest", qualifiedByName = "listToString")
    @Mapping(source = "optInEmail", target = "optInEmail")
    @Mapping(source = "agreePrivacy", target = "agreePrivacy")
    @Mapping(source = "preferredInvestmentInstrument", target = "preferredInvestmentInstrument")
    @Mapping(source = "engagementModel", target = "engagementModel")
    @Mapping(source = "stagePreference", target = "stagePreference")
    @Mapping(source = "riskAppetite", target = "riskAppetite")
    @Mapping(source = "esgStandards", target = "esgStandards")
    @Mapping(source = "localPresence", target = "localPresence")
    @Mapping(source = "aumSize", target = "aumSize")
    InvestorProfile toInvestorProfile(CreateInvestorRequest request);

    // USER ENTITY to OWNER DTO Response
    @Mapping(source = "name", target = "nama")
    @Mapping(source = "organization", target = "organisasi")
    @Mapping(source = "jabatan", target = "jabatan")
    @Mapping(source = "verified", target = "isVerified")
    @Mapping(source = "active", target = "isActive")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "role.name", target = "roleName")
    OwnerDTO toOwnerDTO(User user);

    // USER ENTITY to INVESTOR DTO Response (using InvestorProfile)
    @Mapping(source = "name", target = "nama")
    @Mapping(source = "organization", target = "organisasi")
    @Mapping(source = "jabatan", target = "jabatan")
    @Mapping(source = "verified", target = "isVerified")
    @Mapping(source = "active", target = "isActive")
    @Mapping(source = "role.name", target = "roleName")
    @Mapping(source = "investorProfile.budgetRange", target = "budgetInvestasi")
    @Mapping(source = "investorProfile.sectorInterest", target = "sectorInterest", qualifiedByName = "stringToList")
    @Mapping(source = "investorProfile.optInEmail", target = "optInEmail")
    @Mapping(source = "investorProfile.agreePrivacy", target = "agreePrivacy")
    @Mapping(source = "investorProfile.preferredInvestmentInstrument", target = "preferredInvestmentInstrument")
    @Mapping(source = "investorProfile.engagementModel", target = "engagementModel")
    @Mapping(source = "investorProfile.stagePreference", target = "stagePreference")
    @Mapping(source = "investorProfile.riskAppetite", target = "riskAppetite")
    @Mapping(source = "investorProfile.esgStandards", target = "esgStandards")
    @Mapping(source = "investorProfile.localPresence", target = "localPresence")
    @Mapping(source = "investorProfile.aumSize", target = "aumSize")
    @Mapping(source = "createdAt", target = "createdAt")
    InvestorDTO toInvestorDTO(User user);

    @Named("listToString")
    default String listToString(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        return String.join(",", list);
    }

    @Named("stringToList")
    default List<String> stringToList(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        return Arrays.asList(str.split(","));
    }
}