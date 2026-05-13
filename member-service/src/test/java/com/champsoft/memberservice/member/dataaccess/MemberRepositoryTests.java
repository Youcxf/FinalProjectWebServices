package com.champsoft.memberservice.member.dataaccess;

import com.champsoft.memberservice.member.domain.MemberStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("testing")
class MemberRepositoryTests {

    @Autowired
    private MemberRepository repository;

    @Test
    void savesFindsChecksAndDeletesMember() {
        MemberEntity member = member("repo-member@test.com");
        repository.save(member);

        assertThat(repository.findById(member.getId())).isPresent();
        assertThat(repository.findByEmailIgnoreCase("REPO-MEMBER@test.com")).isPresent();
        assertThat(repository.existsByEmailIgnoreCase("repo-member@test.com")).isTrue();

        repository.delete(member);

        assertThat(repository.findById(member.getId())).isEmpty();
        assertThat(repository.existsByEmailIgnoreCase("repo-member@test.com")).isFalse();
    }

    private MemberEntity member(String email) {
        MemberEntity member = new MemberEntity();
        member.setId(UUID.randomUUID());
        member.setName("Repo Member");
        member.setEmail(email);
        member.setStatus(MemberStatus.ACTIVE);
        member.setOutstandingFees(BigDecimal.ZERO);
        return member;
    }
}
