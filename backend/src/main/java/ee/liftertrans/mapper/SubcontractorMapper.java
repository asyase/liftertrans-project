package ee.liftertrans.mapper;

import ee.liftertrans.dto.SubcontractorDto;
import ee.liftertrans.persistence.entity.Subcontractor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubcontractorMapper {

    @Mapping(source = "id", target = "subcontractorId")
    SubcontractorDto toSubcontractorDto(Subcontractor subcontractor);

    List<SubcontractorDto> toSubcontractorDtos(List<Subcontractor> subcontractors);
}
