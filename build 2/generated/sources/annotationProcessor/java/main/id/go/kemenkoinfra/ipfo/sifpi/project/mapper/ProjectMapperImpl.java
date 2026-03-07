package id.go.kemenkoinfra.ipfo.sifpi.project.mapper;

import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectListItemDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectTimelineResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.CreateProjectRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.EditProjectRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.ProjectTimelineRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.ProjectTimeline;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-07T20:22:33+0700",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.0.jar, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class ProjectMapperImpl implements ProjectMapper {

    @Override
    public Project toEntity(CreateProjectRequest request) {
        if ( request == null ) {
            return null;
        }

        Project.ProjectBuilder project = Project.builder();

        project.name( request.getName() );
        project.description( request.getDescription() );
        project.location( request.getLocation() );
        project.valueProposition( request.getValueProposition() );
        project.ownerInstitution( request.getOwnerInstitution() );
        project.contactPersonName( request.getContactPersonName() );
        project.contactPersonEmail( request.getContactPersonEmail() );
        project.contactPersonPhone( request.getContactPersonPhone() );
        project.cooperationModel( request.getCooperationModel() );
        project.concessionPeriod( request.getConcessionPeriod() );
        project.assetReadiness( request.getAssetReadiness() );
        project.governmentSupport( request.getGovernmentSupport() );
        project.totalCapex( request.getTotalCapex() );
        project.totalOpex( request.getTotalOpex() );
        project.npv( request.getNpv() );
        project.irr( request.getIrr() );
        project.revenueStream( request.getRevenueStream() );
        project.isFeasibilityStudy( request.getIsFeasibilityStudy() );
        project.additionalInfo( request.getAdditionalInfo() );
        project.timelines( projectTimelineRequestListToProjectTimelineList( request.getTimelines() ) );
        project.isSubmitted( request.getIsSubmitted() );

        project.sector( toSector(request.getSector()) );
        project.status( toStatus(request.getStatus()) );

        Project projectResult = project.build();

        syncProjectToTimeline( projectResult );

        return projectResult;
    }

    @Override
    public void updateEntity(EditProjectRequest request, Project project) {
        if ( request == null ) {
            return;
        }

        if ( request.getName() != null ) {
            project.setName( request.getName() );
        }
        if ( request.getDescription() != null ) {
            project.setDescription( request.getDescription() );
        }
        if ( request.getLocation() != null ) {
            project.setLocation( request.getLocation() );
        }
        if ( request.getValueProposition() != null ) {
            project.setValueProposition( request.getValueProposition() );
        }
        if ( request.getLocationImageKey() != null ) {
            project.setLocationImageKey( request.getLocationImageKey() );
        }
        if ( request.getOwnerInstitution() != null ) {
            project.setOwnerInstitution( request.getOwnerInstitution() );
        }
        if ( request.getContactPersonName() != null ) {
            project.setContactPersonName( request.getContactPersonName() );
        }
        if ( request.getContactPersonEmail() != null ) {
            project.setContactPersonEmail( request.getContactPersonEmail() );
        }
        if ( request.getContactPersonPhone() != null ) {
            project.setContactPersonPhone( request.getContactPersonPhone() );
        }
        if ( request.getCooperationModel() != null ) {
            project.setCooperationModel( request.getCooperationModel() );
        }
        if ( request.getConcessionPeriod() != null ) {
            project.setConcessionPeriod( request.getConcessionPeriod() );
        }
        if ( request.getAssetReadiness() != null ) {
            project.setAssetReadiness( request.getAssetReadiness() );
        }
        if ( request.getProjectStructureImageKey() != null ) {
            project.setProjectStructureImageKey( request.getProjectStructureImageKey() );
        }
        if ( request.getGovernmentSupport() != null ) {
            project.setGovernmentSupport( request.getGovernmentSupport() );
        }
        if ( request.getTotalCapex() != null ) {
            project.setTotalCapex( request.getTotalCapex() );
        }
        if ( request.getTotalOpex() != null ) {
            project.setTotalOpex( request.getTotalOpex() );
        }
        if ( request.getNpv() != null ) {
            project.setNpv( request.getNpv() );
        }
        if ( request.getIrr() != null ) {
            project.setIrr( request.getIrr() );
        }
        if ( request.getRevenueStream() != null ) {
            project.setRevenueStream( request.getRevenueStream() );
        }
        if ( request.getProjectFileKey() != null ) {
            project.setProjectFileKey( request.getProjectFileKey() );
        }
        if ( request.getIsFeasibilityStudy() != null ) {
            project.setIsFeasibilityStudy( request.getIsFeasibilityStudy() );
        }
        if ( request.getAdditionalInfo() != null ) {
            project.setAdditionalInfo( request.getAdditionalInfo() );
        }
        if ( project.getTimelines() != null ) {
            List<ProjectTimeline> list = projectTimelineRequestListToProjectTimelineList( request.getTimelines() );
            if ( list != null ) {
                project.getTimelines().clear();
                project.getTimelines().addAll( list );
            }
        }
        else {
            List<ProjectTimeline> list = projectTimelineRequestListToProjectTimelineList( request.getTimelines() );
            if ( list != null ) {
                project.setTimelines( list );
            }
        }
        if ( request.getIsSubmitted() != null ) {
            project.setIsSubmitted( request.getIsSubmitted() );
        }

        project.setSector( request.getSector() == null ? project.getSector() : toSector(request.getSector()) );
        project.setStatus( request.getStatus() == null ? project.getStatus() : toStatus(request.getStatus()) );

        syncProjectToTimeline( project );
    }

    @Override
    public ProjectResponseDTO toDTO(Project project) {
        if ( project == null ) {
            return null;
        }

        ProjectResponseDTO.ProjectResponseDTOBuilder projectResponseDTO = ProjectResponseDTO.builder();

        projectResponseDTO.id( project.getId() );
        projectResponseDTO.ownerId( project.getOwnerId() );
        projectResponseDTO.name( project.getName() );
        projectResponseDTO.description( project.getDescription() );
        projectResponseDTO.location( project.getLocation() );
        projectResponseDTO.valueProposition( project.getValueProposition() );
        projectResponseDTO.ownerInstitution( project.getOwnerInstitution() );
        projectResponseDTO.contactPersonName( project.getContactPersonName() );
        projectResponseDTO.contactPersonEmail( project.getContactPersonEmail() );
        projectResponseDTO.contactPersonPhone( project.getContactPersonPhone() );
        projectResponseDTO.cooperationModel( project.getCooperationModel() );
        projectResponseDTO.concessionPeriod( project.getConcessionPeriod() );
        projectResponseDTO.assetReadiness( project.getAssetReadiness() );
        projectResponseDTO.governmentSupport( project.getGovernmentSupport() );
        projectResponseDTO.totalCapex( project.getTotalCapex() );
        projectResponseDTO.totalOpex( project.getTotalOpex() );
        projectResponseDTO.npv( project.getNpv() );
        projectResponseDTO.irr( project.getIrr() );
        projectResponseDTO.revenueStream( project.getRevenueStream() );
        projectResponseDTO.isFeasibilityStudy( project.getIsFeasibilityStudy() );
        projectResponseDTO.additionalInfo( project.getAdditionalInfo() );
        projectResponseDTO.timelines( projectTimelineListToProjectTimelineResponseDTOList( project.getTimelines() ) );
        projectResponseDTO.isSubmitted( project.getIsSubmitted() );

        projectResponseDTO.sector( fromSector(project.getSector()) );
        projectResponseDTO.status( fromStatus(project.getStatus()) );

        return projectResponseDTO.build();
    }

    @Override
    public ProjectListItemDTO toListItemDTO(Project project) {
        if ( project == null ) {
            return null;
        }

        ProjectListItemDTO.ProjectListItemDTOBuilder projectListItemDTO = ProjectListItemDTO.builder();

        projectListItemDTO.id( project.getId() );
        projectListItemDTO.name( project.getName() );
        projectListItemDTO.sector( project.getSector() );
        projectListItemDTO.status( project.getStatus() );
        projectListItemDTO.location( project.getLocation() );
        projectListItemDTO.description( project.getDescription() );
        projectListItemDTO.isSubmitted( project.getIsSubmitted() );
        projectListItemDTO.createdAt( project.getCreatedAt() );
        projectListItemDTO.editedAt( project.getEditedAt() );

        return projectListItemDTO.build();
    }

    @Override
    public ProjectTimeline toTimelineEntity(ProjectTimelineRequest request) {
        if ( request == null ) {
            return null;
        }

        ProjectTimeline.ProjectTimelineBuilder projectTimeline = ProjectTimeline.builder();

        projectTimeline.timeRange( request.getTimeRange() );
        projectTimeline.phaseDescription( request.getPhaseDescription() );

        return projectTimeline.build();
    }

    protected List<ProjectTimeline> projectTimelineRequestListToProjectTimelineList(List<ProjectTimelineRequest> list) {
        if ( list == null ) {
            return null;
        }

        List<ProjectTimeline> list1 = new ArrayList<ProjectTimeline>( list.size() );
        for ( ProjectTimelineRequest projectTimelineRequest : list ) {
            list1.add( toTimelineEntity( projectTimelineRequest ) );
        }

        return list1;
    }

    protected ProjectTimelineResponseDTO projectTimelineToProjectTimelineResponseDTO(ProjectTimeline projectTimeline) {
        if ( projectTimeline == null ) {
            return null;
        }

        ProjectTimelineResponseDTO.ProjectTimelineResponseDTOBuilder projectTimelineResponseDTO = ProjectTimelineResponseDTO.builder();

        projectTimelineResponseDTO.id( projectTimeline.getId() );
        projectTimelineResponseDTO.timeRange( projectTimeline.getTimeRange() );
        projectTimelineResponseDTO.phaseDescription( projectTimeline.getPhaseDescription() );

        return projectTimelineResponseDTO.build();
    }

    protected List<ProjectTimelineResponseDTO> projectTimelineListToProjectTimelineResponseDTOList(List<ProjectTimeline> list) {
        if ( list == null ) {
            return null;
        }

        List<ProjectTimelineResponseDTO> list1 = new ArrayList<ProjectTimelineResponseDTO>( list.size() );
        for ( ProjectTimeline projectTimeline : list ) {
            list1.add( projectTimelineToProjectTimelineResponseDTO( projectTimeline ) );
        }

        return list1;
    }
}
