package com.champsoft.finalprojectwebservices.member.application;

import com.champsoft.finalprojectwebservices.member.domain.Member;
import com.champsoft.finalprojectwebservices.member.domain.MemberRepository;
import com.champsoft.finalprojectwebservices.shared.domain.DuplicateResourceException;
import com.champsoft.finalprojectwebservices.shared.domain.ResourceNotFoundException;
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
    public List<Member> getAll() {
        return memberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Member getById(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
    }

    public Member create(Member member) {
        memberRepository.findByEmail(member.getEmail()).ifPresent(existing -> {
            throw new DuplicateResourceException("Member email already exists: " + existing.getEmail());
        });
        return memberRepository.save(member);
    }

    public Member update(UUID id, Member updatedMember) {
        Member existing = getById(id);
        memberRepository.findByEmail(updatedMember.getEmail())
                .filter(member -> !member.getId().equals(id))
                .ifPresent(member -> {
                    throw new DuplicateResourceException("Member email already exists: " + member.getEmail());
                });
        existing.update(
                updatedMember.getName(),
                updatedMember.getEmail(),
                updatedMember.getStatus(),
                updatedMember.getOutstandingFees()
        );
        return memberRepository.save(existing);
    }

    public void delete(UUID id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member not found: " + id);
        }
        memberRepository.deleteById(id);
    }
}
