package com.champsoft.memberservice.member.application;

import com.champsoft.memberservice.member.dataaccess.MemberEntity;
import com.champsoft.memberservice.member.dataaccess.MemberRepository;
import com.champsoft.memberservice.member.domain.DuplicateMemberEmailException;
import com.champsoft.memberservice.member.domain.InvalidMemberStateException;
import com.champsoft.memberservice.member.domain.MemberResponse;
import com.champsoft.memberservice.member.domain.MemberStatus;
import com.champsoft.memberservice.member.domain.UpsertMemberRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTests {

    @Mock
    private MemberRepository repository;

    @InjectMocks
    private MemberService service;

    @Test
    void createsMember() {
        when(repository.existsByEmailIgnoreCase("amy@test.com")).thenReturn(false);
        when(repository.save(org.mockito.ArgumentMatchers.any(MemberEntity.class))).thenAnswer(call -> call.getArgument(0));

        MemberResponse response = service.createMember(request("Amy", "amy@test.com", MemberStatus.ACTIVE, BigDecimal.ZERO));

        assertThat(response.email()).isEqualTo("amy@test.com");
    }

    @Test
    void createRejectsDuplicateEmail() {
        when(repository.existsByEmailIgnoreCase("amy@test.com")).thenReturn(true);

        assertThrows(DuplicateMemberEmailException.class,
                () -> service.createMember(request("Amy", "amy@test.com", MemberStatus.ACTIVE, BigDecimal.ZERO)));
    }

    @Test
    void createRejectsSuspendedMemberWithoutFees() {
        assertThrows(InvalidMemberStateException.class,
                () -> service.createMember(request("Amy", "amy@test.com", MemberStatus.SUSPENDED, BigDecimal.ZERO)));
    }

    @Test
    void createRejectsActiveMemberWithFees() {
        assertThrows(MemberRequestValidationException.class,
                () -> service.createMember(request("Amy", "amy@test.com", MemberStatus.ACTIVE, BigDecimal.ONE)));
    }

    @Test
    void getsListsUpdatesAndDeletesMember() {
        UUID id = UUID.randomUUID();
        MemberEntity entity = entity(id, "amy@test.com");
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.findAll()).thenReturn(List.of(entity));
        when(repository.findByEmailIgnoreCase("new@test.com")).thenReturn(Optional.empty());
        when(repository.save(entity)).thenReturn(entity);

        assertThat(service.getMemberById(id).id()).isEqualTo(id);
        assertThat(service.getAllMembers()).hasSize(1);
        assertThat(service.updateMember(id, request("New", "new@test.com", MemberStatus.ACTIVE, BigDecimal.ZERO)).email())
                .isEqualTo("new@test.com");

        service.deleteMember(id);

        verify(repository).delete(entity);
    }

    @Test
    void updateAllowsExistingEmailOnSameMember() {
        UUID id = UUID.randomUUID();
        MemberEntity entity = entity(id, "amy@test.com");
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.findByEmailIgnoreCase("amy@test.com")).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        MemberResponse response = service.updateMember(id, request("Amy", "amy@test.com", MemberStatus.ACTIVE, BigDecimal.ZERO));

        assertThat(response.email()).isEqualTo("amy@test.com");
    }

    @Test
    void updateRejectsExistingEmailOnDifferentMember() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(entity(id, "old@test.com")));
        when(repository.findByEmailIgnoreCase("amy@test.com"))
                .thenReturn(Optional.of(entity(UUID.randomUUID(), "amy@test.com")));

        assertThrows(DuplicateMemberEmailException.class,
                () -> service.updateMember(id, request("Amy", "amy@test.com", MemberStatus.ACTIVE, BigDecimal.ZERO)));
    }

    @Test
    void missingMemberThrowsException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundApplicationException.class, () -> service.getMemberById(id));
    }

    private UpsertMemberRequest request(String name, String email, MemberStatus status, BigDecimal fees) {
        return new UpsertMemberRequest(name, email, status, fees);
    }

    private MemberEntity entity(UUID id, String email) {
        MemberEntity entity = new MemberEntity();
        entity.setId(id);
        entity.setName("Amy");
        entity.setEmail(email);
        entity.setStatus(MemberStatus.ACTIVE);
        entity.setOutstandingFees(BigDecimal.ZERO);
        return entity;
    }
}
