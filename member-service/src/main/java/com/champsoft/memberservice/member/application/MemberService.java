package com.champsoft.memberservice.member.application;

import com.champsoft.memberservice.member.dataaccess.MemberEntity;
import com.champsoft.memberservice.member.dataaccess.MemberRepository;
import com.champsoft.memberservice.member.domain.DuplicateMemberEmailException;
import com.champsoft.memberservice.member.domain.InvalidMemberStateException;
import com.champsoft.memberservice.member.domain.MemberResponse;
import com.champsoft.memberservice.member.domain.MemberStatus;
import com.champsoft.memberservice.member.domain.UpsertMemberRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberById(UUID memberId) {
        return toResponse(getEntity(memberId));
    }

    public MemberResponse createMember(UpsertMemberRequest request) {
        validateRequest(request);

        if (memberRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateMemberEmailException(request.email());
        }

        MemberEntity entity = new MemberEntity();
        entity.setId(UUID.randomUUID());
        apply(entity, request);

        return toResponse(memberRepository.save(entity));
    }

    public MemberResponse updateMember(UUID memberId, UpsertMemberRequest request) {
        validateRequest(request);

        MemberEntity entity = getEntity(memberId);
        memberRepository.findByEmailIgnoreCase(request.email())
                .filter(existing -> !existing.getId().equals(memberId))
                .ifPresent(existing -> {
                    throw new DuplicateMemberEmailException(request.email());
                });

        apply(entity, request);
        return toResponse(memberRepository.save(entity));
    }

    public void deleteMember(UUID memberId) {
        memberRepository.delete(getEntity(memberId));
    }

    private MemberEntity getEntity(UUID memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundApplicationException(memberId));
    }

    private void validateRequest(UpsertMemberRequest request) {
        if (request.status() == MemberStatus.SUSPENDED && request.outstandingFees().signum() == 0) {
            throw new InvalidMemberStateException("Suspended members must have outstanding fees.");
        }

        if (request.status() == MemberStatus.ACTIVE && request.outstandingFees().signum() > 0) {
            throw new MemberRequestValidationException("Active members cannot have outstanding fees.");
        }
    }

    private void apply(MemberEntity entity, UpsertMemberRequest request) {
        entity.setName(request.name().trim());
        entity.setEmail(request.email().trim().toLowerCase());
        entity.setStatus(request.status());
        entity.setOutstandingFees(request.outstandingFees());
    }

    private MemberResponse toResponse(MemberEntity entity) {
        return new MemberResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getOutstandingFees()
        );
    }
}
