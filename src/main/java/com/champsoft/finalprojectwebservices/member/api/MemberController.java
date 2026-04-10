package com.champsoft.finalprojectwebservices.member.api;

import com.champsoft.finalprojectwebservices.member.application.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAll() {
        return ResponseEntity.ok(memberService.getAll().stream().map(MemberMapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(MemberMapper.toResponse(memberService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> create(@Valid @RequestBody MemberRequest request) {
        MemberResponse response = MemberMapper.toResponse(memberService.create(MemberMapper.toDomain(UUID.randomUUID(), request)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> update(@PathVariable UUID id, @Valid @RequestBody MemberRequest request) {
        return ResponseEntity.ok(MemberMapper.toResponse(memberService.update(id, MemberMapper.toDomain(id, request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
