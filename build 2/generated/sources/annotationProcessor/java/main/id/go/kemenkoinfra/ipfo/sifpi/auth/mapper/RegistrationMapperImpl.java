package id.go.kemenkoinfra.ipfo.sifpi.auth.mapper;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.InvestorDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.OwnerDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateInvestorRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateOwnerRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.InvestorProfile;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-07T20:22:34+0700",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.0.jar, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class RegistrationMapperImpl implements RegistrationMapper {

    @Override
    public User toEntityOwner(CreateOwnerRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setName( request.getNama() );
        user.setOrganization( request.getOrganisasi() );
        user.setJabatan( request.getJabatan() );
        user.setEmail( request.getEmail() );
        user.setPhone( request.getPhone() );

        user.setEmailVerified( false );
        user.setActive( true );

        return user;
    }

    @Override
    public User toEntityInvestor(CreateInvestorRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setName( request.getNama() );
        user.setOrganization( request.getOrganisasi() );
        user.setJabatan( request.getJabatan() );
        user.setEmail( request.getEmail() );
        user.setPhone( request.getPhone() );

        user.setEmailVerified( false );
        user.setActive( true );

        return user;
    }

    @Override
    public InvestorProfile toInvestorProfile(CreateInvestorRequest request) {
        if ( request == null ) {
            return null;
        }

        InvestorProfile investorProfile = new InvestorProfile();

        investorProfile.setBudgetRange( request.getBudgetInvestasi() );
        investorProfile.setSectorInterest( listToString( request.getSectorInterest() ) );
        investorProfile.setOptInEmail( request.getOptInEmail() );
        investorProfile.setAgreePrivacy( request.getAgreePrivacy() );
        investorProfile.setPreferredInvestmentInstrument( request.getPreferredInvestmentInstrument() );
        investorProfile.setEngagementModel( request.getEngagementModel() );
        investorProfile.setStagePreference( request.getStagePreference() );
        investorProfile.setRiskAppetite( request.getRiskAppetite() );
        investorProfile.setEsgStandards( request.getEsgStandards() );
        investorProfile.setLocalPresence( request.getLocalPresence() );
        investorProfile.setAumSize( request.getAumSize() );

        return investorProfile;
    }

    @Override
    public OwnerDTO toOwnerDTO(User user) {
        if ( user == null ) {
            return null;
        }

        OwnerDTO ownerDTO = new OwnerDTO();

        ownerDTO.setNama( user.getName() );
        ownerDTO.setOrganisasi( user.getOrganization() );
        ownerDTO.setJabatan( user.getJabatan() );
        ownerDTO.setIsVerified( user.isEmailVerified() );
        ownerDTO.setIsActive( user.isActive() );
        ownerDTO.setCreatedAt( user.getCreatedAt() );
        ownerDTO.setRoleName( userRoleName( user ) );
        ownerDTO.setId( user.getId() );
        ownerDTO.setEmail( user.getEmail() );
        ownerDTO.setPhone( user.getPhone() );

        return ownerDTO;
    }

    @Override
    public InvestorDTO toInvestorDTO(User user) {
        if ( user == null ) {
            return null;
        }

        InvestorDTO investorDTO = new InvestorDTO();

        investorDTO.setNama( user.getName() );
        investorDTO.setOrganisasi( user.getOrganization() );
        investorDTO.setJabatan( user.getJabatan() );
        investorDTO.setIsVerified( user.isEmailVerified() );
        investorDTO.setIsActive( user.isActive() );
        investorDTO.setRoleName( userRoleName( user ) );
        investorDTO.setBudgetInvestasi( userInvestorProfileBudgetRange( user ) );
        String sectorInterest = userInvestorProfileSectorInterest( user );
        investorDTO.setSectorInterest( stringToList( sectorInterest ) );
        investorDTO.setOptInEmail( userInvestorProfileOptInEmail( user ) );
        investorDTO.setAgreePrivacy( userInvestorProfileAgreePrivacy( user ) );
        investorDTO.setPreferredInvestmentInstrument( userInvestorProfilePreferredInvestmentInstrument( user ) );
        investorDTO.setEngagementModel( userInvestorProfileEngagementModel( user ) );
        investorDTO.setStagePreference( userInvestorProfileStagePreference( user ) );
        investorDTO.setRiskAppetite( userInvestorProfileRiskAppetite( user ) );
        investorDTO.setEsgStandards( userInvestorProfileEsgStandards( user ) );
        investorDTO.setLocalPresence( userInvestorProfileLocalPresence( user ) );
        investorDTO.setAumSize( userInvestorProfileAumSize( user ) );
        investorDTO.setCreatedAt( user.getCreatedAt() );
        investorDTO.setId( user.getId() );
        investorDTO.setEmail( user.getEmail() );
        investorDTO.setPhone( user.getPhone() );

        return investorDTO;
    }

    private String userRoleName(User user) {
        Role role = user.getRole();
        if ( role == null ) {
            return null;
        }
        return role.getName();
    }

    private String userInvestorProfileBudgetRange(User user) {
        InvestorProfile investorProfile = user.getInvestorProfile();
        if ( investorProfile == null ) {
            return null;
        }
        return investorProfile.getBudgetRange();
    }

    private String userInvestorProfileSectorInterest(User user) {
        InvestorProfile investorProfile = user.getInvestorProfile();
        if ( investorProfile == null ) {
            return null;
        }
        return investorProfile.getSectorInterest();
    }

    private Boolean userInvestorProfileOptInEmail(User user) {
        InvestorProfile investorProfile = user.getInvestorProfile();
        if ( investorProfile == null ) {
            return null;
        }
        return investorProfile.getOptInEmail();
    }

    private Boolean userInvestorProfileAgreePrivacy(User user) {
        InvestorProfile investorProfile = user.getInvestorProfile();
        if ( investorProfile == null ) {
            return null;
        }
        return investorProfile.getAgreePrivacy();
    }

    private String userInvestorProfilePreferredInvestmentInstrument(User user) {
        InvestorProfile investorProfile = user.getInvestorProfile();
        if ( investorProfile == null ) {
            return null;
        }
        return investorProfile.getPreferredInvestmentInstrument();
    }

    private String userInvestorProfileEngagementModel(User user) {
        InvestorProfile investorProfile = user.getInvestorProfile();
        if ( investorProfile == null ) {
            return null;
        }
        return investorProfile.getEngagementModel();
    }

    private String userInvestorProfileStagePreference(User user) {
        InvestorProfile investorProfile = user.getInvestorProfile();
        if ( investorProfile == null ) {
            return null;
        }
        return investorProfile.getStagePreference();
    }

    private String userInvestorProfileRiskAppetite(User user) {
        InvestorProfile investorProfile = user.getInvestorProfile();
        if ( investorProfile == null ) {
            return null;
        }
        return investorProfile.getRiskAppetite();
    }

    private String userInvestorProfileEsgStandards(User user) {
        InvestorProfile investorProfile = user.getInvestorProfile();
        if ( investorProfile == null ) {
            return null;
        }
        return investorProfile.getEsgStandards();
    }

    private String userInvestorProfileLocalPresence(User user) {
        InvestorProfile investorProfile = user.getInvestorProfile();
        if ( investorProfile == null ) {
            return null;
        }
        return investorProfile.getLocalPresence();
    }

    private String userInvestorProfileAumSize(User user) {
        InvestorProfile investorProfile = user.getInvestorProfile();
        if ( investorProfile == null ) {
            return null;
        }
        return investorProfile.getAumSize();
    }
}
