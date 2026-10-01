package ee.liftertrans.service;

import ee.liftertrans.dto.SubcontractorDto;
import ee.liftertrans.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.liftertrans.mapper.SubcontractorMapper;
import ee.liftertrans.persistence.entity.Subcontractor;
import ee.liftertrans.persistence.repository.SubcontractorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubcontractorService {

    private final SubcontractorRepository subcontractorRepository;
    private final SubcontractorMapper subcontractorMapper;

    public List<SubcontractorDto> getActiveSubcontractors() {

        // Võtame andmebaasist ainult aktiivsed alltöövõtjad
        List<Subcontractor> subcontractors = subcontractorRepository.findActiveSubcontractors();

        // Muudame Entity objektid DTO objektideks
        return subcontractorMapper.toSubcontractorDtos(subcontractors);
    }

    public Subcontractor getValidSubcontractorBy(Integer subcontractorId) {

        // Otsime alltöövõtja ID järgi, kui ei leia, siis 404
        return subcontractorRepository.findById(subcontractorId)
                .orElseThrow(() -> new PrimaryKeyNotFoundException("subcontractorId", subcontractorId));
    }
}
