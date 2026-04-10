package com.champsoft.finalprojectwebservices.fee.application;

import com.champsoft.finalprojectwebservices.fee.domain.Fee;
import com.champsoft.finalprojectwebservices.fee.domain.FeeRepository;
import com.champsoft.finalprojectwebservices.shared.domain.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FeeService {

    private final FeeRepository feeRepository;

    public FeeService(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
    }

    @Transactional(readOnly = true)
    public List<Fee> getAll() {
        return feeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Fee getById(UUID id) {
        return feeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Fee> getByMemberId(UUID memberId) {
        return feeRepository.findByMemberId(memberId);
    }

    public Fee create(Fee fee) {
        return feeRepository.save(fee);
    }

    public Fee update(UUID id, Fee updatedFee) {
        Fee existing = getById(id);
        existing.update(updatedFee.getMemberId(), updatedFee.getAmount(), updatedFee.getStatus());
        return feeRepository.save(existing);
    }

    public void delete(UUID id) {
        if (!feeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fee not found: " + id);
        }
        feeRepository.deleteById(id);
    }
}
